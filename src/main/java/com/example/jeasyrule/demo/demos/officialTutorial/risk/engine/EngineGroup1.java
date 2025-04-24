package com.example.jeasyrule.demo.demos.officialTutorial.risk.engine;

import com.example.jeasyrule.demo.demos.officialTutorial.risk.pojo.Behavior;
import com.example.jeasyrule.demo.demos.officialTutorial.risk.pojo.RiskContext;
import com.example.jeasyrule.demo.demos.officialTutorial.risk.pojo.RiskFeature;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.RuleBuilder;

/**
 * @Author：xujiale
 * @Date：2025/4/25 0:58
 */
public class EngineGroup1 {
    public static Rule abnormalLoginRule() {
        return new RuleBuilder()
                .name("行为 → 异常登录")
                .description("海外登录或终端切换 + 多次失败登录 → ABNORMAL_LOGIN")
                .priority(1)
                .when(f -> {
                    RiskContext ctx = f.get("ctx");
                    return (ctx.getBehaviors().contains(Behavior.LOGIN_OVERSEAS)
                            || ctx.getBehaviors().contains(Behavior.LOGIN_DEVICE_CHANGE))
                            && ctx.getBehaviors().contains(Behavior.MULTI_FAILED_LOGIN);
                })
                .then(f -> {
                    RiskContext ctx = f.get("ctx");
                    ctx.getFeatures().add(RiskFeature.ABNORMAL_LOGIN);
                    f.put("ctx", ctx);                // 触发推理
                })
                .build();
    }

    public static Rule fundsBridgingRule() {
        return new RuleBuilder()
                .name("行为 → 资金搬砖")
                .priority(1)
                .when(f -> {
                    RiskContext ctx = f.get("ctx");
                    return ctx.getBehaviors().contains(Behavior.TRANSFER_MANY_SMALL)
                            && ctx.getBehaviors().contains(Behavior.TRANSFER_LARGE_NIGHT);
                })
                .then(f -> {
                    RiskContext ctx = f.get("ctx");
                    ctx.getFeatures().add(RiskFeature.FUNDS_BRIDGING);
                    f.put("ctx", ctx);
                })
                .build();
    }
}
