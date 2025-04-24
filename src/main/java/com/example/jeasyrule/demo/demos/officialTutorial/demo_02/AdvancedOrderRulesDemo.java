package com.example.jeasyrule.demo.demos.officialTutorial.demo_02;

import org.jeasy.rules.annotation.*;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.InferenceRulesEngine;

import java.util.Optional;

public class AdvancedOrderRulesDemo {

    // 规则1: 大额订单判断
    @Rule(name = "LargeOrderRule", description = "标记大额订单")
    public static class LargeOrderRule {
        @Condition
        public boolean isLargeOrder(Facts facts) {
            Double amount = facts.get("orderAmount");
            return Optional.ofNullable(amount).orElse(0.0) > 1000;
        }

        @Action
        public void markAsLarge(Facts facts) {
            if (!(boolean)facts.get("isLargeMarked")) {
                facts.put("isLargeOrder", true);
                facts.put("isLargeMarked", true); // 防止重复执行
                System.out.println("规则触发: 订单被标记为大额订单");
            }
        }
    }

    // 规则2: 数量折扣
    @Rule(name = "QuantityDiscountRule", description = "数量折扣")
    public static class QuantityDiscountRule {
        @Condition
        public boolean shouldApply(Facts facts) {
            Boolean isLarge = facts.get("isLargeOrder");
            Integer quantity = facts.get("itemCount");
            return Optional.ofNullable(isLarge).orElse(false) 
                   && Optional.ofNullable(quantity).orElse(0) > 5
                   &&  !(boolean)facts.get("qtyDiscountApplied");
        }

        @Action
        public void applyDiscount(Facts facts) {
            Double amount = facts.get("orderAmount");
            Double discounted = amount * 0.9;
            facts.put("discountedAmount", discounted);
            facts.put("qtyDiscountApplied", true);
            System.out.printf("规则触发: 数量折扣10%%，金额 %.2f → %.2f%n", amount, discounted);
        }
    }

    // 规则3: VIP折扣
    @Rule(name = "VipDiscountRule", description = "VIP额外折扣")
    public static class VipDiscountRule {
        @Condition
        public boolean shouldApply(Facts facts) {
            Boolean hasDiscount = facts.get("qtyDiscountApplied");
            Boolean isVip = facts.get("isVip");
            return Optional.ofNullable(hasDiscount).orElse(false) 
                   && Optional.ofNullable(isVip).orElse(false)
                   && !(boolean)facts.get("vipDiscountApplied");
        }

        @Action
        public void applyVipDiscount(Facts facts) {
            Double amount = facts.get("discountedAmount");
            Double finalAmount = amount * 0.95;
            facts.put("finalAmount", finalAmount);
            facts.put("vipDiscountApplied", true);
            System.out.printf("规则触发: VIP额外5%%，金额 %.2f → %.2f%n", amount, finalAmount);
        }
    }

    // 规则4: 礼品卡赠送
    @Rule(name = "GiftCardRule", description = "赠送礼品卡")
    public static class GiftCardRule {
        @Condition
        public boolean shouldApply(Facts facts) {
            Double amount = (Double) Optional.ofNullable(facts.get("finalAmount"))
                                  .orElse(facts.get("orderAmount"));
            return amount > 2000 && Boolean.FALSE.equals(facts.get("giftCardGiven"));
        }

        @Action
        public void giveGiftCard(Facts facts) {
            facts.put("giftCardAmount", 100);
            facts.put("giftCardGiven", true);
            System.out.println("规则触发: 赠送100元礼品卡");
            
            // 重新计算最终金额（可能触发新一轮推导）
            Double current = (Double) Optional.ofNullable(facts.get("finalAmount"))
                                   .orElse(facts.get("orderAmount"));
            facts.put("finalAmount", current - 100);
        }
    }

    // 规则5: 周年庆特别折扣
    @Rule(name = "AnniversaryRule", description = "周年庆特别折扣")
    public static class AnniversaryRule {
        @Condition
        public boolean shouldApply(Facts facts) {
            Boolean isAnniversary = facts.get("isAnniversary");
            Boolean hasGiftCard = facts.get("giftCardGiven");
            return Optional.ofNullable(isAnniversary).orElse(false) 
                   && Optional.ofNullable(hasGiftCard).orElse(false)
                   && !(boolean)facts.get("anniversaryDiscountApplied");
        }

        @Action
        public void applySpecialDiscount(Facts facts) {
            Double amount = facts.get("finalAmount");
            Double newAmount = amount * 0.9;
            facts.put("finalAmount", newAmount);
            facts.put("anniversaryDiscountApplied", true);
            System.out.printf("规则触发: 周年庆额外10%%，金额 %.2f → %.2f%n", amount, newAmount);
        }
    }

    // 规则6: 订单优先级设置
    @Rule(name = "PriorityRule", description = "设置订单优先级")
    public static class PriorityRule {
        @Condition
        public boolean shouldApply(Facts facts) {
            return facts.get("finalAmount") != null 
                   && facts.get("priority") == null;
        }

        @Action
        public void setPriority(Facts facts) {
            Double amount = facts.get("finalAmount");
            String priority = amount > 1500 ? "HIGH" : "NORMAL";
            facts.put("priority", priority);
            System.out.println("规则触发: 订单优先级设置为 " + priority);
        }
    }

    public static void main(String[] args) {
        // 创建推理引擎（会多次推导直到稳定）
        RulesEngine engine = new InferenceRulesEngine();
        
        // 创建规则集合
        Rules rules = new Rules();
        rules.register(new LargeOrderRule());
        rules.register(new QuantityDiscountRule());
        rules.register(new VipDiscountRule());
        rules.register(new GiftCardRule());
        rules.register(new AnniversaryRule());
        rules.register(new PriorityRule());

        // 创建事实 - 模拟一个大额VIP订单，恰逢周年庆
        Facts facts = new Facts();
        facts.put("orderAmount", 2500.0);
        facts.put("itemCount", 8);
        facts.put("isVip", true);
        facts.put("isAnniversary", true);

        System.out.println("======== 开始规则推导 ========");
        engine.fire(rules, facts);
        
        System.out.println("\n======== 最终结果 ========");
        System.out.printf("原始金额: %.2f%n", facts.get("orderAmount"));
        System.out.printf("最终金额: %.2f%n", facts.get("finalAmount"));
        System.out.println("订单优先级: " + facts.get("priority"));
        System.out.println("获得礼品卡: " + 
            (facts.get("giftCardGiven") ? "是" : "否"));
    }
}