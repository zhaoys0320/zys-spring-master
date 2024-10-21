package org.zys.service;


import org.spring.Autowired;
import org.spring.Component;
import org.spring.Scope;

@Component("userService")
@Scope("prototype")
public class UserService {

    @Autowired
    private OrderService orderService;
}
