package com.jit.skiad.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;


import java.util.Date;

@Data
@TableName("relaystatus")
public class RelayStatusDO {
    private Integer id;
    private Integer gwId;//网关
    private Integer addr485;
    private Integer relayPosition;
    private Integer relayStatus;
    private Integer relayValue;
    private Date time;

}
