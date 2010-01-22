package com.example.test;

import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("serial")
public class B implements Serializable {
    private BigDecimal m;

    public B() {
        m = BigDecimal.TEN;
    }
    
    public BigDecimal getM() {
        return m;
    }

    public void setM(BigDecimal m) {
        this.m = m;
    }
}
