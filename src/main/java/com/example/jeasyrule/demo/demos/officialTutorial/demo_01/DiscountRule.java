package com.example.jeasyrule.demo.demos.officialTutorial.demo_01;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;

import java.util.Optional;


// 规则二：若订单是大额订单，且商品数量超过 5 件，则给予 10% 的折扣
@Rule(name = "DiscountRule", description = "Apply 10% discount if large order and item count > 5")
public class DiscountRule {
    @Condition
    public boolean shouldApplyDiscount(Facts facts) {
        Boolean isLargeOrder = facts.get("isLargeOrder");
        Integer itemCount = facts.get("itemCount");
        return Optional.ofNullable(isLargeOrder).orElse(false) && Optional.of(itemCount).orElse(0) > 5;
    }

    @Action
    public void applyDiscount(Facts facts) {
        double orderAmount = facts.get("orderAmount");
        double discountedAmount = orderAmount * 0.9;
        facts.put("discountedAmount", discountedAmount);
        facts.put("hasDiscount", true);
        System.out.println("订单享受 10% 折扣，折扣后金额为: " + discountedAmount);
    }
}
