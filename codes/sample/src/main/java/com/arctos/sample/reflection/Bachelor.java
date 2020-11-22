package com.arctos.sample.reflection;

public class Bachelor extends Student {

    private boolean graduated;

    /**********************
     *    Constructor
     **********************/

    public Bachelor() {
        super("unknown");
    }

    public Bachelor(String name) {
        super(name);
    }

    public Bachelor(String name, boolean graduated) {
        super(name);
        setGraduated(graduated);
    }

    /**********************
     *   Static methods
     **********************/

    private static boolean isBachelor() {
        return true;
    }

    private static String degreeName() {
        return "Bachelor";
    }

    /**********************
     *  Setter and Getter
     **********************/

    public boolean isGraduated() {
        return graduated;
    }

    public void setGraduated(boolean graduated) {
        this.graduated = graduated;
    }

    /**********************
     * Implemented methods
     **********************/

    @Override
    public String study() {
        return getStudyString();
    }

    private String getStudyString() {
        return "I'm " + "studying!";
    }
}
