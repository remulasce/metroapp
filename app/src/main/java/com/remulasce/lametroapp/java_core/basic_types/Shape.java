package com.remulasce.lametroapp.java_core.basic_types;

public class Shape {
    private String raw = "";

    public Shape(String shapeId) {
        this.raw = shapeId;
    }

    public void setShapeId(String raw) {
        this.raw = raw;
    }

    public String getShapeId() {
        return raw;
    }
}
