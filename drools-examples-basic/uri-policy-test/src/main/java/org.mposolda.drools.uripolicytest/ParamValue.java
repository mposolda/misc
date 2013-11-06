package org.mposolda.drools.uripolicytest;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public final class ParamValue {

    private final String value;

    public ParamValue(String value) {
        this.value = value;
    }

    public int toInt() {
        return Integer.parseInt(value);
    }

    public boolean toBoolean() {
        return Boolean.parseBoolean(value);
    }

    public double toDouble() {
        return Double.parseDouble(value);
    }

    // TODO: Other types

    @Override
    public String toString() {
        return value();
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ParamValue) {
            return ((ParamValue)o).value == this.value;
        } else if (o instanceof String) {
            return o.equals(value);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
