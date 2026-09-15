package com.pinglu.safety.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinglu.safety.ai.service.AiVisionService;
import com.pinglu.safety.ai.vo.AiAnalyzeResultVO;
import com.pinglu.safety.dto.HazardReportApproveDTO;
import com.pinglu.safety.dto.HazardReportCreateDTO;
import com.pinglu.safety.entity.HazardReport;
import com.pinglu.safety.exception.BusinessException;
import com.pinglu.safety.mapper.HazardReportMapper;
import com.pinglu.safety.service.FileService;
import com.pinglu.safety.service.HazardReportService;
import com.pinglu.safety.vo.AiHazardReportVO;
import com.pinglu.safety.vo.FileUploadVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class HazardReportServiceImpl extends ServiceImpl<HazardReportMapper, HazardReport>
        implements HazardReportService {

    private static final String DEFAULT_STATUS = "待审核";
    private static final String STATUS_APPROVED = "已审核";
    private static final String STATUS_PROCESSING = "处理中";
    private static final String STATUS_WAIT_REVIEW = "待复核";
    private static final String STATUS_DONE = "已完成";
    private static final Set<String> HAZARD_TYPES = Set.of(
            "漂浮物", "航道障碍物", "船舶异常", "航标异常",
            "水面污染", "岸线异常", "非法占道", "其他"
    );
    private static final Set<String> RISK_LEVELS = Set.of("低风险", "中风险", "高风险");
    private static final DateTimeFormatter REPORT_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final FileService fileService;
    private final AiVisionService aiVisionService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized HazardReport createHazardReport(HazardReportCreateDTO createDTO) {
        LocalDateTime now = LocalDateTime.now();

        HazardReport report = new HazardReport();
        report.setReportNo(generateReportNo());
        report.setReportUser(createDTO.getReportUser());
        report.setLocation(createDTO.getLocation());
        report.setLongitude(createDTO.getLongitude());
        report.setLatitude(createDTO.getLatitude());
        report.setImageUrl(createDTO.getImageUrl());
        report.setHazardType(createDTO.getHazardType());
        report.setRiskLevel(createDTO.getRiskLevel());
        report.setDescription(createDTO.getDescription());
        report.setAiResult(createDTO.getAiResult());
        report.setStatus(DEFAULT_STATUS);
        report.setCreateTime(now);
        report.setUpdateTime(now);

        saveReport(report);
        return report;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized AiHazardReportVO createAiHazardReport(org.springframework.web.multipart.MultipartFile file,
                                                              String reportUser,
                                                              String location,
                                                              BigDecimal longitude,
                                                              BigDecimal latitude) {
        FileUploadVO uploadVO = fileService.uploadImage(file);
        AiAnalyzeResultVO aiResult = analyzeImageSafely(uploadVO.getUrl(), reportUser);
        LocalDateTime now = LocalDateTime.now();

        HazardReport report = new HazardReport();
        report.setReportNo(generateReportNo());
        report.setReportUser(reportUser);
        report.setLocation(location);
        report.setLongitude(longitude);
        report.setLatitude(latitude);
        report.setImageUrl(uploadVO.getUrl());
        report.setHazardType(aiResult.getHazardType());
        report.setRiskLevel(aiResult.getRiskLevel());
        report.setDescription(aiResult.getDescription());
        report.setAiResult(toJson(aiResult));
        report.setStatus(DEFAULT_STATUS);
        report.setCreateTime(now);
        report.setUpdateTime(now);

        saveReport(report);

        log.info("AI智能隐患上报成功 user={}, imageUrl={}, aiResult={}, time={}",
                reportUser, uploadVO.getUrl(), report.getAiResult(), now);
        return new AiHazardReportVO(report.getId(), uploadVO.getUrl(), aiResult, report.getStatus());
    }

    @Override
    public HazardReport getHazardReportById(Long id) {
        HazardReport report = getById(id);
        if (report == null) {
            throw new BusinessException(404, "隐患上报记录不存在");
        }
        return report;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HazardReport approveHazardReport(Long id, HazardReportApproveDTO approveDTO) {
        HazardReport report = getHazardReportById(id);
        if (!DEFAULT_STATUS.equals(report.getStatus())) {
            throw new BusinessException(400, "只有待审核隐患可以审核，当前状态：" + report.getStatus());
        }
        validateReviewValues(approveDTO.getHazardType(), approveDTO.getRiskLevel());

        report.setHazardType(approveDTO.getHazardType());
        report.setRiskLevel(approveDTO.getRiskLevel());
        report.setDescription(approveDTO.getDescription());
        report.setStatus(STATUS_APPROVED);
        report.setUpdateTime(LocalDateTime.now());
        saveReport(report);

        log.info("隐患审核通过 reportNo={}, hazardType={}, riskLevel={}, status={}, time={}",
                report.getReportNo(), report.getHazardType(), report.getRiskLevel(),
                report.getStatus(), report.getUpdateTime());
        return report;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HazardReport updateStatus(Long id, String status) {
        HazardReport report = getHazardReportById(id);
        if (!Set.of(STATUS_APPROVED, STATUS_PROCESSING, STATUS_WAIT_REVIEW, STATUS_DONE).contains(status)) {
            throw new BusinessException(400, "不支持的隐患状态：" + status);
        }
        report.setStatus(status);
        report.setUpdateTime(LocalDateTime.now());
        saveReport(report);
        return report;
    }

    private String generateReportNo() {
        String prefix = "PL-" + LocalDate.now().format(REPORT_DATE_FORMATTER) + "-";
        String latestReportNo = baseMapper.selectLatestReportNoByPrefix(prefix);
        int nextSequence = 1;
        if (latestReportNo != null && latestReportNo.length() >= prefix.length() + 4) {
            String latestSequence = latestReportNo.substring(prefix.length());
            nextSequence = Integer.parseInt(latestSequence) + 1;
        }
        return prefix + String.format("%04d", nextSequence);
    }

    private AiAnalyzeResultVO analyzeImageSafely(String imageUrl, String reportUser) {
        try {
            AiAnalyzeResultVO aiResult = aiVisionService.analyzeImage(imageUrl);
            log.info("AI图片识别成功 user={}, imageUrl={}, aiResult={}, time={}",
                    reportUser, imageUrl, toJson(aiResult), LocalDateTime.now());
            return aiResult;
        } catch (Exception ex) {
            log.warn("AI图片识别失败，已转入人工补充流程 user={}, imageUrl={}, time={}, reason={}",
                    reportUser, imageUrl, LocalDateTime.now(), ex.getMessage(), ex);
            return new AiAnalyzeResultVO(
                    true,
                    "其他",
                    "低风险",
                    0.0,
                    "AI识别失败，请人工补充隐患描述",
                    "请人工审核图片并补充隐患类型、风险等级和处置建议"
            );
        }
    }

    private String toJson(AiAnalyzeResultVO aiResult) {
        try {
            return objectMapper.writeValueAsString(aiResult);
        } catch (JsonProcessingException ex) {
            throw new BusinessException(500, "AI识别结果序列化失败");
        }
    }

    private void saveReport(HazardReport report) {
        try {
            saveOrUpdate(report);
        } catch (RuntimeException ex) {
            log.error("隐患记录保存失败 reportId={}, reportNo={}, status={}",
                    report.getId(), report.getReportNo(), report.getStatus(), ex);
            throw new BusinessException(500, "隐患记录保存失败，请稍后重试");
        }
    }

    private void validateReviewValues(String hazardType, String riskLevel) {
        if (!HAZARD_TYPES.contains(hazardType)) {
            throw new BusinessException(400, "隐患类型不合法");
        }
        if (!RISK_LEVELS.contains(riskLevel)) {
            throw new BusinessException(400, "风险等级不合法");
        }
    }
}
