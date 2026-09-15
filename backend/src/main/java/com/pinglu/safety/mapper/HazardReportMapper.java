package com.pinglu.safety.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pinglu.safety.entity.HazardReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface HazardReportMapper extends BaseMapper<HazardReport> {

    @Select("SELECT report_no FROM hazard_report WHERE report_no LIKE CONCAT(#{prefix}, '%') ORDER BY report_no DESC LIMIT 1")
    String selectLatestReportNoByPrefix(@Param("prefix") String prefix);
}
