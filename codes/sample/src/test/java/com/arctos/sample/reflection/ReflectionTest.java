package com.arctos.sample.reflection;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReflectionTest {

    @Test
    public void givenObject_whenGetsClassName_thenCorrect() {
        Object master = new Master();
        Class<?> clazz = master.getClass();

        assertEquals("Master", clazz.getSimpleName());
        assertEquals("com.arctos.sample.reflection.Master", clazz.getName());
        assertEquals("com.arctos.sample.reflection.Master", clazz.getCanonicalName());
    }

}
