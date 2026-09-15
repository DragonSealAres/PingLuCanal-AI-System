package com.pinglu.safety.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Create work order request")
public class WorkOrderCreateDTO {

    @NotNull(message = "隐患ID不能为空")
    @Schema(description = "Related hazard report ID", example = "1")
    private Long hazardId;

    @NotBlank(message = "工单标题不能为空")
    @Size(max = 200, message = "工单标题长度不能超过200")
    @Schema(description = "Work order title", example = "处理航道漂浮物")
    private String title;

    @NotBlank(message = "处理人员不能为空")
    @Size(max = 100, message = "处理人员长度不能超过100")
    @Schema(description = "Handler name", example = "维修人员张三")
    private String handler;

    @Size(max = 1000, message = "处置要求长度不能超过1000")
    @Schema(description = "Handle requirement", example = "请尽快清理该航段漂浮物")
    private String requirement;
}
