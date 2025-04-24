package com.example.jeasyrule.demo.demos.officialTutorial.risk.engine;

import com.example.jeasyrule.demo.demos.officialTutorial.risk.pojo.RiskContext;
import com.example.jeasyrule.demo.demos.officialTutorial.risk.pojo.RiskFeature;
import com.example.jeasyrule.demo.demos.officialTutorial.risk.pojo.RiskLevel;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.RuleBuilder;

/**
 * @Author：xujiale
 * @Date：2025/4/25 1:01
 */
public class EngineGroup2 {
    public static Rule highRiskRule() {
        return new RuleBuilder()
                .name("特征 → 高风险")
                .priority(2)
                .when(f -> {
                    RiskContext ctx = f.get("ctx");
                    return ctx.getFeatures().contains(RiskFeature.ABNORMAL_LOGIN)
                            && ctx.getFeatures().contains(RiskFeature.FUNDS_BRIDGING);
                })
                .then(f -> {
                    RiskContext ctx = f.get("ctx");
                    ctx.setLevel(RiskLevel.HIGH);
                    f.put("ctx", ctx);
                })
                .build();
    }

    public static Rule mediumRiskRule() {
        return new RuleBuilder()
                .name("特征 → 中风险")
                .priority(2)
                .when(f -> {
                    RiskContext ctx = f.get("ctx");
                    return ctx.getLevel() == null                      // 还没被判高风险
                            && ctx.getFeatures().contains(RiskFeature.ABNORMAL_LOGIN);
                })
                .then(f -> {
                    RiskContext ctx = f.get("ctx");
                    ctx.setLevel(RiskLevel.MEDIUM);
                    f.put("ctx", ctx);
                })
                .build();
    }
}
