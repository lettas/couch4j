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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((m == null) ? 0 : m.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof B)) {
            return false;
        }
        B other = (B) obj;
        if (m == null) {
            if (other.m != null) {
                return false;
            }
        } else if (!m.equals(other.m)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "B [m=" + m + "]";
    }

}
