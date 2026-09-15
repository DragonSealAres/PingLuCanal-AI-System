package com.pinglu.safety.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Create hazard report request")
public class HazardReportCreateDTO {

    @NotBlank(message = "上报人不能为空")
    @Size(max = 100, message = "上报人长度不能超过100")
    @Schema(description = "Reporter name", example = "巡检员张三")
    private String reportUser;

    @NotBlank(message = "隐患位置不能为空")
    @Size(max = 255, message = "隐患位置长度不能超过255")
    @Schema(description = "Hazard location", example = "平陆运河XX航段")
    private String location;

    @NotNull(message = "经度不能为空")
    @DecimalMin(value = "-180.0", message = "经度不能小于-180")
    @DecimalMax(value = "180.0", message = "经度不能大于180")
    @Schema(description = "Longitude", example = "108.123456")
    private BigDecimal longitude;

    @NotNull(message = "纬度不能为空")
    @DecimalMin(value = "-90.0", message = "纬度不能小于-90")
    @DecimalMax(value = "90.0", message = "纬度不能大于90")
    @Schema(description = "Latitude", example = "22.123456")
    private BigDecimal latitude;

    @Size(max = 500, message = "隐患图片地址长度不能超过500")
    @Schema(description = "Hazard image URL", example = "/uploads/test.jpg")
    private String imageUrl;

    @NotBlank(message = "隐患类型不能为空")
    @Size(max = 50, message = "隐患类型长度不能超过50")
    @Schema(description = "Hazard type", example = "漂浮物")
    private String hazardType;

    @NotBlank(message = "风险等级不能为空")
    @Size(max = 20, message = "风险等级长度不能超过20")
    @Schema(description = "Risk level", example = "中风险")
    private String riskLevel;

    @NotBlank(message = "隐患描述不能为空")
    @Size(max = 1000, message = "隐患描述长度不能超过1000")
    @Schema(description = "Hazard description", example = "航道右侧发现较大漂浮树枝")
    private String description;

    @Schema(description = "AI analysis result JSON")
    private String aiResult;
}
