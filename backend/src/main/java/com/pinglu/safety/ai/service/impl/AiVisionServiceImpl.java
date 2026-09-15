package com.pinglu.safety.ai.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinglu.safety.ai.config.AiProperties;
import com.pinglu.safety.ai.prompt.AiPromptTemplate;
import com.pinglu.safety.ai.service.AiVisionService;
import com.pinglu.safety.ai.vo.AiAnalyzeResultVO;
import com.pinglu.safety.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiVisionServiceImpl implements AiVisionService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> HAZARD_TYPES = Set.of("漂浮物", "航道障碍物", "船舶异常", "航标异常", "水面污染", "岸线异常", "非法占道", "其他");
    private static final Set<String> RISK_LEVELS = Set.of("低风险", "中风险", "高风险");

    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;

    @Value("${file.upload.root-path:uploads}")
    private String uploadRootPath;

    @Override
    public AiAnalyzeResultVO analyzeImage(String imageUrl) {
        Path imagePath = resolveImagePath(imageUrl);

        if (aiProperties.isMockEnabled()) {
            return mockResult();
        }

        return analyzeImageByRemoteModel(imagePath);
    }

    private AiAnalyzeResultVO mockResult() {
        return new AiAnalyzeResultVO(
                true,
                "漂浮物",
                "中风险",
                0.92,
                "航道水面发现较大漂浮树枝",
                "建议安排巡检人员及时清理"
        );
    }

    private AiAnalyzeResultVO analyzeImageByRemoteModel(Path imagePath) {
        if (!StringUtils.hasText(aiProperties.getApiKey())) {
            throw new BusinessException(500, "AI服务未配置API Key，请设置环境变量AI_API_KEY或启用Mock模式");
        }

        try {
            String responseBody = sendAiRequest(imagePath);
            String content = extractAiContent(responseBody);
            AiAnalyzeResultVO result = parseAiResult(content);
            validateAiResult(result);
            return result;
        } catch (BusinessException ex) {
            throw ex;
        } catch (java.net.http.HttpTimeoutException ex) {
            throw new BusinessException(500, "AI请求超时，请稍后重试");
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new BusinessException(500, "AI请求失败，请稍后重试");
        }
    }

    private String sendAiRequest(Path imagePath) throws IOException, InterruptedException {
        String endpoint = normalizeBaseUrl(aiProperties.getBaseUrl()) + "/chat/completions";
        String dataUrl = toImageDataUrl(imagePath);
        Map<String, Object> requestBody = Map.of(
                "model", aiProperties.getModel(),
                "temperature", 0.2,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", AiPromptTemplate.HAZARD_IMAGE_ANALYSIS_PROMPT
                        ),
                        Map.of(
                                "role", "user",
                                "content", List.of(
                                        Map.of("type", "text", "text", "请分析这张平陆运河航道巡检图片。"),
                                        Map.of("type", "image_url", "image_url", Map.of("url", dataUrl))
                                )
                        )
                )
        );

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(aiProperties.getTimeoutSeconds()))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofSeconds(aiProperties.getTimeoutSeconds()))
                .header("Authorization", "Bearer " + aiProperties.getApiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            String upstreamBody = response.body() == null ? "" : response.body().trim();
            log.error("AI upstream request failed: endpoint={}, model={}, status={}, response={}",
                    endpoint, aiProperties.getModel(), response.statusCode(), abbreviate(upstreamBody));
            throw new BusinessException(500, buildRemoteErrorMessage(response.statusCode()));
        }
        return response.body();
    }

    private String buildRemoteErrorMessage(int statusCode) {
        return switch (statusCode) {
            case 401 -> "AI请求失败：API Key无效或已过期";
            case 403 -> "AI请求失败：当前API Key或业务空间没有模型推理权限";
            case 404 -> "AI请求失败：接口地址或模型不存在";
            case 429 -> "AI请求失败：调用频率或额度已超过限制";
            default -> statusCode >= 500
                    ? "AI服务暂时不可用，请稍后重试"
                    : "AI请求失败，状态码：" + statusCode;
        };
    }

    private String abbreviate(String value) {
        if (value.length() <= 2000) {
            return value;
        }
        return value.substring(0, 2000) + "...";
    }

    private Path resolveImagePath(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            throw new BusinessException(400, "图片地址不能为空");
        }
        if (!imageUrl.startsWith("/uploads/")) {
            throw new BusinessException(400, "图片地址必须以/uploads/开头");
        }

        String relativePath = imageUrl.substring("/uploads/".length()).replace("\\", "/");
        if (relativePath.contains("..")) {
            throw new BusinessException(400, "图片地址不合法");
        }

        String extension = getExtension(relativePath);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(400, "图片类型不合法，仅支持jpg、jpeg、png、webp");
        }

        Path uploadRoot = Paths.get(uploadRootPath).toAbsolutePath().normalize();
        Path imagePath = uploadRoot.resolve(relativePath).normalize();
        if (!imagePath.startsWith(uploadRoot) || !Files.isRegularFile(imagePath)) {
            throw new BusinessException(404, "图片不存在");
        }
        return imagePath;
    }

    private String extractAiContent(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            if (!content.isTextual() || !StringUtils.hasText(content.asText())) {
                throw new BusinessException(500, "AI返回内容为空");
            }
            return content.asText();
        } catch (JsonProcessingException ex) {
            throw new BusinessException(500, "AI返回内容不是合法JSON");
        }
    }

    private AiAnalyzeResultVO parseAiResult(String content) {
        try {
            String json = content == null ? "" : content.trim();
            if (json.startsWith("```")) {
                json = json.replaceFirst("^```(?:json)?\\s*", "")
                        .replaceFirst("\\s*```$", "")
                        .trim();
            }
            AiAnalyzeResultVO result = objectMapper.readValue(json, AiAnalyzeResultVO.class);
            validateAiResult(result);
            return result;
        } catch (JsonProcessingException ex) {
            throw new BusinessException(500, "AI返回内容不是合法JSON");
        }
    }

    private void validateAiResult(AiAnalyzeResultVO result) {
        if (result == null || result.getHasHazard() == null) {
            throw new BusinessException(500, "AI返回内容缺少必要字段");
        }
        if (!HAZARD_TYPES.contains(result.getHazardType())) {
            throw new BusinessException(500, "AI返回的隐患类型不合法");
        }
        if (!RISK_LEVELS.contains(result.getRiskLevel())) {
            throw new BusinessException(500, "AI返回的风险等级不合法");
        }
        Double confidence = result.getConfidence();
        if (confidence == null || confidence < 0 || confidence > 1) {
            throw new BusinessException(500, "AI返回的置信度不合法");
        }
        if (!StringUtils.hasText(result.getDescription()) || !StringUtils.hasText(result.getSuggestion())) {
            throw new BusinessException(500, "AI返回内容缺少描述或处置建议");
        }
    }

    private String toImageDataUrl(Path imagePath) throws IOException {
        String extension = getExtension(imagePath.getFileName().toString());
        String mediaType = "jpg".equals(extension) ? "jpeg" : extension;
        String base64 = Base64.getEncoder().encodeToString(Files.readAllBytes(imagePath));
        return "data:image/" + mediaType + ";base64," + base64;
    }

    private String normalizeBaseUrl(String baseUrl) {
        if (!StringUtils.hasText(baseUrl)) {
            throw new BusinessException(500, "AI服务地址未配置");
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private String getExtension(String filename) {
        String cleanFilename = StringUtils.cleanPath(filename == null ? "" : filename);
        int dotIndex = cleanFilename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == cleanFilename.length() - 1) {
            return "";
        }
        return cleanFilename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
