package com.pinglu.safety.vo;

import com.pinglu.safety.ai.vo.AiAnalyzeResultVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI智能隐患上报结果")
public class AiHazardReportVO {

    @Schema(description = "隐患记录ID", example = "1")
    private Long reportId;

    @Schema(description = "上传图片URL")
    private String imageUrl;

    @Schema(description = "AI识别结果")
    private AiAnalyzeResultVO aiResult;

    @Schema(description = "当前状态", example = "待审核")
    private String status;
}
