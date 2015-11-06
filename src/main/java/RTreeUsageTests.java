import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.index.strtree.GeometryItemDistance;
import com.vividsolutions.jts.index.strtree.ItemDistance;
import com.vividsolutions.jts.math.Vector2D;
import javafx.util.Pair;

public class RTreeUsageTests
{
	private static GeometryFactory gFactory = new GeometryFactory();
	private static ItemDistance distance = new GeometryItemDistance();

	public static void main(String[] args)
	{
		double radius = .9;
		double x = 1.;
		Geometry c1 = createCircle(new Coordinate(x, 0), radius, 20);
		Geometry c2 = createCircle(new Coordinate(-x, 0), radius, 20);
		Hyperbola hyperbola = Hyperbola.computeHyperbola(gFactory, c1, c2);
		System.out.println(hyperbola.inRightBrunch(c1) + " " + hyperbola.inRightBrunch(c2));
		System.out.println(hyperTest(hyperbola, -2, 2, 0.05));
	}

	private static String hyperTest(Hyperbola hyperbola, double from, double to, double step)
	{
		StringBuilder builder = new StringBuilder();
		for (double y = from; y <= to; y += step)
		{
			for (double x = from; x <= to; x += step)
			{
				if(hyperbola.inRightBrunch(new Coordinate(x, y)))
					builder.append('+');
				else builder.append(' ');
			}
			builder.append('\n');
		}
		return builder.toString();
	}

	static Geometry createCircle(Coordinate centre, double radius, int polygonSize)
	{
		Geometry centrePt = gFactory.createPoint(centre);
		return centrePt.buffer(radius, polygonSize);
	}


	/**
	 * Hyperbola represents in canonical view: x^2 / (a^2) - y^2 / (b^2) = 1,
	 * where 2 * a - distance between figures and
	 * b^2 = c^2 - a^2, c - distance between centers.
	 */
	private static class Hyperbola
	{
		static Hyperbola computeHyperbola(GeometryFactory factory, Geometry figure1, Geometry figure2)
		{
			return new Hyperbola(factory, figure1, figure2);
		}

		double a;
		double b;

		Pair<Coordinate, Double> transformationRule;

		private Hyperbola(GeometryFactory gFactory, Geometry figure1, Geometry figure2)
		{
			transformationRule = GeomUtil.getTransformRule(figure1, figure2);
			Geometry fig1 = GeomUtil.transformByRule(gFactory, (Geometry) figure1.clone(), transformationRule);
			Geometry fig2 = GeomUtil.transformByRule(gFactory, ((Geometry) figure2.clone()), transformationRule);
			if (!checkDistance(fig1, fig2))
				throw new IllegalArgumentException("Distance between figures must be greater zero");
			a = computeA(fig1, fig2);
			b = computeB(fig1, fig2);
		}

		private static double computeA(Geometry figure1, Geometry figure2)
		{
			return figure1.distance(figure2) / 2;
		}

		private static double computeB(Geometry figure1, Geometry figure2)
		{
			return Math.pow((Math.pow(figure1.getCentroid().getCoordinate().distance(figure2.getCentroid().getCoordinate()), 2))
					- (Math.pow(figure1.distance(figure2), 2)), 0.5);
		}

		private static boolean checkDistance(Geometry fig1, Geometry fig2)
		{
			return fig1.distance(fig2) > 0;
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
			Coordinate cBar = GeomUtil.applyTransformationOnCoordinate(coordinate, transformationRule);
			if (cBar.x > 0 && (cBar.x * cBar.x / (a * a)) - (cBar.y * cBar.y / (b * b)) >= 1)
			{
				return true;
			} else return false;
		}

		/**
		 * @return (a, b) parameters
		 */
		public Pair<Double, Double> getHyperbolaParameters()
		{
			return new Pair<Double, Double>(a, b);
		}
	}


	private static class GeomUtil
	{
		private static Pair<Coordinate, Double> getCircleParameters(Geometry figure)
		{
			Coordinate center = figure.getCentroid().getCoordinate();
			double radius = figure.getCoordinate().distance(center);
			return new Pair<Coordinate, Double>(center, radius);
		}

		/**
		 * @return Coordinate offset, angle (in rads)
		 */
		private static Pair<Coordinate, Double> getTransformRule(Geometry figure1, Geometry figure2)
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

		private static Geometry transformByRule(GeometryFactory factory, Geometry figure, Pair<Coordinate, Double> transformation)
		{
			Coordinate[] newCoordinates = new Coordinate[figure.getCoordinates().length];
			Coordinate[] coordinates = figure.getCoordinates();
			for (int i = 0; i < coordinates.length; i++)
			{
				newCoordinates[i] = applyTransformationOnCoordinate(figure.getCoordinates()[i], transformation);
			}
			return factory.createPolygon(newCoordinates);
		}

		private static Coordinate applyTransformationOnCoordinate(Coordinate coordinate, Pair<Coordinate, Double> transformation)
		{
			double xBar = coordinate.x * Math.cos(transformation.getValue())
					+ coordinate.y * Math.sin(transformation.getValue());
			double yBar = -1 * coordinate.x * Math.sin(transformation.getValue())
					+ coordinate.y * Math.cos(transformation.getValue());
			return new Coordinate(xBar, yBar);
		}

		private static Pair<Coordinate, Double> getReverseTransformRule(Pair<Coordinate, Double> rule)
		{
			return new Pair<Coordinate, Double>(
					new Coordinate(-rule.getKey().x, -rule.getKey().y),
					-rule.getValue());
		}
	}
}
