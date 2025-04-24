package com.example.jeasyrule.demo.demos.officialTutorial.fizzbuzz;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngineParameters;
import org.jeasy.rules.core.DefaultRulesEngine;

/**
 * @Author：xujiale
 * @Date：2025/4/24 13:34
 */
public class Launcher {
    public static void main(String[] args) {
        //当第一个规则被触发时，不再执行别的规则，true;
        RulesEngineParameters parameters = new RulesEngineParameters();
        parameters.setSkipOnFirstAppliedRule(true);
        DefaultRulesEngine engine = new DefaultRulesEngine(parameters);
        Rules rules = new Rules();
        rules.register(new FizzRule());
        rules.register(new FuzzRule());
        rules.register(new FizzFuzzRule(new FizzRule(), new FuzzRule()));
        rules.register(new NonFizzFuzzRule());
        Facts facts = new Facts();
        for (int i = 1; i <= 100; i++) {
            facts.put("number", i);
            engine.fire(rules, facts);
            System.out.println();
        }
    }
}
