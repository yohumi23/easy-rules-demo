package com.example.jeasyrule.demo.demos.officialTutorial.demo_01;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;

import java.util.Optional;


// 规则三：若订单有折扣，且用户是 VIP，则额外再给予 5% 的折扣
@Rule(name = "VipDiscountRule", description = "Apply additional 5% discount if has discount and user is VIP")
public class VipDiscountRule {
    @Condition
    public boolean shouldApplyVipDiscount(Facts facts) {
        Boolean hasDiscount = facts.get("hasDiscount");
        Boolean isVip = facts.get("isVip");
        return Optional.ofNullable(hasDiscount).orElse(false) && Optional.ofNullable(isVip).orElse(false);
    }

    @Action
    public void applyVipDiscount(Facts facts) {
        double discountedAmount = facts.get("discountedAmount");
        double finalAmount = discountedAmount * 0.95;
        facts.put("finalAmount", finalAmount);
        System.out.println("订单因用户是 VIP 额外享受 5% 折扣，最终金额为: " + finalAmount);
    }
}

