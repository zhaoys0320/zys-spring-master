package org.spring;

import org.zys.exception.BusinessException;
import org.zys.exception.ErrorCode;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZysApplicationContext{
    private  Class configClass;
    private ConcurrentHashMap<String,Object> singletonObjectMap = new ConcurrentHashMap<>();

    //设置map保存每类bean的属性，避免每次使用或者创建bean对象都要解析属性。
    private ConcurrentHashMap<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();
    public ZysApplicationContext(Class configClass){
        this.configClass = configClass;
        //得到配置类
        //解析配置类--->解析ComponentScan注解--->扫描路径
        scan(configClass);
        for (Map.Entry<String, BeanDefinition> entries:beanDefinitionMap.entrySet()) {
            String BeanName = entries.getKey();
            BeanDefinition beanDefinition = beanDefinitionMap.get(BeanName);

            if (beanDefinition.getScope().equals("singleton")) {
                Object bean = createBean(BeanName,beanDefinition);
                singletonObjectMap.put(BeanName,bean);
            }
        }

    }
        public Object createBean(String BeanName,BeanDefinition beanDefinition){
            Class clazz = beanDefinition.getClazz();
            try {

                //调用构造方法，构造示例
                Object instance = clazz.getDeclaredConstructor().newInstance();
                Field[] declaredFields = clazz.getDeclaredFields();


                //依赖注入，获取到类的定义区域，判断每个定义区域是否包含Autowired对象，包含则创建bean注入。
                for (Field declaredField : declaredFields) {
                    if (declaredField.isAnnotationPresent(Autowired.class)) {
                        String name = declaredField.getName();
                        System.out.println("需要注入的bean名称： "+name);
                        Object bean = getBean(name);
                        declaredField.setAccessible(true);
                        declaredField.set(instance,bean);
                    }
                }

                //Aware接口回调
                if (instance instanceof BeanNameAware) {
                    ((BeanNameAware) instance).setBeanName(BeanName);
                }
                //初始化前BeanPostProcessor
                for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                    instance = beanPostProcessor.postProcessBeforeInitialization(instance, BeanName);
                }

                //初始化
                if (instance instanceof InitializeBean) {
                    ((InitializeBean) instance).afterPropertiesSet();
                }
                //初始化后BeanPostProcessor
                for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                    instance = beanPostProcessor.postProcessAfterInitialization(instance, BeanName);
                }
                return instance;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    private void scan(Class configClass) {
        ComponentScan declaredAnnotation =(ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String value = declaredAnnotation.value();
        System.out.println(value);
        String replacedValue = value.replace('.', '/');
        System.out.println(replacedValue);
        /*
        * classLoader 分类
        * 1、Bootstrap-->加载jre/lib下边的类
        * 2、Extension--> 加载jre/ext/lib 下边的类
        * 3、Application-->加载指定classpath下边的类
        * */
        //获取classLoader 通过classloader 获取文件夹下边的类
        ClassLoader classLoader = ZysApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(replacedValue);
        File file = new File(resource.getFile());
        System.out.println(file);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for(File f: files){
                String name = f.getName();
                String s = name.split("\\.")[0];
                StringBuilder s1= new StringBuilder(value+"."+ s);
                String string = s1.toString();
                try {
                    Class<?> aClass = classLoader.loadClass(string);

                    if(aClass.isAnnotationPresent(Component.class)){
                        //有component注解 表示为一个bean  创建bean对象
                        //根据component注解得到bean的属性 创建BeanDefinition对象

                        //判断当前类是否实现了BeanPostProcessor接口，如果实现了就实例化加入列表等待遍历访问
                        if (BeanPostProcessor.class.isAssignableFrom(aClass)) {
                            Object instance = aClass.getDeclaredConstructor().newInstance();
                            beanPostProcessorList.add((BeanPostProcessor) instance);
                        }


                        BeanDefinition beanDefinition = new BeanDefinition();

                        beanDefinition.setClazz(aClass);
                        Component componentAnnotation = aClass.getDeclaredAnnotation(Component.class);
                        String BeanName = componentAnnotation.value();

                        if(aClass.isAnnotationPresent(Scope.class)){
                            Scope scopeAnnotation = aClass.getDeclaredAnnotation(Scope.class);
                            String scope = scopeAnnotation.value();
                            beanDefinition.setScope(scope);
                        }else{
                            beanDefinition.setScope("singleton");
                        }

                        beanDefinitionMap.put(BeanName,beanDefinition);

                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException ignored) {
                }
            }
        }
    }

    public Object getBean(String BeanName){
        if (beanDefinitionMap.containsKey(BeanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(BeanName);
            if (beanDefinition.getScope().equals("singleton")) {
                return singletonObjectMap.get(BeanName);
            } else{
                return createBean(BeanName,beanDefinition);
            }
        }else{
            //报错 申请不存在的bean
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

    }
}
