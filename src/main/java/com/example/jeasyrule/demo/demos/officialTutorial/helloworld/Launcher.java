package com.example.jeasyrule.demo.demos.officialTutorial.helloworld;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.core.DefaultRulesEngine;

/**
 * @Author：xujiale
 * @Date：2025/4/24 23:31
 */
public class Launcher {

    public static void main(String[] args) {
        DefaultRulesEngine engine = new DefaultRulesEngine();
        engine.fire(new Rules(new HelloRule()), new Facts());
    }
}
