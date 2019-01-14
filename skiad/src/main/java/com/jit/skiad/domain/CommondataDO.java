package com.jit.skiad.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("commondata")
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor(staticName = "of")
public class CommondataDO {
    private Long id;
    private Integer gwId;
    private Integer addr;
    private String sensor_type;
    private Float dataValue;
    private Date reportTime;
}
