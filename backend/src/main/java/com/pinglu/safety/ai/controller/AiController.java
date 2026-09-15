package com.pinglu.safety.ai.controller;

import com.pinglu.safety.ai.config.AiProperties;
import com.pinglu.safety.ai.dto.AiImageAnalyzeDTO;
import com.pinglu.safety.ai.service.AiVisionService;
import com.pinglu.safety.ai.vo.AiAnalyzeResultVO;
import com.pinglu.safety.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "AI图片识别")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {

    private final AiVisionService aiVisionService;
    private final AiProperties aiProperties;

    @Operation(summary = "AI图片隐患识别")
    @PostMapping("/analyze-image")
    public Result<AiAnalyzeResultVO> analyzeImage(@Valid @RequestBody AiImageAnalyzeDTO analyzeDTO) {
        return Result.success(aiVisionService.analyzeImage(analyzeDTO.getImageUrl()));
    }

    @Operation(summary = "检查AI API Key是否已配置")
    @GetMapping("/config-test")
    public Result<Map<String, Boolean>> configTest() {
        return Result.success(Map.of("apiConfigured", StringUtils.hasText(aiProperties.getApiKey())));
    }
}
