package com.example.jeasyrule.demo.demos.officialTutorial.mvel;

import org.mvel2.MVEL;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author：xujiale
 * @Date：2025/4/24 23:44
 */
public class MVELRuleExample {

    public static void main(String[] args) {
        Map<String, Object> context = new HashMap<>();
/*        context.put("age", 18);
        String expression = "if (age == 18) { 'is 18' } else { 'not 18' }";
        String result = (String) MVEL.eval(expression, context);
        System.out.println(result);

        List<Integer> nums = Arrays.asList(1, 2, 3);
        context.put("nums", nums);
        String expression1 = "sum = 0; for(num : nums) { sum += num; } sum";
        Integer sum = (Integer) MVEL.eval(expression1, context);
        System.out.println(sum);*/


        Person person = new Person("jerry");
        context.put("person", person);
        String expression2 = "person.name"; // 直接访问属性,也可用getName() 获取
        String name = (String) MVEL.eval(expression2, context);
        System.out.println(name);
    }
}
