package org.spring;

/*
* 定义类的属性
*
* */

import lombok.Data;

@Data
public class BeanDefinition {
    private String scope;
    private Class clazz;

    public BeanDefinition(String scope, Class clazz) {
        this.scope = scope;
        this.clazz = clazz;
    }

    public BeanDefinition() {
    }
}
