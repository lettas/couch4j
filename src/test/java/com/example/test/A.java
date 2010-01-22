package com.example.test;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class A implements Serializable {
    private Date date;
    private String name;
    private int number;
    private B b;

    public A() {
        date = new Date();
        name = "Name";
        number = 5;
        b = new B();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }

}
