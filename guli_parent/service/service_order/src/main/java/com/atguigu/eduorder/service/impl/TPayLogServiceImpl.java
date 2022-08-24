package com.atguigu.eduorder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.eduorder.entity.TOrder;
import com.atguigu.eduorder.entity.TPayLog;
import com.atguigu.eduorder.mapper.TPayLogMapper;
import com.atguigu.eduorder.service.TOrderService;
import com.atguigu.eduorder.service.TPayLogService;
import com.atguigu.eduorder.utils.HttpClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付日志表 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2022-08-13
 */
@Service
public class TPayLogServiceImpl extends ServiceImpl<TPayLogMapper, TPayLog> implements TPayLogService {

    @Autowired
    private TOrderService orderService;


    @Override
    public Map createNatvie(String orderNo) {
        try{
            //1.根据订单号查询订单信息
            QueryWrapper<TOrder> wrapper=new QueryWrapper<>();
            wrapper.eq("order_no",orderNo);
            TOrder order=orderService.getOne(wrapper);


            //2.使用map设置生成二维码需要参数
            Map m=new HashMap();
            //1、设置支付参数
            m.put("appid", "wx74862e0dfcf69954");//支付id,固定
            m.put("mch_id", "1558950191");//商户号,固定
            m.put("nonce_str", WXPayUtil.generateNonceStr());//工具类,生成一个随机的二维码
            m.put("body", order.getCourseTitle());//二维码的名称,这里设置了课程名称
            m.put("out_trade_no", orderNo);//订单号
            m.put("total_fee", order.getTotalFee().multiply(new BigDecimal("100")).longValue()+"");
            m.put("spbill_create_ip", "127.0.0.1");//支付地址
            m.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");//回调地址
            m.put("trade_type", "NATIVE");//支付方式类型,生成的是二维码

            //3.发送httpclient请求,传递参数
            HttpClient client=new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //4.得到发送请求返回结果
            //client设置参数
            client.setXmlParam(WXPayUtil.generateSignedXml(m, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));//微信提供的商户号
            client.setHttps(true);//支持https访问
            client.post();//执行发送请求

            //4.得到发送请求返回结果
            //返回内容,是使用xml格式返回
            String xml = client.getContent();

            //把xml格式转换为map集合,把map集合返回
            Map<String ,String> resultMap=WXPayUtil.xmlToMap(xml);


            //封装其他信息
            Map map=new HashMap();
            map.put("out_trade_no", orderNo);
            map.put("course_id", order.getCourseId());
            map.put("total_fee", order.getTotalFee());
            map.put("result_code", resultMap.get("result_code"));
            map.put("code_url", resultMap.get("code_url"));

            //微信支付二维码2小时过期，可采取2小时未支付取消订单
            //redisTemplate.opsForValue().set(orderNo, map, 120, TimeUnit.MINUTES);
            return map;

        }catch(Exception e){
            e.printStackTrace();
            return new HashMap<>();
        }



    }

    @Override
    public Map<String, String> queryPayStatus(String orderNo) {


        try {
            //1、封装参数
            Map m = new HashMap<>();
            m.put("appid", "wx74862e0dfcf69954");
            m.put("mch_id", "1558950191");
            m.put("out_trade_no", orderNo);
            m.put("nonce_str", WXPayUtil.generateNonceStr());
            //发送http请求
            HttpClient client=new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(m, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);
            client.post();


            //3、返回第三方的数据
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //6、转成Map
            //7、返回
            return resultMap;


        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }

    }
//支付成功后的更新数据
    @Override
    public void updateOrderStatus(Map<String, String> map) {

        String orderNo=map.get("out_trade_no");
        //1.根据订单号查询订单信息
        QueryWrapper<TOrder> wrapper=new QueryWrapper<>();
        wrapper.eq("order_no",orderNo);
        TOrder order=orderService.getOne(wrapper);
        //更新状态
        if(order.getStatus().intValue()==1){return;}
        order.setStatus(1);
        orderService.updateById(order);



        //记录支付日志
        TPayLog payLog=new TPayLog();
        payLog.setOrderNo(order.getOrderNo());//支付订单号
        payLog.setPayTime(new Date());
        payLog.setPayType(1);//支付类型
        payLog.setTotalFee(order.getTotalFee());//总金额(分)
        payLog.setTradeState(map.get("trade_state"));//支付状态
        payLog.setTransactionId(map.get("transaction_id"));
        payLog.setAttr(JSONObject.toJSONString(map));
        baseMapper.insert(payLog);//插入到支付日志表

    }
}
