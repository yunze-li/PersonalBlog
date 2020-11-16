package com.arctos.sample.reflection;

public class Master extends Student implements Person {
    @Override
    public String walk() {
        return "I'm walking!";
    }

    @Override
    protected String getStudentName() {
        return "arctos";
    }

    @Override
    public String study() {
        return "I'm studying!";
    }
}
