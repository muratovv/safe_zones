package HyperEdgeFramework.Util;

import com.github.davidmoten.rtree.geometry.Circle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class AdapterUtil
{
	public static Polygon polygon(GeometryFactory gFactory, Circle circle, int splits)
	{
		double radius = radius(circle);
		Point point = gFactory.createPoint(new Coordinate(circle.x(), circle.y()));
		return ((Polygon) point.buffer(radius, splits));
	}

	private static double radius(Circle circle)
	{
		double a = circle.mbr().perimeter() / 4.;
		return a / 2.;
	}
}
