package com.example.jeasyrule.demo.demos.officialTutorial.airco;

/**
 * @Author：xujiale
 * @Date：2025/4/24 13:12
 */

import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Facts;

/**
 * 高温条件
 */
public class HighTempCon implements Condition {
    static Condition itsHot(){
        return new HighTempCon();
    }

    @Override
    public boolean evaluate(Facts facts) {
        Integer temperature = facts.get("temperature");
        return temperature > 25;
    }
}
