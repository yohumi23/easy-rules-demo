package com.example.jeasyrule.demo.demos.officialTutorial.airco;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.core.InferenceRulesEngine;
import org.jeasy.rules.core.RuleBuilder;

import static com.example.jeasyrule.demo.demos.officialTutorial.airco.DecreaseTemperatureAction.coolAir;
import static com.example.jeasyrule.demo.demos.officialTutorial.airco.HighTempCon.itsHot;

/**
 * @Author：xujiale
 * @Date：2025/4/24 13:23
 */
public class Launcher {
    public static void main(String[] args) {
        Facts facts = new Facts();
        facts.put("temperature", 20);
        RuleBuilder ruleBuilder = new RuleBuilder();
        Rule rule0 = ruleBuilder
                .name("空调触发器")
                .description("当温度大于25时，开启空调")
                .when(itsHot())
                .then(coolAir())
                .build();
        Rules rules = new Rules(rule0);
        InferenceRulesEngine engine = new InferenceRulesEngine();
        engine.fire(rules, facts);
    }
}
