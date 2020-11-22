package com.arctos.sample.reflection;

import org.junit.Test;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ReflectionTest {

    private static List<String> getMethodNames(Method[] methods) {
        List<String> methodNames = new ArrayList<>();
        for (Method method : methods)
            methodNames.add(method.getName());
        return methodNames;
    }

    /*********************
     * Class/Package Name
     *********************/

    @Test
    public void givenObject_whenGetsClassName_thenCorrect() {
        Object master = new Master("arctos");
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
        Object master = new Master("arctos");
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
        Master master = new Master("arctos");
        String str = "any string";

        Class<?> masterClass = master.getClass();
        Class<?> masterSuperClass = masterClass.getSuperclass();

        assertEquals("Student", masterSuperClass.getSimpleName());
        assertEquals("com.arctos.sample.reflection.Student", masterSuperClass.getCanonicalName());
        assertEquals("String", str.getClass().getSimpleName());
        assertEquals("Object", str.getClass().getSuperclass().getSimpleName());
        assertNull(str.getClass().getSuperclass().getSuperclass());
    }

    /**************************
     * Implemented Interfaces
     **************************/

    @Test
    public void givenClass_whenGetsImplementedInterfaces_thenCorrect() {
        try {
            Class<?> masterClass = Class.forName("com.arctos.sample.reflection.Master");
            Class<?> studentClass = Class.forName("com.arctos.sample.reflection.Student");

            Class<?>[] masterInterfaces = masterClass.getInterfaces();
            Class<?>[] studentInterfaces = studentClass.getInterfaces();

            assertEquals(1, masterInterfaces.length);
            assertEquals(1, studentInterfaces.length);
            assertEquals("Person", masterInterfaces[0].getSimpleName());
            assertEquals("Studying", studentInterfaces[0].getSimpleName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*********************************
     *          Constructors
     *********************************/

    @Test
    public void givenClass_whenGetsAllConstructors_thenCorrect() {
        try {
            Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");

            Constructor<?>[] constructors = bachelorClass.getConstructors();

            assertEquals(3, constructors.length);
            assertEquals("com.arctos.sample.reflection.Bachelor", constructors[0].getName());
            assertEquals("com.arctos.sample.reflection.Bachelor", constructors[1].getName());
            assertEquals("com.arctos.sample.reflection.Bachelor", constructors[2].getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenClass_whenGetsEachConstructorByParamTypes_thenCorrect() {
        try {
            Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");

            Constructor<?> cons1 = bachelorClass.getConstructor();
            Constructor<?> cons2 = bachelorClass.getConstructor(String.class);
            Constructor<?> cons3 = bachelorClass.getConstructor(String.class, boolean.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenClass_whenInstantiatesObjectsAtRuntime_thenCorrect() {
        try {
            Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
            Constructor<?> cons1 = bachelorClass.getConstructor();
            Constructor<?> cons2 = bachelorClass.getConstructor(String.class);
            Constructor<?> cons3 = bachelorClass.getConstructor(String.class, boolean.class);

            Bachelor bachelor1 = (Bachelor) cons1.newInstance(); // casting is required here
            Bachelor bachelor2 = (Bachelor) cons2.newInstance("arctos"); // casting is required here
            Bachelor bachelor3 = (Bachelor) cons3.newInstance("arctos li", true); // casting is required here

            assertEquals("unknown", bachelor1.getName());
            assertFalse(bachelor1.isGraduated());
            assertEquals("arctos", bachelor2.getName());
            assertFalse(bachelor2.isGraduated());
            assertEquals("arctos li", bachelor3.getName());
            assertTrue(bachelor3.isGraduated());
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /*******************
     *     Fields
     *******************/

    @Test
    public void givenClass_whenGetsPublicFields_thenCorrect() {
        try {
            Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
            Field[] fields = bachelorClass.getFields();

            assertEquals(1, fields.length);
            assertEquals("GENDER", fields[0].getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenClass_whenGetsPublicFieldByName_thenCorrect() {
        try {
            Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
            Field field = bachelorClass.getField("GENDER");

            assertEquals("GENDER", field.getName());
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenClass_whenGetsDeclaredFields_thenCorrect() {
        try {
            Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
            Field[] fields = bachelorClass.getDeclaredFields();

            assertEquals(1, fields.length);
            assertEquals("graduated", fields[0].getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenClass_whenGetsDeclaredFieldByName_thenCorrect() {
        try {
            Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
            Field field = bachelorClass.getDeclaredField("graduated");

            assertEquals("graduated", field.getName());
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenClass_whenGetsFieldType_thenCorrect() {
        try {
            Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
            Field field = bachelorClass.getDeclaredField("graduated");
            Class<?> fieldClass = field.getType();

            assertEquals("boolean", fieldClass.getName());
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenClassField_whenSetsAndGetsValue_thenCorrect() {
        try {
            Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
            Bachelor bachelor = (Bachelor) bachelorClass.getConstructor().newInstance();
            Field field = bachelorClass.getDeclaredField("graduated");
            field.setAccessible(true); // set it to be accessible so we can modify it in below

            assertEquals(false, field.get(bachelor)); // pass object to get() method to retrieve the value
            assertFalse(field.getBoolean(bachelor));
            assertFalse(bachelor.isGraduated());

            field.set(bachelor, true); // to set value of field, we need pass object and the new value

            assertEquals(true, field.get(bachelor));
            assertTrue(field.getBoolean(bachelor));
            assertTrue(bachelor.isGraduated());

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenClassField_whenGetsAndSetsWithNull_thenCorrect() {
        try {
            Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
            Field field = bachelorClass.getField("GENDER");
            field.setAccessible(true); // set it to be accessible so we can modify it in below

            assertEquals("male", field.get(null)); // pass null as instance of class parameter

            field.set(null, "female");

            assertEquals("female", field.get(null));
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /*******************
     *     Methods
     *******************/

    @Test
    public void givenClass_whenGetsAllPublicMethods_thenCorrect() {
        try {
            Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
            Method[] methods = bachelorClass.getMethods();

            List<String> methodNames = getMethodNames(methods);
            assertTrue(methodNames.containsAll(Arrays
                    .asList("equals", "notify", "notifyAll", "hashCode", "toString", "getClass",
                            "study", "isGraduated", "setGraduated", "getName", "setName")));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenClass_whenGetsOnlyDeclaredMethods_thenCorrect() {
        try {
            Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
            List<String> actualMethodNames = getMethodNames(bachelorClass.getDeclaredMethods());
            List<String> expectedMethodNames = Arrays.asList("study", "isGraduated", "setGraduated", "getStudyString", "isBachelor", "degreeName");

            assertEquals(expectedMethodNames.size(), actualMethodNames.size());
            assertTrue(expectedMethodNames.containsAll(actualMethodNames));
            assertTrue(actualMethodNames.containsAll(expectedMethodNames));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenMethodName_whenGetsMethod_thenCorrect() throws Exception {
        Bachelor bachelor = new Bachelor();
        Method getStudyStringMethod = bachelor.getClass().getDeclaredMethod("getStudyString");

        assertFalse(getStudyStringMethod.isAccessible());

        getStudyStringMethod.setAccessible(true); // set it to be accessible so we can invoke it in below

        assertTrue(getStudyStringMethod.isAccessible());
        assertEquals("I'm studying!", getStudyStringMethod.invoke(bachelor));
    }

    @Test
    public void givenMethod_whenInvokes_thenCorrect() {
        try {
            Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
            Bachelor bachelor = (Bachelor) bachelorClass.getConstructor().newInstance();
            Method isGraduatedMethod = bachelor.getClass().getDeclaredMethod("isGraduated");
            Method setGraduatedMethod = bachelor.getClass().getDeclaredMethod("setGraduated", boolean.class);
            boolean graduated = (boolean) isGraduatedMethod.invoke(bachelor);

            assertFalse(graduated);
            assertFalse(bachelor.isGraduated());

            setGraduatedMethod.invoke(bachelor, true);

            boolean graduated2 = (boolean) isGraduatedMethod.invoke(bachelor);
            assertTrue(graduated2);
            assertTrue(bachelor.isGraduated());
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenMethod_whenInvokesWithNull_thenCorrect() {
        try {
            Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
            Bachelor bachelor = (Bachelor) bachelorClass.getConstructor().newInstance();
            Method isBachelorMethod = bachelor.getClass().getDeclaredMethod("isBachelor");
            Method degreeNameMethod = bachelor.getClass().getDeclaredMethod("degreeName");

            isBachelorMethod.setAccessible(true); // set it to be accessible so we can invoke it in below
            degreeNameMethod.setAccessible(true); // set it to be accessible so we can invoke it in below

            assertTrue((boolean) isBachelorMethod.invoke(null));
            assertEquals("Bachelor", degreeNameMethod.invoke(null));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
