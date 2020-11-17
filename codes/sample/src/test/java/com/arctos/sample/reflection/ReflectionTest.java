package com.arctos.sample.reflection;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class ReflectionTest {

    /*********************
     * Class/Package Name
     *********************/

    @Test
    public void givenObject_whenGetsClassName_thenCorrect() {
        Object master = new Master();
        Class<?> clazz = master.getClass();

        assertEquals("Master", clazz.getSimpleName());
        assertEquals("com.arctos.sample.reflection.Master", clazz.getName());
        assertEquals("com.arctos.sample.reflection.Master", clazz.getCanonicalName());
    }

    @Test
    public void givenClassName_whenCreatesObject_thenCorrect() {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("com.arctos.sample.reflection.Master");
            assertEquals("Master", clazz.getSimpleName());
            assertEquals("com.arctos.sample.reflection.Master", clazz.getName());
            assertEquals("com.arctos.sample.reflection.Master", clazz.getCanonicalName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenClass_whenGetsPackageInfo_thenCorrect() {
        Object master = new Master();
        Class<?> masterClass = master.getClass();
        Package pkg = masterClass.getPackage();

        assertEquals("com.arctos.sample.reflection", pkg.getName());
    }

    /*********************
     *   Class Modifier
     *********************/

    @Test
    public void givenClass_whenRecognisesModifiers_thenCorrect() {
        try {
            Class<?> masterClass = Class.forName("com.arctos.sample.reflection.Master");
            Class<?> studentClass = Class.forName("com.arctos.sample.reflection.Student");

            int masterModifiers = masterClass.getModifiers();
            int studentModifiers = studentClass.getModifiers();

            assertTrue(Modifier.isPublic(masterModifiers));
            assertFalse(Modifier.isFinal(masterModifiers));
            assertTrue(Modifier.isAbstract(studentModifiers));
            assertTrue(Modifier.isPublic(studentModifiers));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*********************
     *   Super Class
     *********************/

    @Test
    public void givenClass_whenGetsSuperClass_thenCorrect() {
        Master master = new Master();
        String str = "any string";

        Class<?> masterClass = master.getClass();
        Class<?> masterSuperClass = masterClass.getSuperclass();

        assertEquals("Student", masterSuperClass.getSimpleName());
        assertEquals("com.arctos.sample.reflection.Student", masterSuperClass.getCanonicalName());
        assertEquals("String", str.getClass().getSimpleName());
        assertEquals("Object", str.getClass().getSuperclass().getSimpleName());
        assertNull(str.getClass().getSuperclass().getSuperclass());
    }
}
