package com.pinglu.safety.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pinglu.safety.dto.WorkOrderCreateDTO;
import com.pinglu.safety.dto.WorkOrderFinishDTO;
import com.pinglu.safety.entity.WorkOrder;

public interface WorkOrderService extends IService<WorkOrder> {

    WorkOrder createWorkOrder(WorkOrderCreateDTO createDTO);

    WorkOrder getWorkOrderById(Long id);

    WorkOrder startWorkOrder(Long id);

    WorkOrder finishWorkOrder(Long id, WorkOrderFinishDTO finishDTO);

    WorkOrder approveWorkOrder(Long id);
}
