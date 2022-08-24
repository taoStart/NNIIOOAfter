package com.atguigu.staservice.schedule;

import com.atguigu.staservice.service.StatisticsDailyService;
import com.atguigu.staservice.utils.DateUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class ScheduledTask {

    @Autowired
    private StatisticsDailyService staService;

//    @Scheduled(cron = "0/5 * * * * ?")
//    public void task(){
//        System.out.println("定时任务_五秒一执行");
//    }


    @Scheduled(cron = "0 0 23 * * ?")
    public void task2(){
        staService.registerCount(DateUtil.formatDate(DateUtil.addDays(new Date(), -1)));
    }
}
