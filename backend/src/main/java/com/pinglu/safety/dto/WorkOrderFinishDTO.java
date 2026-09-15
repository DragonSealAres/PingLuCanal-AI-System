package com.pinglu.safety.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Finish work order request")
public class WorkOrderFinishDTO {

    @NotBlank(message = "处理说明不能为空")
    @Size(max = 1000, message = "处理说明长度不能超过1000")
    @Schema(description = "Handle remark", example = "已完成航道漂浮物清理")
    private String handleRemark;

    @Size(max = 500, message = "处理完成图片地址长度不能超过500")
    @Schema(description = "Handle completion image", example = "/uploads/2026/09/09/finish.jpg")
    private String handleImage;
}
