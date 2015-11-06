import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.index.strtree.GeometryItemDistance;
import com.vividsolutions.jts.index.strtree.ItemDistance;

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
				if (hyperbola.inRightBrunch(new Coordinate(x, y)))
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
}
