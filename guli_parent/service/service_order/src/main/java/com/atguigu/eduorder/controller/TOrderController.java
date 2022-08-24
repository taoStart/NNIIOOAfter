package com.atguigu.eduorder.controller;


import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.R;
import com.atguigu.eduorder.entity.TOrder;
import com.atguigu.eduorder.service.TOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 订单 前端控制器
 * 1.生成订单接口
 * 2.根据id查询订单信息
 * 3.生成微信支付二维码
 * 4.查询订单支付状态接口
 *
 *
 *
 * </p>
 *
 * @author testjava
 * @since 2022-08-13
 */
@RestController
@RequestMapping("/eduorder/order")
@CrossOrigin
public class TOrderController {

    @Autowired
    private TOrderService orderService;

    //1.生成订单
    @PostMapping("createrOrder/{courseId}")
    public R saveOrder(@PathVariable String courseId, HttpServletRequest request){
        String memberIdByJwtToken = JwtUtils.getMemberIdByJwtToken(request);
        //创建了订单表,返回订单号
        String orderId=orderService.createrOrder(courseId,memberIdByJwtToken);

        return R.ok().data("orderId",orderId);
    }

    //2.生成的订单号,把订单查询出来
    @GetMapping("getOrderInfo/{orderId}")
    public R getOrderInfo(@PathVariable String orderId){
        QueryWrapper<TOrder> wrapper=new QueryWrapper<>();
        wrapper.eq("order_no",orderId);
        TOrder order=orderService.getOne(wrapper);
        return R.ok().data("item",order);
    }
}

