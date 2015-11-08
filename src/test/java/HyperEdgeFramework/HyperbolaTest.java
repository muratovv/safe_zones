package HyperEdgeFramework;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Assert;
import org.junit.Test;

public class HyperbolaTest
{

	private static GeometryFactory gFactory = new GeometryFactory();

	private static String hyperView(Hyperbola hyperbola, double from, double to, double step)
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

	/**
	 * This method is no test, in fact,
	 * it is text representation of right brunch of hyperbola
	 *
	 * @throws Exception
	 */
	@Test
	public void testHyperView() throws Exception
	{
		double radius = .001;
		double x = .5;
		Geometry c1 = createCircle(new Coordinate(x, 0), radius, 20);
		Geometry c2 = createCircle(new Coordinate(-x, 0), radius, 20);
		Hyperbola hyperbola = Hyperbola.computeHyperbola(gFactory, c1, c2);
		System.out.println(hyperView(hyperbola, -2, 2, 0.05));
	}

	@Test
	public void testInRightBrunch() throws Exception
	{
		double radius = .001;
		double x = .5;
		Geometry c1 = createCircle(new Coordinate(x, 0), radius, 20);
		Geometry c2 = createCircle(new Coordinate(-x, 0), radius, 20);
		Hyperbola hyperbola = Hyperbola.computeHyperbola(gFactory, c1, c2);
		Assert.assertEquals(true, hyperbola.inRightBrunch(c1));
		Assert.assertEquals(false, hyperbola.inRightBrunch(c2));
	}

	@Test
	public void testGetHyperbolaParameters() throws Exception
	{
		double radius = .1;
		double x = .5;
		Geometry c1 = createCircle(new Coordinate(x, 0), radius, 20);
		Geometry c2 = createCircle(new Coordinate(-x, 0), radius, 20);
		Hyperbola hyperbola = Hyperbola.computeHyperbola(gFactory, c1, c2);
		Assert.assertEquals(0.4, hyperbola.getHyperbolaParameters().getKey(), 0.01);
		Assert.assertEquals(0.3, hyperbola.getHyperbolaParameters().getValue(), 0.01);
	}
}
