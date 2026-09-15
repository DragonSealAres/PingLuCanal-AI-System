package com.pinglu.safety.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pinglu.safety.entity.WorkOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {

    @Select("SELECT order_no FROM work_order WHERE order_no LIKE CONCAT(#{prefix}, '%') ORDER BY order_no DESC LIMIT 1")
    String selectLatestOrderNoByPrefix(@Param("prefix") String prefix);

    @Select("""
            SELECT COUNT(*)
            FROM work_order
            WHERE hazard_id = #{hazardId}
              AND status IN ('待派单', '处理中', '待复核')
            """)
    long countActiveByHazardId(@Param("hazardId") Long hazardId);
}
