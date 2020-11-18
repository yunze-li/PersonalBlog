package com.arctos.sample.reflection;

public class Master extends Student implements Person {
    public Master(String name) {
        super(name);
    }

    @Override
    public String walk() {
        return "I'm walking!";
    }

    @Override
    public String study() {
        return "I'm studying!";
    }
}
