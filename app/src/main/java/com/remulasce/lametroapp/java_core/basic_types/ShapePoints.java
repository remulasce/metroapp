package com.remulasce.lametroapp.java_core.basic_types;

import java.util.ArrayList;
import java.util.List;

/** This is like a Shape, but with all the points filled in.
 * It's really big.
 *
 * The ShapePoint list is ordered from first point to last point visited.
 * You're welcome.
 */
public class ShapePoints {
    public ShapePoints() {}
    public String shapeId;

    public List<ShapePoint> points = new ArrayList<>();
}
