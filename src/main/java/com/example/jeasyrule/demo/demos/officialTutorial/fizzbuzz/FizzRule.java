package com.example.jeasyrule.demo.demos.officialTutorial.fizzbuzz;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author：xujiale
 * @Date：2025/4/24 13:35
 */
@Rule(name = "fizz rule", description = "如果数字是3的倍数，打印fizz")
public class FizzRule {
    Logger logger = LoggerFactory.getLogger(FizzRule.class);

    @Condition
    public boolean isFizz(@Fact("number") Integer number) {
        return number % 3 == 0;
    }

    @Action
    public void printFizz() {
        System.out.print("fizz");
    }
}
