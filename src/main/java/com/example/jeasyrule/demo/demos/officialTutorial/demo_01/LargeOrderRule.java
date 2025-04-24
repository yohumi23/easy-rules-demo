package com.example.jeasyrule.demo.demos.officialTutorial.demo_01;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;

import java.util.Optional;

// 规则一：判断订单金额是否超过 1000 元，若超过则标记订单为大额订单
@Rule(name = "LargeOrderRule", description = "Mark order as large if amount > 1000")
public class LargeOrderRule {
    @Condition
    public boolean isLargeOrder(Facts facts) {
        Double orderAmount = facts.get("orderAmount");
        return Optional.ofNullable(orderAmount).orElse(0.0) > 1000;
    }

    @Action
    public void markAsLargeOrder(Facts facts) {
        facts.put("isLargeOrder", true);
        System.out.println("订单被标记为大额订单");
    }
}
