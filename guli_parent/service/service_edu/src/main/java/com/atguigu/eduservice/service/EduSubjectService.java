package com.atguigu.eduservice.service;

import com.atguigu.eduservice.entity.EduSubject;
import com.atguigu.eduservice.entity.subject.OneSubject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author testjava
 * @since 2022-06-17
 */
public interface EduSubjectService extends IService<EduSubject> {


//    添加课程分类

    void saveSubject(MultipartFile file,EduSubjectService eduSubjectService);

//    课程分类的所有数据
    List<OneSubject> getAllOneTwoSubject();
}
