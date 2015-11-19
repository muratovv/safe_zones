package HyperEdgeFramework.Util;

import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.vividsolutions.jts.geom.*;

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

	public static Coordinate point(com.github.davidmoten.rtree.geometry.Point point)
	{
		return new Coordinate(point.x(), point.y());
	}

	public static Rectangle rectangle(Polygon poly)
	{
		Envelope envelope = poly.getEnvelopeInternal();
		return Rectangle.create(
				envelope.getMinX(), envelope.getMinY(),
				envelope.getMaxX(), envelope.getMaxY());
	}

	public static Polygon polygon(GeometryFactory factory, Rectangle r)
	{
		return factory.createPolygon(new Coordinate[]{
				new Coordinate(r.x1(), r.y1()),
				new Coordinate(r.x1(), r.y2()),
				new Coordinate(r.x2(), r.y2()),
				new Coordinate(r.x2(), r.y1()),
				new Coordinate(r.x1(), r.y1()),
		});
	}
}
