package com.example.jeasyrule.demo.demos.officialTutorial.risk.engine;


import com.example.jeasyrule.demo.demos.officialTutorial.risk.pojo.Action;
import com.example.jeasyrule.demo.demos.officialTutorial.risk.pojo.RiskContext;
import com.example.jeasyrule.demo.demos.officialTutorial.risk.pojo.RiskLevel;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.RuleBuilder;

/**
 * @Author：xujiale
 * @Date：2025/4/25 1:02
 */
public class RuleGroup3 {
    public static Rule highRiskActionRule() {
        return new RuleBuilder()
                .name("高风险 → 冻结账户并告警")
                .priority(3)
                .when(f -> {
                    RiskContext ctx = f.get("ctx");
                    return ctx.getLevel() == RiskLevel.HIGH;
                })
                .then(f -> {
                    RiskContext ctx = f.get("ctx");
                    ctx.getActions().add(Action.FREEZE_ACCOUNT);
                    ctx.getActions().add(Action.ALERT_MANAGER);
                    f.put("ctx", ctx);
                    System.out.println(">>> 高风险！冻结账户并通知风控经理。");
                })
                .build();
    }

    public static Rule mediumRiskActionRule() {
        return new RuleBuilder()
                .name("中风险 → 二次验证")
                .priority(3)
                .when(f -> {
                    RiskContext ctx = f.get("ctx");
                    return ctx.getLevel() == RiskLevel.MEDIUM;
                })
                .then(f -> {
                    RiskContext ctx = f.get("ctx");
                    ctx.getActions().add(Action.REQUIRE_OTP_VERIFICATION);
                    f.put("ctx", ctx);
                    System.out.println(">>> 中风险，要求短信 OTP 验证。");
                })
                .build();
    }
}
