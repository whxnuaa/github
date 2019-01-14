package com.jit.skiad.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.jit.skiad.domain.CommondataDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface DataMapper extends BaseMapper<CommondataDO> {

    //获取最新的水位数据
    @Select("select * from commondata where report_time = (select max(report_time) from commondata where gw_id=#{gwId})")
    public CommondataDO getCurrentData(@Param("gwId")Integer gwId);
}
