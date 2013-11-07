package org.mposolda.drools.uripolicytest;

/**
 * Value of request parameter
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ParamValue {

    private final String value;

    public ParamValue(String value) {
        // TODO:
//        if (value == null) {
//            throw new IllegalArgumentException("Null value of parameter");
//        }
        this.value = value;
    }

    // TODO: Improve existing

    public Integer toInt() {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public Boolean toBoolean() {
        if (value != null) {
            return Boolean.parseBoolean(value);
        } else {
            return null;
        }
    }

    public double toDouble() {
        return Double.parseDouble(value);
    }

    // TODO: Other types

    @Override
    public String toString() {
        return value!=null ? value() : "";
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ParamValue) {
            return ((ParamValue)o).value.equals(this.value);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
