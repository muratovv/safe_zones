package HyperEdgeFramework.Util;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.math.Vector2D;
import javafx.util.Pair;

/**
 * HyperEdgeFramework.Hyperbola represents in canonical view: x^2 / (a^2) - y^2 / (b^2) = 1,
 * where 2 * a - distance between figures and
 * b^2 = c^2 - a^2, c - distance between centers.
 */
public class GeomUtil
{

	// StaticZone /////////////////////////////////////////////////////////////
	private static GeometryFactory gFactory = new GeometryFactory();
	private static GeomUtil.Metric metric = new GeomUtil.Metric.Eucludean();

	public static GeometryFactory factory()
	{
		return gFactory;
	}

	public static GeomUtil.Metric metric()
	{
		return metric;
	}

	public static void setMetric(GeomUtil.Metric metric)
	{
		GeomUtil.metric = metric;
	}

	public static void setFactory(GeometryFactory gFactory)
	{
		GeomUtil.gFactory = gFactory;
	}
	///////////////////////////////////////////////////////////////////////////

	public static Pair<Coordinate, Double> getCircleParameters(Geometry figure)
	{
		Coordinate center = figure.getCentroid().getCoordinate();
		double radius = figure.getCoordinate().distance(center);
		return new Pair<>(center, radius);
	}

	public static Coordinate computeIntersectionPoint(GeometryFactory gFactory, Polygon polygon, LineString segment)
	{
		Coordinate[] coordinates = polygon.intersection(segment).getCoordinates();

		assert coordinates.length == 2;

		for (Coordinate coordinate : coordinates)
		{
			if (!DoubleUtil.q(polygon.getCentroid().distance(gFactory.createPoint(coordinate)), 0))
			{
				return coordinate;
			}
		}
		return null;
	}

	public interface Metric
	{
		double dist(Coordinate c1, Coordinate c2);


		class Eucludean implements Metric
		{
			@Override
			public double dist(Coordinate c1, Coordinate c2)
			{
				return Math.sqrt((c1.x - c2.x) * (c1.x - c2.x) + (c1.y - c2.y) * (c1.y - c2.y));
			}

		}
	}


	public static class Transformation
	{
		Pair<Coordinate, Double> transformation;

		public Transformation(Geometry figure1, Geometry figure2)
		{
			Pair<Coordinate, Double> transformRule = getTransformRule(figure1, figure2);
			transformation = new Pair<>(transformRule.getKey(), transformRule.getValue());
		}

		/**
		 * @return Coordinate offset, angle (in rads)
		 */
		private static Pair<Coordinate, Double> getTransformRule(Geometry figure1, Geometry figure2)
		{
			Pair<Coordinate, Double> circle1 = getCircleParameters(figure1);
			Pair<Coordinate, Double> circle2 = getCircleParameters(figure2);
			Double angle = Angle.angle(circle1.getKey(), circle2.getKey());
			Vector2D vector = new Vector2D(circle1.getKey(), circle2.getKey());
			vector = vector.divide(2);
			Coordinate midInVec = vector.toCoordinate();
			return new Pair<>(new Coordinate(circle1.getKey().x + midInVec.x, circle1.getKey().y + midInVec.y), angle);
		}

		public Pair<Coordinate, Double> getTransformation()
		{
			return new Pair<>(((Coordinate) transformation.getKey().clone()), transformation.getValue());
		}

		public Coordinate transform(Coordinate coordinate)
		{
			double alpha = transformation.getValue();
			double xDiff = coordinate.x - transformation.getKey().x;
			double yDiff = coordinate.y - transformation.getKey().y;

			double xBar = xDiff * Math.cos(alpha)
					+ yDiff * Math.sin(alpha);

			double yBar = -xDiff * Math.sin(alpha)
					+ yDiff * Math.cos(alpha);

			return new Coordinate(xBar, yBar);
		}

		public Geometry transform(GeometryFactory factory, Geometry figure)
		{
			Coordinate[] newCoordinates = new Coordinate[figure.getCoordinates().length];
			Coordinate[] coordinates = figure.getCoordinates();
			for (int i = 0; i < coordinates.length; i++)
			{
				newCoordinates[i] = transform(figure.getCoordinates()[i]);
			}
			return factory.createPolygon(newCoordinates);
		}

	}
}
