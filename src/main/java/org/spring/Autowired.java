package org.spring;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
* 即告诉编译程序如何处理，也可理解为注解类的生命周期。
* */
@Retention(RetentionPolicy.RUNTIME)

@Target(ElementType.FIELD)
/*
* Target翻译中文为目标，即该注解可以声明在哪些目标元素之前，也可理解为注释类型的程序元素的种类
* */
public @interface Autowired {
}
