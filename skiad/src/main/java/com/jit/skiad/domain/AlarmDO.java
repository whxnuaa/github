package com.jit.skiad.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("alarm")
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class AlarmDO {
    private Integer id;
    private Integer gwId;//网关id
    private String reason;//原因
    private String operator;//操作员
    private Date time;//时间
    private String remark;//备注

}
