package com.arctos.sample.reflection;

public abstract class Student implements Studying {

    public static String GENDER = "male";
    private String name;

    public Student(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}