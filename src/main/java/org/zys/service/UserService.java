package org.zys.service;


import org.spring.*;

@Component("userService")
@Scope("prototype")
public class UserService implements BeanNameAware, InitializeBean,UserInterface{

    @Autowired
    private OrderService orderService;

    private String beanName;

    @Override
    public void setBeanName(String var1) {
        this.beanName = var1;
        System.out.println(beanName);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("用户自定义的初始化");
    }

    public void test(){
        System.out.println(beanName);
        System.out.println(orderService);
    }
}
