package com.atguigu.servicebase;


import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration//配置类
@EnableSwagger2//swagger注解
public class SwaggerConfig {


    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)//类型
                .groupName("webApi")//给起的名字
                .apiInfo(webApiInfo())//下面文档的信息
                .select()
//                .paths(Predicates.not(PathSelectors.regex("/admin/.*")))//包含admin就不进行显示
                .paths(Predicates.not(PathSelectors.regex("/error.*")))//包含error// 就不进行显示
                .build();

    }

    private ApiInfo webApiInfo(){

        return new ApiInfoBuilder()
                .title("网站-课程API文档")
                .description("本文档描述了课程中心微服务接口定义")
                .version("1.0")
                .contact(new Contact("java", "http://nniioo.com", "2915727133@qq.com"))
                .build();
    }
}
