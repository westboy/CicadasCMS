package com.zhiliao.component.beetl;


import com.google.common.collect.Maps;
import com.zhiliao.component.beetl.fun.*;
import com.zhiliao.component.beetl.tag.ShiroTag;
import org.beetl.core.Function;
import org.beetl.core.GroupTemplate;
import org.beetl.core.TagFactory;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class BeetlConfiguration {


    @Value("${system.site.name}")
    private String siteName;

    @Value("${system.dev.model}")
    private String devModel;

    @Value("${system.http.protocol}")
    private String httpProtocol;

    @Value("${system.http.host}")
    private String host;

    @Value("${system.site.prefix}")
    private String sitePrefix;

    @Value("${system.login.path}")
    private String loginPath;

    public Map<String,Object> sharedVars(){
        Map<String,Object> sharedVars =  Maps.newHashMap();
        sharedVars.put("siteName",siteName);
        sharedVars.put("baseURL",httpProtocol+"://"+ host);
        sharedVars.put("resPath",httpProtocol+"://"+ host+"/"+"static"+"/"+"www");
        sharedVars.put("devModel",Boolean.valueOf(devModel));
        sharedVars.put("adminLoginPath",loginPath);
        sharedVars.put("sitePrefix",sitePrefix);
        sharedVars.put("frontPath",httpProtocol+"://"+ host+"/"+sitePrefix);
        return sharedVars;
    }

    public Map<String,Object> functionPackages(){
        Map<String,Object> functionPackages = new HashMap<String,Object>();
        functionPackages.put("shiro",new ShiroTag());
        return functionPackages;
    }

    @Bean(name = "function")
    public Map<String,Function> function(PermissionFunction permissionTag,
                                         TreePermissionFunction permissionFunction,
                                         TreeCategoryFunction categoryFunction,
                                         SelectCategoryFunction selectCategoryFunction,
                                         TreeContentCategoryFunction contentCategoryFunction,
                                         SelectPermissionFunction selectPermissionOut,
                                         ContentTreeCategoryFunction contentTreeCategoryFunction,
                                         ContentSelectCategoryFunction contentSelectCategoryFunction,
                                         TreeTopicCatagoryFunction treeTopicCatagoryFunction,
                                         TreeOrganizationFunction treeOrganizationFunction,
                                         SelectOrganizationFunction selectOrganizationFunction,
                                         OrganizationFunction organizationFunction
                                        ){
        Map<String,Function> functionPackages = Maps.newHashMap();
        /*????????????*/
        functionPackages.put("PrintTime",new PrintTimeFunction());
        /*????????????Checkbox*/
        functionPackages.put("PermissionOut",permissionTag);
        /*????????????????????????*/
        functionPackages.put("TreePermissionOut",permissionFunction);
        /*????????????????????????*/
        functionPackages.put("TreeCategoryOut",categoryFunction);
        /*??????????????????????????????????????????*/
        functionPackages.put("TreeContentCategoryFunction",contentCategoryFunction);
        /*????????????HtmlSelect??????*/
        functionPackages.put("SelectPermissionOut",selectPermissionOut);
        /*????????????HtmlSelect??????*/
        functionPackages.put("SelectCategoryOut",selectCategoryFunction);
        /*????????????????????????????????????*/
        functionPackages.put("ContentCategoryOut",contentTreeCategoryFunction);
        functionPackages.put("ContentSelectCategoryFunction",contentSelectCategoryFunction);
        /*???????????????????????????????????????*/
        functionPackages.put("printModelFiledClass",new PrintModelFiledClassFunction());

        functionPackages.put("TopicCatagoryOut",treeTopicCatagoryFunction);
        functionPackages.put("TreeOrgOut",treeOrganizationFunction);
        functionPackages.put("selectOrganizationOut",selectOrganizationFunction);
        functionPackages.put("organizationOut",organizationFunction);


        return functionPackages;
    }

    @Bean(initMethod = "init", name = "beetlConfig")
    public BeetlGroupUtilConfiguration getbeetlConfig(@Qualifier("function")Map<String,Function> function,
                                                      @Qualifier("tagFactory")Map<String,TagFactory> tagFactory) {

        BeetlGroupUtilConfiguration beetlGroupUtilConfiguration = new BeetlGroupUtilConfiguration();
        ResourcePatternResolver patternResolver = ResourcePatternUtils.getResourcePatternResolver(new DefaultResourceLoader());
        try {
            ClasspathResourceLoader classpathResourceLoader = new ClasspathResourceLoader(BeetlConfiguration.class.getClassLoader(), "templates/");
            beetlGroupUtilConfiguration.setConfigFileResource(patternResolver.getResource("classpath:beetl.properties"));
            beetlGroupUtilConfiguration.setResourceLoader(classpathResourceLoader);
            beetlGroupUtilConfiguration.setFunctionPackages(functionPackages());
            beetlGroupUtilConfiguration.setSharedVars(sharedVars());
            beetlGroupUtilConfiguration.setFunctions(function);
            beetlGroupUtilConfiguration.setTagFactorys(tagFactory);
//            beetlGroupUtilConfiguration.setErrorHandler(new BeetlWebErrorHandler());
            return beetlGroupUtilConfiguration;

        } catch (Exception e) {

            throw new RuntimeException(e);

        }
    }



    @Bean(name = "beetlViewResolver")
    public BeetlSpringViewResolver getBeetlSpringViewResolver(@Qualifier(value = "beetlConfig") BeetlGroupUtilConfiguration beetlGroupUtilConfiguration) {
        BeetlSpringViewResolver beetlSpringViewResolver = new BeetlSpringViewResolver();
        beetlSpringViewResolver.setContentType("text/html;charset=UTF-8");
        beetlSpringViewResolver.setOrder(0);
        beetlSpringViewResolver.setSuffix(".html");
        beetlSpringViewResolver.setConfig(beetlGroupUtilConfiguration);
        return beetlSpringViewResolver;
    }

    @Bean(name = "groupTemplate")
    public GroupTemplate getGroupTemplate(
            @Qualifier("beetlConfig") BeetlGroupUtilConfiguration beetlGroupUtilConfiguration) {
        return beetlGroupUtilConfiguration.getGroupTemplate();

    }

}
