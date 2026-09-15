package com.pinglu.safety.ai.service;

import com.pinglu.safety.ai.vo.AiAnalyzeResultVO;

public interface AiVisionService {

    AiAnalyzeResultVO analyzeImage(String imageUrl);
}
