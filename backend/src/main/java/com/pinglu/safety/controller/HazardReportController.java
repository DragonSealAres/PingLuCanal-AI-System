package com.pinglu.safety.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pinglu.safety.common.Result;
import com.pinglu.safety.dto.HazardReportApproveDTO;
import com.pinglu.safety.dto.HazardReportCreateDTO;
import com.pinglu.safety.entity.HazardReport;
import com.pinglu.safety.service.HazardReportService;
import com.pinglu.safety.vo.AiHazardReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Hazard Report")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hazards")
public class HazardReportController {

    private final HazardReportService hazardReportService;

    @Operation(summary = "Create a hazard report")
    @PostMapping
    public Result<HazardReport> create(@Valid @RequestBody HazardReportCreateDTO createDTO) {
        return Result.success(hazardReportService.createHazardReport(createDTO));
    }

    @Operation(summary = "Create a hazard report by uploaded image and AI analysis")
    @PostMapping("/ai-report")
    public Result<AiHazardReportVO> createAiReport(@RequestParam("file") MultipartFile file,
                                                   @NotBlank(message = "上报人不能为空") @RequestParam String reportUser,
                                                   @NotBlank(message = "隐患位置不能为空") @RequestParam String location,
                                                   @NotNull(message = "经度不能为空")
                                                   @DecimalMin(value = "-180.0", message = "经度不能小于-180")
                                                   @DecimalMax(value = "180.0", message = "经度不能大于180")
                                                   @RequestParam BigDecimal longitude,
                                                   @NotNull(message = "纬度不能为空")
                                                   @DecimalMin(value = "-90.0", message = "纬度不能小于-90")
                                                   @DecimalMax(value = "90.0", message = "纬度不能大于90")
                                                   @RequestParam BigDecimal latitude) {
        return Result.success(hazardReportService.createAiHazardReport(file, reportUser, location, longitude, latitude));
    }

    @Operation(summary = "List hazard reports")
    @GetMapping
    public Result<List<HazardReport>> list() {
        List<HazardReport> reports = hazardReportService.list(
                Wrappers.<HazardReport>lambdaQuery().orderByDesc(HazardReport::getCreateTime)
        );
        return Result.success(reports);
    }

    @Operation(summary = "Get hazard report detail")
    @GetMapping("/{id}")
    public Result<HazardReport> detail(@PathVariable Long id) {
        return Result.success(hazardReportService.getHazardReportById(id));
    }

    @Operation(summary = "Approve hazard report")
    @PutMapping("/{id}/approve")
    public Result<HazardReport> approve(@PathVariable Long id,
                                        @Valid @RequestBody HazardReportApproveDTO approveDTO) {
        return Result.success(hazardReportService.approveHazardReport(id, approveDTO));
    }
}
