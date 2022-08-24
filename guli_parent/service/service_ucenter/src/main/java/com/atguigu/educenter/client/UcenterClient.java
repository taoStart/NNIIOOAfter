package com.atguigu.educenter.client;

import com.atguigu.commonutils.R;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface UcenterClient {
    //根据用户id获取用户信息
    @GetMapping("/ucenterservice/member/getUcenterPay/{memberId}")
    public R getUcenterPay(@PathVariable("memberId") String memberId);
}



