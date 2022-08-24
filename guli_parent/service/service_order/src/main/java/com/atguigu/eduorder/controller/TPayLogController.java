package com.atguigu.eduorder.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduorder.service.TPayLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 支付日志表 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2022-08-13
 */
@RestController
@RequestMapping("/eduorder/paylog")
@CrossOrigin
public class TPayLogController {


    @Autowired
    private TPayLogService payLogService;


    //订单号
    @GetMapping("createNative/{orderNo}")
    public R createNative(@PathVariable String orderNo){
        //返回信息.包含二维码地址,还有其他信息
        Map map=payLogService.createNatvie(orderNo);
        return R.ok().data(map);
    }

    //查询订单支付状态
    @GetMapping("queryPayStatus/{orderNo}")
    public R queryPayStatus(@PathVariable String orderNo){
        Map<String,String> map=payLogService.queryPayStatus(orderNo);
        if (map == null){
            return R.error().message("支付出错了");

        }
        //如果返回map里面不为空,通过map放获取订单状态
        if (map.get("trade_state").equals("SUCCESS")){
            //支付成功后,添加到支付表
            payLogService.updateOrderStatus(map);
            return R.ok();
        }
        return  R.ok().message("支付中");

    }
}

