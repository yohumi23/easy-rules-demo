package com.example.jeasyrule.demo.demos.officialTutorial.airco;

import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Facts;

/**
 * @Author：xujiale
 * @Date：2025/4/24 13:14
 */
public class DecreaseTemperatureAction implements Action {
    static Action coolAir(){
        return new DecreaseTemperatureAction();
    }

    @Override
    public void execute(Facts facts) {
        System.out.println("It is hot! cooling air..");
        Integer temperature = facts.get("temperature");
        facts.put("temperature", temperature - 1);
    }
}


