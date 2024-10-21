package org.zys;

import org.spring.ZysApplicationContext;
import org.zys.service.UserInterface;
import org.zys.service.UserService;

public class Main {
    public static void main(String[] args) {
        ZysApplicationContext zysApplicationContext = new ZysApplicationContext(AppConfig.class);
        UserInterface bean = (UserInterface) zysApplicationContext.getBean("userService");
        bean.test();

    }
}