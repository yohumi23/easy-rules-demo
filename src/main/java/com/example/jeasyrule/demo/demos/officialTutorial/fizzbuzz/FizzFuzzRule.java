package com.example.jeasyrule.demo.demos.officialTutorial.fizzbuzz;

import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.support.composite.UnitRuleGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author：xujiale
 * @Date：2025/4/24 13:46
 */
public class FizzFuzzRule extends UnitRuleGroup {
    Logger logger = LoggerFactory.getLogger(FizzFuzzRule.class);
    public FizzFuzzRule(Object... rules) {
        for (Object rule : rules) {
            addRule(rule);
        }
    }

//    @Priority
//    public int priority(){
//        return 0;
//    }

    @Override
    public int getPriority() {
        return 0;
    }
}
