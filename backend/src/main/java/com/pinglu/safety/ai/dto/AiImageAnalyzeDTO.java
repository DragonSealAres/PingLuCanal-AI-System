package com.pinglu.safety.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "AI图片分析请求")
public class AiImageAnalyzeDTO {

    @NotBlank(message = "图片地址不能为空")
    @Schema(description = "已上传图片URL", example = "/uploads/2026/09/09/test.jpg")
    private String imageUrl;
}
