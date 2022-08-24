package com.atguigu.eduservice.client;

import com.atguigu.commonutils.R;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VodFileDegradeFeignClient implements VodClient {
    @Override
    public R removeAlyVideo(String id) {
        return R.error().message("熔断器触发,超时请求");
    }

    @Override
    public R deleteBatch(List<String> videoIdList) {
        return R.error().message("熔断器触发,超时请求");
    }
}
