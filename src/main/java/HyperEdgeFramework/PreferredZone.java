package HyperEdgeFramework;

import HyperEdgeFramework.Util.AdapterUtil;
import HyperEdgeFramework.Util.GeomUtil;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.distance.DistanceOp;

public class PreferredZone implements Geometry
{
	private static int currentIndex = 0;

	Polygon poly;
	double alpha;
	Coordinate centroid;
	int index;

	public PreferredZone(Polygon polygon, double alpha)
	{
		if (!PolyUtil.isConvex(polygon))
			throw new IllegalArgumentException(String.format("%s is not convex", polygon));
		this.centroid = polygon.getCentroid().getCoordinate();
		this.poly = polygon;
		this.alpha = alpha;
		this.index = currentIndex++;
	}

	public Polygon getPoly()
	{
//		return ((Polygon) poly.clone());
		return poly;
	}

	public int getIndex()
	{
		return index;
	}

	/**
	 * @param dest point for evaluate distance from centroid
	 * @return distance between centroid and {@param dest}
	 */
	public double distance(Point dest)
	{
		Coordinate intersections = DistanceOp.nearestPoints(poly, dest)[0];

		if (intersections == null)
			throw new IllegalArgumentException(String.format("%s must be on border or outside", dest));

		return GeomUtil.metric().dist(intersections, dest.getCoordinate());
	}

	public double distance(PreferredZone zone)
	{
		Coordinate[] coordinates = DistanceOp.nearestPoints(poly, zone.poly);
		return GeomUtil.metric().dist(coordinates[0], coordinates[1]);
	}


	public double distanceInside(Point p1, Point p2)
	{
		if (poly.intersects(p1) && poly.intersects(p2))
		{
			return GeomUtil.metric().dist(p1.getCoordinate(), p2.getCoordinate()) * alpha;
		}
		throw new IllegalArgumentException(String.format("%s and %s must be in bounds", p1, p2));
	}

	public Double distanceToCentroid(Point p)
	{
		Coordinate centroid = poly.getCentroid().getCoordinate();
		LineString segment = GeomUtil.factory().createLineString(new Coordinate[]{
				centroid,
				p.getCoordinate(),
		});
		Coordinate intersectionPoint = GeomUtil.computeIntersectionPoint(GeomUtil.factory(), poly, segment);
		if (intersectionPoint != null)
			return GeomUtil.metric().dist(centroid, intersectionPoint) * alpha
					+ GeomUtil.metric().dist(intersectionPoint, p.getCoordinate());
		else return GeomUtil.metric().dist(centroid, p.getCoordinate()) * alpha;
	}

	@Override
	public double distance(Rectangle r)
	{
		Coordinate[] coordinates = DistanceOp.nearestPoints(poly, AdapterUtil.polygon(GeomUtil.factory(), r));
		return GeomUtil.metric().dist(coordinates[0], coordinates[1]);
	}

	@Override
	public Rectangle mbr()
	{
		return AdapterUtil.rectangle(poly);
	}

	@Override
	public boolean intersects(Rectangle r)
	{
		return poly.intersects(AdapterUtil.polygon(GeomUtil.factory(), r));
	}

	@Override
	public String toString()
	{
		return "PreferredZone{" +
				"alpha=" + alpha +
				", index=" + index +
				'}';
	}


	private static class PolyUtil
	{
		private static boolean isConvex(Polygon polygon)
		{
			Coordinate[] coordinates = polygon.getCoordinates();
			int rotation = angleDirection(coordinates[0], coordinates[1], coordinates[2]);
			for (int i = 1; i < coordinates.length - 2; i++)
			{
				int newRotation = angleDirection(coordinates[i], coordinates[i + 1], coordinates[i + 2]);
				if (rotation == 0)
					rotation = newRotation;
				else if (rotation != newRotation)
					return false;
			}
			return true;
		}

		private static int angleDirection(Coordinate from, Coordinate mid, Coordinate to)
		{
			double v = Angle.angleBetweenOriented(mid, from, to);
			if (v > 0)
				return 1;
			else if (v == 0)
				return 0;
			else return -1;
		}
	}
}
