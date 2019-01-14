package com.jit.skiad.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@TableName("gw_user")
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class GwUserDO {
    private Integer id;
    private Integer gwId;
    private Integer userId;
    private Integer useFlag;
}
