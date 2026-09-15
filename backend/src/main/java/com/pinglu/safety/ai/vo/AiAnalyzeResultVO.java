package com.pinglu.safety.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI图片隐患识别结果")
public class AiAnalyzeResultVO {

    @Schema(description = "是否存在安全隐患", example = "true")
    private Boolean hasHazard;

    @Schema(description = "隐患类型", example = "漂浮物")
    private String hazardType;

    @Schema(description = "风险等级", example = "中风险")
    private String riskLevel;

    @Schema(description = "置信度，范围0到1", example = "0.92")
    private Double confidence;

    @Schema(description = "隐患描述", example = "航道水面发现较大漂浮树枝")
    private String description;

    @Schema(description = "处理建议", example = "建议安排巡检人员及时清理")
    private String suggestion;
}
