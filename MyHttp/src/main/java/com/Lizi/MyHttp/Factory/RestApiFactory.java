package com.Lizi.MyHttp.Factory;

import com.Lizi.MyHttp.Annot.MyAutowired;
import com.Lizi.MyHttp.Annot.Component;
import com.Lizi.MyHttp.Annot.RestApi;
import com.Lizi.MyHttp.Annot.RestController;
import com.Lizi.MyHttp.Util.outUtil;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RestApiFactory {
//    private String packUrl=null;
    public static RestApiFactory beanFactory=null;
    public final ConcurrentHashMap<String, Method> getMethod=new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, Method> postMethod=new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, Object> component=new ConcurrentHashMap<>();
    private RestApiFactory(String packName){
        try {
            init(packName);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
    public static RestApiFactory getBeanFactory(String packName){
        if(beanFactory==null)beanFactory=new RestApiFactory(packName);
        return beanFactory;
    }
    public static RestApiFactory getBeanFactory(){
        if(beanFactory==null) outUtil.outErr("工厂类未初始化");
        return beanFactory;
    }
    public void init(String packgeName) throws IllegalAccessException, InstantiationException {
        outUtil.outBlue("开始扫描And加载\"RestApi\"注解类方法");
        Reflections reflections=null;
        if(packgeName==null){
            reflections=new Reflections();
        }else {
            reflections=new Reflections(packgeName);
        }
        loadClass(reflections);


    }

    private void loadClass(Reflections reflections) throws IllegalAccessException, InstantiationException {
        Set<Class<?>> set= reflections.getTypesAnnotatedWith(Component.class);
        for (Class<?> c:set) {
            if(c.isAnnotation()){continue;}                                        //如果获取到的是Annotation跳过本次循环
            component.put(c.getName(),c.newInstance());                             //装载组件
            String route=c.getDeclaredAnnotation(RestController.class).route();
            Method[] methods=c.getDeclaredMethods();
            for (Method m:methods) {
                RestApi http=m.getDeclaredAnnotation(RestApi.class);
                if(http.method().equals("GET")) {
                    getMethod.put(route+http.url(), m);
                }else
                if(http.method().equals("POST")){
                    postMethod.put(route+http.url(), m);
                }
            }
        }
        outUtil.outBlue("扫描完成，共有:"+set.size()+"个注解类,"+(getMethod.size()+postMethod.size())+"个接口");
        injection(reflections);
    }
    private void injection(Reflections reflections){
        Set<Field> set= reflections.getFieldsAnnotatedWith(MyAutowired.class);
        System.out.println(set);
    }

}
