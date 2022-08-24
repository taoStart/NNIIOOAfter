package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.entity.vo.TeacherQuery;
import com.atguigu.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 *
 * @author testjava
 * @since 2022-06-05
 */
@Api(description = "讲师管理")
@RestController
@RequestMapping("/eduservice/teacher")
@CrossOrigin
public class EduTeacherController {

    //访问地址:   http://localhost:8001/eduservice/teacher/findAll
    //把service注入
    @Autowired
    private EduTeacherService teacherService;



    //1.查询讲师表所有数据
    //rest风格
    @GetMapping("findAll")
    public R findAllTeacher(){
        List<EduTeacher> list = teacherService.list(null);
        return R.ok().data("items",list);
    }


    //2.逻辑删除讲师的方法
    @ApiOperation(value = "逻辑删除讲师列表")
    @DeleteMapping("{id}")//通过id删除
    public R removeTeacher(@PathVariable String id){//此注解是通过路径传值,并且把路径上的id传入
        boolean flag = teacherService.removeById(id);
        if(flag){
            return R.ok();
        }else {
            return R.error();
        }
    }
    //3.分页查询
    //current当前页
    //limit 每页记录数
    @GetMapping("pageTeacher/{current}/{limit}")
    public R pageListTeacher(@PathVariable long current,//当前页
                             @PathVariable long limit//每页记录数
                             ){

      //创建page对象
        Page<EduTeacher> pageTeacher=new Page<>(current,limit);

        //调用方法实现分页
        //调用方法时,底层封装,把分页所有数据封装到pageTeacher对象里面

        teacherService.page(pageTeacher,null);

        //得到记录总数
        long total =pageTeacher.getTotal();   //得到记录总数

        List<EduTeacher> records=pageTeacher.getRecords();//数据list集合



//        第一种写法:有两个值
//        return R.ok().data("total",total).data("rows",records);

//        第二种写法
        Map map=new HashMap();
        map.put("total",total);
        map.put("rows",records);

        return R.ok().data(map);
    }



    //4条件查询带分页查询的方法
    @PostMapping("pageTeacherCondition/{current}/{limit}")
    public R pageTeacherCondition(@PathVariable long current,@PathVariable long limit,
                                  @RequestBody(required = false)  TeacherQuery teacherQuery){
        //创建一个page对象
        Page<EduTeacher> pageTeacher=new Page<>(current,limit);
        //调用方法实现条件查询
        QueryWrapper<EduTeacher> wrapper=new QueryWrapper<>();
        //多条件组合查询
        //mybatis学过 动态sql
        //判断条件值是否为空,如果不为空拼接条件
        String name=teacherQuery.getName();
        Integer level=teacherQuery.getLevel();
        String begin=teacherQuery.getBegin();
        String end=teacherQuery.getEnd();
        //判断条件是否为空

        //第一个字段
        if(!StringUtils.isEmpty(name)){
            //构建条件
            wrapper.like("name",name);

        }
        //第二个字段
        if (!StringUtils.isEmpty(level)){
            wrapper.eq("level",level);
        }

        //第二个字段
        if (!StringUtils.isEmpty(begin)){
            wrapper.ge("gmt_create",begin);

        }
        //第二个字段
        if (!StringUtils.isEmpty(end)){
            wrapper.le("gmt_create",end);
        }
        //排序的方法
        wrapper.orderByDesc("gmt_create");

        //调用方法实现条件查询分页
        teacherService.page(pageTeacher,wrapper);

        long total =pageTeacher.getTotal();   //得到记录总数

        List<EduTeacher> records=pageTeacher.getRecords();//数据list集合
        return R.ok().data("total",total).data("rows",records);

    }
    //5添加讲师

    @PostMapping("addTeacher")
    public R addTeacher(@RequestBody EduTeacher eduTeacher){
        boolean save = teacherService.save(eduTeacher);
        if (save){
            return R.ok();

        }else {
            return R.error();
        }


    }

    //根据讲师id进行查询
    @GetMapping("getTeacher/{id}")
    public R getTeacher(@PathVariable String id) {
        EduTeacher eduTeacher = teacherService.getById(id);
        return R.ok().data("teacher",eduTeacher);
    }

    //讲师修改功能
    @PostMapping("updateTeacher")
    public R updateTeacher(@RequestBody EduTeacher eduTeacher) {
        boolean flag = teacherService.updateById(eduTeacher);
        if(flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }
}













