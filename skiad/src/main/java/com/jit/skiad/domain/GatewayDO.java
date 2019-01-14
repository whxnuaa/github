package com.jit.skiad.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@TableName("gateway")
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class GatewayDO {
    private Integer id;//网关id
    private String gwName;//网关名称
    private Float waterLevel;//水位
    private String operator;//操作员
}
