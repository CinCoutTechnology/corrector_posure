package com.example.posture_corrector.model;

public class modelC {

    String name;
    String Text;
    int a;

    public modelC(String name, String text, int a) {
        this.name = name;
        Text = text;
        this.a = a;
    }

    public modelC() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }
}
