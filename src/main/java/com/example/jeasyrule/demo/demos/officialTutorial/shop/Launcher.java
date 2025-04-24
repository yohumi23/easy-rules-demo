package com.example.jeasyrule.demo.demos.officialTutorial.shop;

import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.mvel.MVELRule;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.support.reader.YamlRuleDefinitionReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;

/**
 * @Author：xujiale
 * @Date：2025/4/25 0:05
 */
public class Launcher {

    public static void main(String[] args) throws Exception {
        Person person = new Person("John", 19);
        MVELRule rule1 = new MVELRule();
        rule1.name("adult rule")
                .description("check if person is adult")
                .priority(0)
                .when("person.age > 18")
                .then("person.setAdult(true)");
        DefaultRulesEngine engine = new DefaultRulesEngine();
        Rules rules = new Rules();
        rules.register(rule1);
        Facts facts = new Facts();
        facts.put("person", person);
        engine.fire(rules, facts);

        MVELRuleFactory ruleFactory = new MVELRuleFactory(new YamlRuleDefinitionReader());
        ClassLoader classLoader = Launcher.class.getClassLoader();
        URL resource = classLoader.getResource("alcohol.yml");
        String fileName = resource.getPath();
        Rule rule2 = ruleFactory.createRule(new FileReader(fileName));
        rules.register(rule2);
        engine.fire(rules, facts);
        System.out.println(person);
    }
}
