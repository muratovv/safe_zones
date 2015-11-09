package HyperEdgeFramework;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.math.Vector2D;
import javafx.util.Pair;

/**
 * HyperEdgeFramework.Hyperbola represents in canonical view: x^2 / (a^2) - y^2 / (b^2) = 1,
 * where 2 * a - distance between figures and
 * b^2 = c^2 - a^2, c - distance between centers.
 */
public class GeomUtil
{
	public static Pair<Coordinate, Double> getCircleParameters(Geometry figure)
	{
		Coordinate center = figure.getCentroid().getCoordinate();
		double radius = figure.getCoordinate().distance(center);
		return new Pair<>(center, radius);
	}

	/**
	 * @return Coordinate offset, angle (in rads)
	 */
	public static Pair<Coordinate, Double> getTransformRule(Geometry figure1, Geometry figure2)
	{
		Pair<Coordinate, Double> circle1 = getCircleParameters(figure1);
		Pair<Coordinate, Double> circle2 = getCircleParameters(figure2);
		Double angle = Angle.angle(circle1.getKey(), circle2.getKey());
		Vector2D vector = new Vector2D(circle1.getKey(), circle2.getKey());
		vector = vector.divide(2);
		Coordinate midInVec = vector.toCoordinate();
		return new Pair<>(new Coordinate(circle1.getKey().x + midInVec.x, circle1.getKey().y + midInVec.y), angle);
	}

	public static Geometry transformByRule(GeometryFactory factory, Geometry figure, Pair<Coordinate, Double> transformation)
	{
		Coordinate[] newCoordinates = new Coordinate[figure.getCoordinates().length];
		Coordinate[] coordinates = figure.getCoordinates();
		for (int i = 0; i < coordinates.length; i++)
		{
			newCoordinates[i] = applyTransformationOnCoordinate(figure.getCoordinates()[i], transformation);
		}
		return factory.createPolygon(newCoordinates);
	}

	public static Coordinate applyTransformationOnCoordinate(Coordinate coordinate, Pair<Coordinate, Double> transformation)
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
}
