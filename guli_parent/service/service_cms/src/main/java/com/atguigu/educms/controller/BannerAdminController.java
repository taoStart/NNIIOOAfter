package com.atguigu.educms.controller;


import com.atguigu.commonutils.R;
import com.atguigu.educms.entity.CrmBanner;
import com.atguigu.educms.service.CrmBannerService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 首页banner表 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2022-08-04
 */
@RestController
@RequestMapping("/educms/banneradmin")
@CrossOrigin
public class BannerAdminController {

    @Autowired
    private CrmBannerService bannerService;


//1.分页查询
    @GetMapping("pageBanner/{page}/{limit}")
    public R pageBanner(@PathVariable long page,@PathVariable Long limit){
        Page<CrmBanner> pageBanner=new Page<>(page,limit);
        bannerService.page(pageBanner,null);

        return R.ok().data("items",pageBanner.getRecords()).data("total",pageBanner.getTotal());

    }
//添加banner
    @PostMapping("addBanner")
    public R addBanner(@RequestBody CrmBanner crmBanner){
        bannerService.save(crmBanner);
        return R.ok();
    }



    //获取banner
    @GetMapping("get/{id}")
    public R getBanner(@PathVariable String id){
        CrmBanner banner=bannerService.getById(id);
        return R.ok().data("item",banner);

    }



//修改banner
    @PutMapping("updataBanner")
    @ApiOperation(value = "修改banner")
    public R updateBanner(@RequestBody CrmBanner banner){
        bannerService.updateById(banner);
        return R.ok();
    }
    @DeleteMapping("deleteBanner/{id}")
    public R deleteBanner(@PathVariable String id){
        bannerService.removeById(id);
        return R.ok();
    }


}

