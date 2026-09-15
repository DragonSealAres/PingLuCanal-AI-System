package com.pinglu.safety.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("hazard_report")
@Schema(description = "Hazard report")
public class HazardReport {

    @TableId(type = IdType.AUTO)
    @Schema(description = "Primary key")
    private Long id;

    @Schema(description = "Hazard report number", example = "PL-20260909-0001")
    private String reportNo;

    @Schema(description = "Reporter name")
    private String reportUser;

    @Schema(description = "Hazard location")
    private String location;

    @Schema(description = "Longitude")
    private BigDecimal longitude;

    @Schema(description = "Latitude")
    private BigDecimal latitude;

    @Schema(description = "Hazard image URL")
    private String imageUrl;

    @Schema(description = "Hazard type")
    private String hazardType;

    @Schema(description = "Risk level")
    private String riskLevel;

    @Schema(description = "Hazard description")
    private String description;

    @Schema(description = "AI analysis result")
    private String aiResult;

    @Schema(description = "Current status", example = "待审核")
    private String status;

    @Schema(description = "Create time")
    private LocalDateTime createTime;

    @Schema(description = "Update time")
    private LocalDateTime updateTime;
}
