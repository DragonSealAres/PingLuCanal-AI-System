package com.pinglu.safety.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pinglu.safety.dto.HazardReportApproveDTO;
import com.pinglu.safety.dto.HazardReportCreateDTO;
import com.pinglu.safety.entity.HazardReport;
import com.pinglu.safety.vo.AiHazardReportVO;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public interface HazardReportService extends IService<HazardReport> {

    HazardReport createHazardReport(HazardReportCreateDTO createDTO);

    HazardReport getHazardReportById(Long id);

    HazardReport approveHazardReport(Long id, HazardReportApproveDTO approveDTO);

    HazardReport updateStatus(Long id, String status);

    AiHazardReportVO createAiHazardReport(MultipartFile file,
                                          String reportUser,
                                          String location,
                                          BigDecimal longitude,
                                          BigDecimal latitude);
}
