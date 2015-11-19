package HyperEdgeFramework;

import HyperEdgeFramework.Util.DoubleUtil;
import HyperEdgeFramework.Util.GeomUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class Hyperbola
{


	PreferredZone from;
	PreferredZone to;

	double static_distance;


	private Hyperbola(PreferredZone from, PreferredZone to)
	{
		this.from = from;
		this.to = to;
		if (!checkDistance(from.getPoly(), to.getPoly()))
			throw new IllegalArgumentException(String.format("Distance between %s and %s must be positive", from, to));
		compute_distance();
	}

	/**
	 * @param from compute from
	 * @param to   compute to, getting hyperbola brunch closer to this
	 * @return computed hyperbola
	 * @throws IllegalArgumentException if distance between figures equal zero
	 */
	public static Hyperbola create(PreferredZone from, PreferredZone to)
	{
		return new Hyperbola(from, to);
	}

	private static boolean checkDistance(Geometry fig1, Geometry fig2)
	{
		double distance = fig1.distance(fig2);
		return DoubleUtil.g(distance, 0);
	}

	private void compute_distance()
	{
		Coordinate c1 = from.getPoly().getCentroid().getCoordinate();
		Coordinate c2 = to.getPoly().getCentroid().getCoordinate();

		LineString segment = GeomUtil.factory().createLineString(new Coordinate[]{c1, c2});

		Coordinate fromBound = GeomUtil.computeIntersectionPoint(GeomUtil.factory(), from.getPoly(), segment);
		Coordinate toBound = GeomUtil.computeIntersectionPoint(GeomUtil.factory(), to.getPoly(), segment);

		double fromDist = fromBound != null ? GeomUtil.metric().dist(c1, fromBound) * from.alpha : 0;
		double toDist = GeomUtil.metric().dist(c2, toBound) * to.alpha;
		static_distance = fromDist + toDist + GeomUtil.metric().dist(fromBound != null ? fromBound : c1, toBound);
	}

	public boolean inRightBrunch(Geometry figure)
	{
		for (Coordinate coordinate : figure.getCoordinates())
		{
			if (!inRightBrunch(coordinate))
				return false;
		}
		return true;
	}

	public boolean inRightBrunch(Coordinate coordinate)
	{
		Point point = GeomUtil.factory().createPoint(coordinate);
		Double fromDist = from.distanceToCentroid(point);
		Double toDist = to.distanceToCentroid(point);
		return DoubleUtil.ge(fromDist - toDist, static_distance);
	}


}
