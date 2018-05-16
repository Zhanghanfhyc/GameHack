package rts.ai.core;

import java.util.ArrayList;
import java.util.List;

public class ParameterSpecification {
    public String name = null;
    public Class type = null;
    public Object defaultValue = null;
    public List<Object> possibleValues = null;     // used only if not-null
    public Double minValue = null, maxValue = null; // for parameters with a range


    public ParameterSpecification(String n, Class t, Object dv) {
        name = n;
        type = t;
        defaultValue = dv;
    }


    public void addPossibleValue(Object v) {
        if (possibleValues==null) possibleValues = new ArrayList<>();
        possibleValues.add(v);
    }


    public void setRange(Double min, Double max) {
        minValue = min;
        maxValue = max;
    }
}
