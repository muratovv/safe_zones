import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.math.Vector2D;
import javafx.util.Pair;

/**
 * Hyperbola represents in canonical view: x^2 / (a^2) - y^2 / (b^2) = 1,
 * where 2 * a - distance between figures and
 * b^2 = c^2 - a^2, c - distance between centers.
 */
public class GeomUtil
{
	public static Pair<Coordinate, Double> getCircleParameters(Geometry figure)
	{
		Coordinate center = figure.getCentroid().getCoordinate();
		double radius = figure.getCoordinate().distance(center);
		return new Pair<Coordinate, Double>(center, radius);
	}

	/**
	 * @return Coordinate offset, angle (in rads)
	 */
	public static Pair<Coordinate, Double> getTransformRule(Geometry figure1, Geometry figure2)
	{
		Pair<Coordinate, Double> circle1 = getCircleParameters(figure1);
		Pair<Coordinate, Double> circle2 = getCircleParameters(figure2);
		Double angle = Angle.angle(circle2.getKey(), circle1.getKey());
		Vector2D vector = new Vector2D(circle1.getKey(), circle2.getKey());
		vector = vector.divide(2);
		Coordinate midInVec = vector.toCoordinate();
		return new Pair<Coordinate, Double>(
				new Coordinate(circle1.getKey().x + midInVec.x, circle1.getKey().y + midInVec.y),
				angle);
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
		double xBar = coordinate.x * Math.cos(transformation.getValue())
				+ coordinate.y * Math.sin(transformation.getValue());
		double yBar = -1 * coordinate.x * Math.sin(transformation.getValue())
				+ coordinate.y * Math.cos(transformation.getValue());
		return new Coordinate(xBar, yBar);
	}

	public static Pair<Coordinate, Double> getReverseTransformRule(Pair<Coordinate, Double> rule)
	{
		return new Pair<Coordinate, Double>(
				new Coordinate(-rule.getKey().x, -rule.getKey().y),
				-rule.getValue());
	}
}
