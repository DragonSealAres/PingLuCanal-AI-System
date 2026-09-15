package com.pinglu.safety.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Approve hazard report request")
public class HazardReportApproveDTO {

    @NotBlank(message = "隐患类型不能为空")
    @Size(max = 50, message = "隐患类型长度不能超过50")
    private String hazardType;

    @NotBlank(message = "风险等级不能为空")
    @Size(max = 20, message = "风险等级长度不能超过20")
    private String riskLevel;

    @NotBlank(message = "隐患描述不能为空")
    @Size(max = 1000, message = "隐患描述长度不能超过1000")
    private String description;
}
