package com.example.jeasyrule.demo.demos.officialTutorial.demo_01;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngineParameters;
import org.jeasy.rules.core.InferenceRulesEngine;

public class Launcher {
    public static void main(String[] args) {
        // 创建推理规则引擎
        RulesEngineParameters parameters = new RulesEngineParameters();
        parameters.setSkipOnFirstAppliedRule(true);
        InferenceRulesEngine ruleEngine = new InferenceRulesEngine();

        // 创建规则集合
        Rules rules = new Rules();
        rules.register(new LargeOrderRule());
        rules.register(new DiscountRule());
        rules.register(new VipDiscountRule());

        // 创建事实集合
        Facts facts = new Facts();
        facts.put("orderAmount", (double)1200);
        facts.put("itemCount", 6);
        facts.put("isVip", true);

        // 运行规则引擎
        ruleEngine.fire(rules, facts);
    }
}    