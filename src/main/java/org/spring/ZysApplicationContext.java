package org.spring;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.net.URL;

public class ZysApplicationContext {
    private  Class configClass;

    public ZysApplicationContext(Class configClass) throws URISyntaxException {
        this.configClass = configClass;
        //得到配置类
        //解析配置类--->解析ComponentScan注解--->扫描路径
        ComponentScan declaredAnnotation =(ComponentScan) this.configClass.getDeclaredAnnotation(ComponentScan.class);
        String value = declaredAnnotation.value();
        System.out.println(value);

        /*
        * classLoader 分类
        * 1、Bootstrap-->加载jre/lib下边的类
        * 2、Extension--> 加载jre/ext/lib 下边的类
        * 3、Application-->加载指定classpath下边的类
        *
        * */
        //获取classLoader 通过classloader 获取文件夹下边的类
        ClassLoader classLoader = ZysApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource("org/zys/service");
        File file = new File(resource.getFile());
        System.out.println(file);
        if (file.isDirectory()) {

            File[] files = file.listFiles();
            for(File f: files){
                System.out.println(f);
            }
        }

    }

    public Object getBean(String BeanName){
            return null;
    }
}
