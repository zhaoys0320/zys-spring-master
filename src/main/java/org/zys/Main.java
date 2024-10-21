package org.zys;

import org.spring.ZysApplicationContext;

public class Main {
    public static void main(String[] args) {
        ZysApplicationContext zysApplicationContext = new ZysApplicationContext(AppConfig.class);
        zysApplicationContext.getBean("userService");


    }
}