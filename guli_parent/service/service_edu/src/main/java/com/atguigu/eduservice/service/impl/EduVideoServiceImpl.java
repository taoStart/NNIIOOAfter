package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.client.VodClient;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.mapper.EduVideoMapper;
import com.atguigu.eduservice.service.EduVideoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2022-06-19
 */
@Service
public class EduVideoServiceImpl extends ServiceImpl<EduVideoMapper, EduVideo> implements EduVideoService {
    //注入删除多个视频的接口
    @Autowired
    private VodClient vodClient;

    //1  根据课程id删除小节
    @Override
    public void removeVideoByCourseId(String courseId) {
        //根据课程id查询所有的视频id
        QueryWrapper<EduVideo> wrapperVideo=new QueryWrapper<>();//query对象
        wrapperVideo.eq("course_id",courseId);//查表
        wrapperVideo.select("video_source_id");//取出指定字段
        List<EduVideo> eduVideoList = baseMapper.selectList(wrapperVideo);

        //List<EduVideo>
        List<String> videoIds=new ArrayList<>();
        for (int i = 0; i <eduVideoList.size(); i++) {
            EduVideo eduVideo=eduVideoList.get(i);
            String videoSourceId = eduVideo.getVideoSourceId();

            //判断表里面有数据
            if(!StringUtils.isEmpty(videoSourceId)){
                //放到videoIds集合里面
                videoIds.add(videoSourceId);
            }

        }
        if (videoIds.size()>0){
            vodClient.deleteBatch(videoIds);
        }




        QueryWrapper<EduVideo> wrapper =new QueryWrapper<>();
        wrapper.eq("course_id",courseId);
        baseMapper.delete(wrapper);
    }


}