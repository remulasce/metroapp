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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shape shape = (Shape) o;

        return raw != null ? raw.equals(shape.raw) : shape.raw == null;

    }

    @Override
    public int hashCode() {
        return raw != null ? raw.hashCode() : 0;
    }
}
