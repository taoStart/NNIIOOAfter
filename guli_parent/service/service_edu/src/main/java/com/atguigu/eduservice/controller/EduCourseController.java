package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.client.VodClient;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.entity.vo.CourseInfoVo;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.entity.vo.CourseQuery;
import com.atguigu.eduservice.entity.vo.TeacherQuery;
import com.atguigu.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2022-06-19
 */
@RestController
@RequestMapping("/eduservice/course")
@CrossOrigin
public class EduCourseController {

    @Autowired
    private EduCourseService courseService;





    // 课程列表基本实现
    @GetMapping("getCourseList")
    public R getCourseList(){
        List<EduCourse> list =courseService.list(null);
        return R.ok().data("list",list);

    }

//    添加课程基本信息的方法
    @PostMapping("addCourseInfo")
    public R addCourseInfo(@RequestBody CourseInfoVo courseInfoVo){
        //返回添加之后课程id，为了后面添加大纲使用
        String id = courseService.saveCourseInfo(courseInfoVo);


        return R.ok().data("courseId",id);
    }
    //根据课程查询课程基本信息
    @GetMapping("getCourseInfo/{courseId}")
    public R getCourseInfo(@PathVariable String courseId){


        CourseInfoVo courseInfoVo=courseService.getCourseInfo(courseId);
        return R.ok().data("courseInfoVo",courseInfoVo );
    }
    //修改课程信息
    @PostMapping("updateCourseInfo")
    public R updateCourseInfo(@RequestBody CourseInfoVo courseInfoVo){
        courseService.updateCourseInfo(courseInfoVo);

        return R.ok();
    }

    //根据课程id查询课程确认信息
    @GetMapping("getPublishCourseInfo/{id}")
    public R getPublishCourseInfo(@PathVariable String id){
        CoursePublishVo coursePublishVo=courseService.publishCourseInfo(id);

        return R.ok().data("coursePublishVo",coursePublishVo);
    }

    //课程最终发布
    //修改课程状态
    @PostMapping("publishCourse/{id}")
    public R publishCourse(@PathVariable String id){
        EduCourse eduCourse=new EduCourse();
        eduCourse.setId(id);
        eduCourse.setStatus("Normal");
        courseService.updateById(eduCourse);
        return R.ok();
    }






    //4条件查询带分页查询的方法
    @PostMapping("pageCourseCondition/{current}/{limit}")
    public R pageCourseCondition(@PathVariable long current,@PathVariable long limit,
                                  @RequestBody(required = false) CourseQuery teacherQuery){
        //创建一个page对象
        Page<EduCourse> pageCourse=new Page<>(current,limit);
        //调用方法实现条件查询
        QueryWrapper<EduCourse> wrapper=new QueryWrapper<>();
        //多条件组合查询
//        //mybatis学过 动态sql
//        //判断条件值是否为空,如果不为空拼接条件
        String title=teacherQuery.getTitle();
        String status=teacherQuery.getStatus();
        String begin=teacherQuery.getBegin();
        String end=teacherQuery.getEnd();
        //判断条件是否为空

                //第一个字段
        if(!StringUtils.isEmpty(title)){
            //构建条件
            wrapper.like("title",title);

        }
        //第二个字段
        if (!StringUtils.isEmpty(status)){
            wrapper.eq("status",status);
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
//
        //调用方法实现条件查询分页
        courseService.page(pageCourse,wrapper);
//
        long total =pageCourse.getTotal();   //得到记录总数
//
        List<EduCourse> records=pageCourse.getRecords();//数据list集合
        return R.ok().data("total",total).data("rows",records);

    }


    //删除课程
    @DeleteMapping("{courseId}")
    public R deleteCourse(@PathVariable String courseId){
        courseService.removeCourse(courseId);
        return R.ok();
    }

}

