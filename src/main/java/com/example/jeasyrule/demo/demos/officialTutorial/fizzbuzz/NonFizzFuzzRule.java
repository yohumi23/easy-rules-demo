package com.example.jeasyrule.demo.demos.officialTutorial.fizzbuzz;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author：xujiale
 * @Date：2025/4/24 13:57
 */
@Rule(name = "non fizz&fuzz", description = "既不是Fizz也不是Fuzz的规则,打印数字")
public class NonFizzFuzzRule {

    Logger logger = LoggerFactory.getLogger(NonFizzFuzzRule.class);

    @Condition
    public boolean isNonFizzFuzz(@Fact("number") Integer number) {
        return number % 3 != 0 && number % 5 != 0;
    }

    @Action
    public void printNumber(@Fact("number") Integer number) {
        System.out.print(number);
    }
}
