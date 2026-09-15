package com.pinglu.safety.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("work_order")
@Schema(description = "Work order")
public class WorkOrder {

    @TableId(type = IdType.AUTO)
    @Schema(description = "Primary key")
    private Long id;

    @Schema(description = "Work order number", example = "PL-WO-20260909-0001")
    private String orderNo;

    @Schema(description = "Related hazard report ID", example = "1")
    private Long hazardId;

    @Schema(description = "Work order title", example = "处理航道漂浮物")
    private String title;

    @Schema(description = "Handler name", example = "维修人员张三")
    private String handler;

    @Schema(description = "Handle requirement")
    private String requirement;

    @Schema(description = "Work order status", example = "待派单")
    private String status;

    @Schema(description = "Handle remark")
    private String handleRemark;

    @Schema(description = "Handle completion image")
    private String handleImage;

    @Schema(description = "Create time")
    private LocalDateTime createTime;

    @Schema(description = "Update time")
    private LocalDateTime updateTime;
}
