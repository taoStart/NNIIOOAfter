package com.atguigu.educenter.service.impl;

import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.MD5;
import com.atguigu.commonutils.R;
import com.atguigu.educenter.entity.UcenterMember;
import com.atguigu.educenter.entity.vo.RegisterVo;
import com.atguigu.educenter.mapper.UcenterMemberMapper;
import com.atguigu.educenter.service.UcenterMemberService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2022-08-08
 */
@Service
public class UcenterMemberServiceImpl extends ServiceImpl<UcenterMemberMapper, UcenterMember> implements UcenterMemberService {


//    @Autowired
//    private RedisTemplate<String,String> redisTemplate;


    @Override
    public String login(UcenterMember ucenter) {
     //获取手机号和密码
        String mobile=ucenter.getMobile();
        String password= MD5.encrypt(ucenter.getPassword());

        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
            throw new GuliException(20001, "手机号或密码为空,请输入正确手机号和密码");
        }
        //判断手机号是否正确
        QueryWrapper<UcenterMember> wrapper=new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        UcenterMember mobileselect = baseMapper.selectOne(wrapper);

        if(mobileselect==null){//查出来没有手机号
            //异常
            throw new GuliException(20001,"没有此用户,点击注册进行注册,或者手机号输入错误");
        }

        if(!password.equals(mobileselect.getPassword())){
            throw new GuliException(20001,"密码输入错误");
        }
        //判断用户是否被禁用
        if(mobileselect.getIsDisabled()){
            throw new GuliException(20001,"您因违反了用户守则,请联系管理员进行申诉");
        }
        //登录成功
        //使用token(jwt)生成字符串
        String jwtToken = JwtUtils.getJwtToken(mobileselect.getId(), mobileselect.getNickname());
        return jwtToken;

    }
//注册实现
    @Override
    public void register(RegisterVo registerVo) {
        //获取注册的数据
        String mobile = registerVo.getMobile().trim();
        String password = MD5.encrypt(registerVo.getPassword()).trim();
        String nickname = registerVo.getNickname().trim();
//        String code = registerVo.getCode();


        //判断非空
        if(StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)||StringUtils.isEmpty(nickname)){
            throw new GuliException(20001,"注册有空值");
        }

        //判断验证码
        /*
        获取验证码

        String redisCode=redisTemplate.opsForValue().get(mobile);
        if (!code.equals(redisCode)){
            throw new GuliException(20001,"验证码错误");

        }
        */




        //判断手机号是否重复
        QueryWrapper<UcenterMember> wrapper=new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        Integer selectCount = baseMapper.selectCount(wrapper);
        if (selectCount>0){
            throw new GuliException(20001,"该号码完成注册,已注册");
        }else {


            //添加到数据库
            UcenterMember member = new UcenterMember();
            member.setMobile(mobile);
            member.setNickname(nickname);
            member.setPassword(password);
            member.setIsDisabled(false);
            member.setAvatar("https://makesi.oss-cn-chengdu.aliyuncs.com/2022/06/14/user.png");
            baseMapper.insert(member);
        }
    }
//
//查询某一天注册人数
    @Override
    public Integer countRegisterDay(String day) {



        return baseMapper.countRegisterDay(day);
    }
}
