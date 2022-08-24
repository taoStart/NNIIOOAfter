package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduCourseDescription;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.entity.frontvo.CourseFrontVo;
import com.atguigu.eduservice.entity.frontvo.CourseWebVo;
import com.atguigu.eduservice.entity.vo.CourseInfoVo;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.mapper.EduCourseMapper;
import com.atguigu.eduservice.service.EduChapterService;
import com.atguigu.eduservice.service.EduCourseDescriptionService;
import com.atguigu.eduservice.service.EduCourseService;
import com.atguigu.eduservice.service.EduVideoService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2022-06-19
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

//课程描述
    @Autowired
    private EduCourseDescriptionService courseDescriptionService;
//注入小节和章节
    @Autowired
    private EduVideoService eduVideoService;
//章节信息
    @Autowired
    private EduChapterService chapterService;



//    添加课程基本信息的方法
    @Override
    public String saveCourseInfo(CourseInfoVo courseInfoVo) {
//        加入到两张表中添加信息
//       首先加入CourseInfoVo对象转换eduCourse对象

        EduCourse eduCourse = new EduCourse();
        //转换为Educourse,把courseInfoVo
        BeanUtils.copyProperties(courseInfoVo, eduCourse);

        int insert = baseMapper.insert(eduCourse);

        if (insert == 0) {
            throw new GuliException(20001, "添加课程信息失败");

        }

/*
* 由于课程表表和课程简介表是一对一的关系
*
*
* 所以两张表的id应该是一样的
*
*
* */
//这样  获取添加之后课程的id
        String cid=eduCourse.getId();

//        2.向课程简介表添加课程简介
//
//        edu_course_description
        EduCourseDescription courseDescription=new EduCourseDescription();
        courseDescription.setDescription(courseInfoVo.getDescription());
//        设置描述id就是课程id
        courseDescription.setId(cid);


        courseDescriptionService.save(courseDescription);
        return cid;
    }
    //数据回显
    @Override
    public CourseInfoVo getCourseInfo(String courseId) {
//        根据id查询两张表,1.查询课程表2,查询描述表
//        1.课程表
        EduCourse eduCourse = baseMapper.selectById(courseId);
        CourseInfoVo courseInfoVo=new CourseInfoVo();
        BeanUtils.copyProperties(eduCourse,courseInfoVo);



//        2.查询描述表
        EduCourseDescription courseDescription= courseDescriptionService.getById(courseId);
        courseInfoVo.setDescription(courseDescription.getDescription());

        return courseInfoVo;
    }
//修改课程信息
    @Override
    public void updateCourseInfo(CourseInfoVo courseInfoVo) {
//        修改两张表,1.修改课程表2.修改描述表
//        1.课程表
        EduCourse eduCourse=new EduCourse();
        BeanUtils.copyProperties(courseInfoVo,eduCourse);
        int update=baseMapper.updateById(eduCourse);
        if (update==0){
            throw new GuliException(20001,"修改课程信息失败");

        }
//        2.修改描述表
        EduCourseDescription description=new EduCourseDescription();
        description.setId(courseInfoVo.getId());
        description.setDescription(courseInfoVo.getDescription());
        courseDescriptionService.updateById(description);



    }
    //根据课程id查询课程确认信息
    @Override
    public CoursePublishVo publishCourseInfo(String id) {
        //调用mapper
        CoursePublishVo publishCourseInfo = baseMapper.getPublishCourseInfo(id);
        return publishCourseInfo;
    }
//删除课程
    @Override
    public void removeCourse(String courseId) {
        //1  根据课程id删除小节
        eduVideoService.removeVideoByCourseId(courseId);


        //2.根据课程id删除章节
        chapterService.removeChapterByCourseId(courseId);


        //3.根据课程id删除描述
        courseDescriptionService.removeById(courseId);

        //4.根据课程id删除课程本身
        int result = baseMapper.deleteById(courseId);

        if (result == 0){
            throw  new GuliException(20001,"删除失败");
        }

    }

    //1.条件查询带分页查询课程
    @Override
    public Map<String, Object> getCourseFrontList(Page<EduCourse> pageCourse, CourseFrontVo courseFrontVo) {

        //2.根据讲师id查询所讲的课程
        QueryWrapper<EduCourse> wrapper=new QueryWrapper<>();

// 一级分类
        if (!StringUtils.isEmpty(courseFrontVo.getSubjectParentId())){
            //一级分类
            wrapper.eq("subject_parent_id",courseFrontVo.getSubjectParentId());

        }
//  二级分类
        if (!StringUtils.isEmpty(courseFrontVo.getSubjectId())){
            wrapper.eq("subject_id",courseFrontVo.getSubjectId());
        }
//  根据销量排序
        if(!StringUtils.isEmpty(courseFrontVo.getBuyCountSort())){
            wrapper.orderByDesc("buy_count");
        }
//  根据课程时间
        if (!StringUtils.isEmpty(courseFrontVo.getGmtCreateSort())){
            wrapper.orderByDesc("gmt_create");
        }
//  根据课程价格
        if (!StringUtils.isEmpty(courseFrontVo.getPriceSort())){
            wrapper.orderByDesc("price");
        }

        baseMapper.selectPage(pageCourse,wrapper);

        List<EduCourse> records = pageCourse.getRecords();
        long current = pageCourse.getCurrent();
        long pages = pageCourse.getPages();
        long size = pageCourse.getSize();
        long total = pageCourse.getTotal();
        boolean hasNext = pageCourse.hasNext();
        boolean hasPrevious = pageCourse.hasPrevious();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);

        return map;
    }

    //根据id,sql语句查询课程信息
    @Override
    public CourseWebVo getBaseCourseInfo(String courseId) {
        return baseMapper.getBaseCourseInfo(courseId);


    }
}
