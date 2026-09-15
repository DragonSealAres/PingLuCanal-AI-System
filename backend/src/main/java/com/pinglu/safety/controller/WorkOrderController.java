package com.pinglu.safety.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pinglu.safety.common.Result;
import com.pinglu.safety.dto.WorkOrderCreateDTO;
import com.pinglu.safety.dto.WorkOrderFinishDTO;
import com.pinglu.safety.entity.WorkOrder;
import com.pinglu.safety.service.WorkOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;

import java.util.List;

@Tag(name = "Work Order")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @Operation(summary = "Create work order by hazard report")
    @PostMapping("/create")
    public Result<WorkOrder> create(@Valid @RequestBody WorkOrderCreateDTO createDTO) {
        return Result.success(workOrderService.createWorkOrder(createDTO));
    }

    @Operation(summary = "List work orders")
    @GetMapping
    public Result<List<WorkOrder>> list(@RequestParam(value = "handler", required = false) String handler) {
        List<WorkOrder> workOrders = workOrderService.list(
                Wrappers.<WorkOrder>lambdaQuery()
                        .eq(StringUtils.hasText(handler), WorkOrder::getHandler, handler)
                        .orderByDesc(WorkOrder::getCreateTime)
        );
        return Result.success(workOrders);
    }

    @Operation(summary = "Get work order detail")
    @GetMapping("/{id}")
    public Result<WorkOrder> detail(@PathVariable Long id) {
        return Result.success(workOrderService.getWorkOrderById(id));
    }

    @Operation(summary = "Start work order")
    @PutMapping("/{id}/start")
    public Result<WorkOrder> start(@PathVariable Long id) {
        return Result.success(workOrderService.startWorkOrder(id));
    }

    @Operation(summary = "Submit work order handle result")
    @PutMapping("/{id}/finish")
    public Result<WorkOrder> finish(@PathVariable Long id, @Valid @RequestBody WorkOrderFinishDTO finishDTO) {
        return Result.success(workOrderService.finishWorkOrder(id, finishDTO));
    }

    @Operation(summary = "Approve work order")
    @PutMapping("/{id}/approve")
    public Result<WorkOrder> approve(@PathVariable Long id) {
        return Result.success(workOrderService.approveWorkOrder(id));
    }
}
