package com.example.jeasyrule.demo.demos.officialTutorial.helloworld;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Rule;

/**
 * @Author：xujiale
 * @Date：2025/4/24 23:30
 */
@Rule(name = "hello_world engine", description = "hello world engine")
public class HelloRule {

    @Condition
    public boolean when() {
        return true;
    }

    @Action
    public void then() {
        System.out.println("hello world");
    }
}
