package com.example.jeasyrule.demo.demos.officialTutorial.shop;

/**
 * @Author：xujiale
 * @Date：2025/4/24 23:35
 */
public class Person {

    private String name;
    private int age;
    private boolean adult;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
            this.adult = adult;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", adult=" + adult +
                '}';
    }
}
