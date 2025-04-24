package com.example.jeasyrule.demo.demos.officialTutorial.fizzbuzz;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author：xujiale
 * @Date：2025/4/24 13:45
 */
@Rule(name = "fuzz rule", description = "如果数字是5的倍数，打印fuzz")
public class FuzzRule {
    Logger logger = LoggerFactory.getLogger(FuzzRule.class);
    @Condition
    public boolean isFuzz(@Fact("number") Integer number) {
        return number % 5 == 0;
    }
    @Action
    public void printFuzz() {
        System.out.print("fuzz");
    }
}
