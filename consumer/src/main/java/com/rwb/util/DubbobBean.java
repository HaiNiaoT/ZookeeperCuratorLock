package com.rwb.util;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DubbobBean {
    private final static ClassPathXmlApplicationContext context =
            new ClassPathXmlApplicationContext(new String[]{"dubbo-consumer.xml"});

    public static  <T> T getBean(String name,Class<T> type){

        return context.getBean(name,type);
    }
}
