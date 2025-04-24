package com.example.jeasyrule.demo.demos.officialTutorial.risk;

import com.example.jeasyrule.demo.demos.officialTutorial.risk.pojo.Behavior;
import com.example.jeasyrule.demo.demos.officialTutorial.risk.pojo.RiskContext;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.InferenceRulesEngine;

import java.util.Arrays;

import static com.example.jeasyrule.demo.demos.officialTutorial.risk.engine.EngineGroup1.abnormalLoginRule;
import static com.example.jeasyrule.demo.demos.officialTutorial.risk.engine.EngineGroup1.fundsBridgingRule;
import static com.example.jeasyrule.demo.demos.officialTutorial.risk.engine.EngineGroup2.highRiskRule;
import static com.example.jeasyrule.demo.demos.officialTutorial.risk.engine.EngineGroup2.mediumRiskRule;
import static com.example.jeasyrule.demo.demos.officialTutorial.risk.engine.RuleGroup3.highRiskActionRule;
import static com.example.jeasyrule.demo.demos.officialTutorial.risk.engine.RuleGroup3.mediumRiskActionRule;

public class RiskInferenceDemo {

    public static void main(String[] args) {

        // ① 初始化上下文（模拟发生的行为）
        RiskContext ctx = new RiskContext();
        ctx.getBehaviors().addAll(Arrays.asList(
            Behavior.LOGIN_OVERSEAS,
            Behavior.MULTI_FAILED_LOGIN,
            Behavior.TRANSFER_MANY_SMALL,
            Behavior.TRANSFER_LARGE_NIGHT
        ));

        // ② 注册全部规则
        Rules rules = new Rules(
            abnormalLoginRule(),
            fundsBridgingRule(),
            highRiskRule(),
            mediumRiskRule(),
            highRiskActionRule(),
            mediumRiskActionRule()
        );

        // ③ Facts
        Facts facts = new Facts();
        facts.put("ctx", ctx);

        // ④ 强大的前向推理引擎
        RulesEngine engine = new InferenceRulesEngine();
        engine.fire(rules, facts);

        // ⑤ 查看最终结果
        RiskContext result = facts.get("ctx");
        System.out.println("\n=== 推理结果 ===");
        System.out.println("风险特征: " + result.getFeatures());
        System.out.println("风险等级: " + result.getLevel());
        System.out.println("处置动作: " + result.getActions());
    }
}