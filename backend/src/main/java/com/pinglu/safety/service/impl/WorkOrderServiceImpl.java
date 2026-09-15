package com.pinglu.safety.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pinglu.safety.dto.WorkOrderCreateDTO;
import com.pinglu.safety.dto.WorkOrderFinishDTO;
import com.pinglu.safety.entity.HazardReport;
import com.pinglu.safety.entity.WorkOrder;
import com.pinglu.safety.exception.BusinessException;
import com.pinglu.safety.mapper.WorkOrderMapper;
import com.pinglu.safety.service.HazardReportService;
import com.pinglu.safety.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder>
        implements WorkOrderService {

    private static final String STATUS_WAIT_ASSIGN = "待派单";
    private static final String STATUS_HAZARD_APPROVED = "已审核";
    private static final String STATUS_HAZARD_PROCESSING = "处理中";
    private static final String STATUS_HAZARD_WAIT_REVIEW = "待复核";
    private static final String STATUS_HAZARD_DONE = "已完成";
    private static final String STATUS_PROCESSING = "处理中";
    private static final String STATUS_WAIT_REVIEW = "待复核";
    private static final String STATUS_DONE = "已完成";
    private static final DateTimeFormatter ORDER_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final HazardReportService hazardReportService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized WorkOrder createWorkOrder(WorkOrderCreateDTO createDTO) {
        HazardReport hazardReport = hazardReportService.getHazardReportById(createDTO.getHazardId());
        if (!STATUS_HAZARD_APPROVED.equals(hazardReport.getStatus())) {
            throw new BusinessException(400, "只有已审核隐患可以生成工单，当前状态：" + hazardReport.getStatus());
        }
        if (baseMapper.countActiveByHazardId(hazardReport.getId()) > 0) {
            throw new BusinessException(400, "该隐患已有未关闭工单，不能重复生成");
        }
        LocalDateTime now = LocalDateTime.now();

        WorkOrder workOrder = new WorkOrder();
        workOrder.setOrderNo(generateOrderNo());
        workOrder.setHazardId(hazardReport.getId());
        workOrder.setTitle(createDTO.getTitle());
        workOrder.setHandler(createDTO.getHandler());
        workOrder.setRequirement(createDTO.getRequirement());
        workOrder.setStatus(STATUS_WAIT_ASSIGN);
        workOrder.setCreateTime(now);
        workOrder.setUpdateTime(now);

        saveWorkOrder(workOrder);
        hazardReportService.updateStatus(hazardReport.getId(), STATUS_HAZARD_PROCESSING);
        log.info("工单创建成功 orderNo={}, hazardId={}, handler={}, status={}, time={}",
                workOrder.getOrderNo(), workOrder.getHazardId(), workOrder.getHandler(), workOrder.getStatus(), now);
        return workOrder;
    }

    @Override
    public WorkOrder getWorkOrderById(Long id) {
        WorkOrder workOrder = getById(id);
        if (workOrder == null) {
            throw new BusinessException(404, "工单不存在");
        }
        return workOrder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrder startWorkOrder(Long id) {
        WorkOrder workOrder = getWorkOrderById(id);
        assertStatus(workOrder, STATUS_WAIT_ASSIGN, "只有待派单工单可以开始处理");
        updateStatus(workOrder, STATUS_PROCESSING);
        log.info("工单开始处理 orderNo={}, handler={}, status={}, time={}",
                workOrder.getOrderNo(), workOrder.getHandler(), workOrder.getStatus(), workOrder.getUpdateTime());
        return workOrder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrder finishWorkOrder(Long id, WorkOrderFinishDTO finishDTO) {
        WorkOrder workOrder = getWorkOrderById(id);
        assertStatus(workOrder, STATUS_PROCESSING, "只有处理中工单可以提交处理结果");
        workOrder.setHandleRemark(finishDTO.getHandleRemark());
        workOrder.setHandleImage(finishDTO.getHandleImage());
        updateStatus(workOrder, STATUS_WAIT_REVIEW);
        hazardReportService.updateStatus(workOrder.getHazardId(), STATUS_HAZARD_WAIT_REVIEW);
        log.info("工单提交处理结果 orderNo={}, handler={}, handleImage={}, status={}, time={}",
                workOrder.getOrderNo(), workOrder.getHandler(), workOrder.getHandleImage(),
                workOrder.getStatus(), workOrder.getUpdateTime());
        return workOrder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrder approveWorkOrder(Long id) {
        WorkOrder workOrder = getWorkOrderById(id);
        assertStatus(workOrder, STATUS_WAIT_REVIEW, "只有待复核工单可以审核完成");
        updateStatus(workOrder, STATUS_DONE);
        hazardReportService.updateStatus(workOrder.getHazardId(), STATUS_HAZARD_DONE);
        log.info("工单审核完成 orderNo={}, handler={}, status={}, time={}",
                workOrder.getOrderNo(), workOrder.getHandler(), workOrder.getStatus(), workOrder.getUpdateTime());
        return workOrder;
    }

    private void updateStatus(WorkOrder workOrder, String nextStatus) {
        workOrder.setStatus(nextStatus);
        workOrder.setUpdateTime(LocalDateTime.now());
        saveWorkOrder(workOrder);
    }

    private void assertStatus(WorkOrder workOrder, String expectedStatus, String message) {
        if (!expectedStatus.equals(workOrder.getStatus())) {
            throw new BusinessException(400, message + "，当前状态：" + workOrder.getStatus());
        }
    }

    private String generateOrderNo() {
        String prefix = "PL-WO-" + LocalDate.now().format(ORDER_DATE_FORMATTER) + "-";
        String latestOrderNo = baseMapper.selectLatestOrderNoByPrefix(prefix);
        int nextSequence = 1;
        if (latestOrderNo != null && latestOrderNo.length() >= prefix.length() + 4) {
            String latestSequence = latestOrderNo.substring(prefix.length());
            nextSequence = Integer.parseInt(latestSequence) + 1;
        }
        return prefix + String.format("%04d", nextSequence);
    }

    private void saveWorkOrder(WorkOrder workOrder) {
        try {
            saveOrUpdate(workOrder);
        } catch (RuntimeException ex) {
            throw new BusinessException(500, "工单保存失败，请稍后重试");
        }
    }
}
