import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import javafx.util.Pair;

public class Hyperbola
{
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

	static Hyperbola computeHyperbola(GeometryFactory factory, Geometry figure1, Geometry figure2)
	{
		return new Hyperbola(factory, figure1, figure2);
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
		return cBar.x > 0 && (cBar.x * cBar.x / (a * a)) - (cBar.y * cBar.y / (b * b)) >= 1;
	}

	/**
	 * @return (a, b) parameters
	 */
	public Pair<Double, Double> getHyperbolaParameters()
	{
		return new Pair<Double, Double>(a, b);
	}
}
