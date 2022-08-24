package com.atguigu.eduservice.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.eduservice.entity.EduSubject;
import com.atguigu.eduservice.entity.excel.SubjectData;
import com.atguigu.eduservice.service.EduSubjectService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public class    SubjectExcelListener extends AnalysisEventListener<SubjectData> {

    //因为subjectExceListener不能交给spring进行管理,需要自己new  ,不能注入其他对象

//    手动注入来实现数据库的操作
    public EduSubjectService subjectService;


    public SubjectExcelListener( ) {

    }

    public SubjectExcelListener(EduSubjectService subjectService) {
        this.subjectService = subjectService;
    }
    //读取数据,一行一行读取数据

    @Override
    public void invoke(SubjectData subjectData, AnalysisContext analysisContext) {
        if(subjectData == null){
            throw new GuliException(20001,"文件数据为空");
        }

        //一行一行读取,每次读取两个值
        EduSubject existOneSubject= this.existOneSubject(subjectService, subjectData.getOneSubjectName());
        if (existOneSubject==null){//没有相同的一级,进行分类
//         一级分类的添加
            existOneSubject=new EduSubject();
            existOneSubject.setParentId("0");
            existOneSubject.setTitle(subjectData.getOneSubjectName());
            subjectService.save(existOneSubject);
        }
//        添加二级分类:
/*获取一级分类*/
        String pid=existOneSubject.getId();


        EduSubject existTwoSubject = this.existTwoSubject(subjectService, subjectData.getTwoSubjectName(), pid);
        if (existTwoSubject == null ){
            existTwoSubject=new EduSubject();
            existTwoSubject.setParentId(pid);
            existTwoSubject.setTitle(subjectData.getTwoSubjectName());
            subjectService.save(existTwoSubject);

        }


        /*
* 规则:
*     一级分类:添加一次,只添加一次
*     之后做个判断:有的话跳过:因为在添加的时候每次的ParentId在每次添加的值是唯一的,
*
*
*
* */





    }

    //        判断一级分类不能重复添加
    private EduSubject existOneSubject(EduSubjectService subjectService,String name){//传入数据库传值,还有一级分类的名称
        QueryWrapper<EduSubject> wrapper=new QueryWrapper<>();
        wrapper.eq("title",name);
        wrapper.eq("parent_id","0");
        EduSubject oneSubject=subjectService.getOne(wrapper);
        return oneSubject;
    }



//        判断二级分类不能重复添加
    private EduSubject existTwoSubject(EduSubjectService subjectService,String name,String pid){//传入数据库传值,还有一级分类的名称
        QueryWrapper<EduSubject> wrapper=new QueryWrapper<>();
        wrapper.eq("title",name);
        wrapper.eq("parent_id","pid");
        EduSubject twoSubject=subjectService.getOne(wrapper);
        return twoSubject;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
