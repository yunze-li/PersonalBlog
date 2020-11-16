package com.arctos.sample.reflection;

public abstract class Student implements Studying {
    private String name;
    protected abstract String getStudentName();
}