package HyperEdgeFramework;

import HyperEdgeFramework.Util.AdapterUtil;
import HyperEdgeFramework.Util.DoubleUtil;
import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Point;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class HyperbolaTest
{

	private static GeometryFactory gFactory = new GeometryFactory();

	private static String hyperView(Hyperbola hyperbola, double from, double to, double step)
	{
		StringBuilder builder = new StringBuilder();
		for (double y = from; DoubleUtil.le(y, to); y += step)
		{
			for (double x = from; DoubleUtil.le(x, to); x += step)
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
		Geometry c1 = createCircle(new Coordinate(-x, 0), radius, 20);
		Geometry c2 = createCircle(new Coordinate(x, 0), radius, 20);
		Hyperbola hyperbola = Hyperbola.create(gFactory, c1, c2);
		System.out.println(hyperView(hyperbola, -2, 2, 0.05));
	}

	@Test
	public void testInRightBrunchEasy() throws Exception
	{
		double radius = .001;
		double x = .5;
		Geometry c1 = createCircle(new Coordinate(x, 0), radius, 20);
		Geometry c2 = createCircle(new Coordinate(-x, 0), radius, 20);
		Hyperbola hyperbola = Hyperbola.create(gFactory, c1, c2);
		Assert.assertEquals(false, hyperbola.inRightBrunch(c1));
		Assert.assertEquals(true, hyperbola.inRightBrunch(c2));
	}

	@Test
	public void testInRightBrunchHard() throws Exception
	{
		int between = 1;
		int quantity = 3;
		ArrayList<Circle> circles = Grid.linearGrid(quantity, Point.create(0, 0), 1, between);
		ArrayList<Polygon> polygons = circles.stream().map(circle ->
				AdapterUtil.polygon(new GeometryFactory(), circle, 3))
				.collect(Collectors.toCollection(ArrayList::new));

		Hyperbola hyperbola = Hyperbola.create(new GeometryFactory(), polygons.get(0), polygons.get(1));
		Assert.assertFalse(hyperbola.inRightBrunch(polygons.get(0)));
		for (int i = 1; i < polygons.size(); i++)
		{
			Polygon polygon = polygons.get(i);
			Assert.assertTrue(hyperbola.inRightBrunch(polygon));
		}
	}

	@Test
	public void testGetHyperbolaParameters() throws Exception
	{
		double radius = .1;
		double x = .5;
		Geometry c1 = createCircle(new Coordinate(-x, 0), radius, 20);
		Geometry c2 = createCircle(new Coordinate(x, 0), radius, 20);
		Hyperbola hyperbola = Hyperbola.create(gFactory, c1, c2);
		Assert.assertEquals(0.4, hyperbola.getHyperbolaParameters().getKey(), 0.01);
		Assert.assertEquals(0.3, hyperbola.getHyperbolaParameters().getValue(), 0.01);
	}

	@Test
	public void testHyperbolaTransformationRule1()
	{
		int between = 1;
		int quantity = 2;
		ArrayList<Circle> circles = Grid.linearGrid(quantity, Point.create(0, 0), 1, between);
		ArrayList<Polygon> polygons = circles.stream().map(circle -> AdapterUtil.polygon(new GeometryFactory(), circle, 1))
				.collect(Collectors.toCollection(ArrayList::new));
		Hyperbola hyperbola = Hyperbola.create(new GeometryFactory(), polygons.get(0), polygons.get(1));
		Assert.assertEquals(1.5, hyperbola.transformationRule.getKey().x, 0.01);
		Assert.assertEquals(0, hyperbola.transformationRule.getKey().y, 0.01);
		Assert.assertEquals(0, hyperbola.transformationRule.getValue(), 0.01);
	}

	@Test
	public void testHyperbolaTransformationRule2()
	{
		Circle circle3 = Circle.create(0, -3, 1);
		Circle circle5 = Circle.create(0, -6, 1);
		Circle circle6 = Circle.create(3, -6, 1);
		Polygon polygon3 = AdapterUtil.polygon(new GeometryFactory(), circle3, 1);
		Polygon polygon5 = AdapterUtil.polygon(new GeometryFactory(), circle5, 1);
		Polygon polygon6 = AdapterUtil.polygon(new GeometryFactory(), circle6, 1);

		Hyperbola hyperbola = Hyperbola.create(new GeometryFactory(), polygon3,
				polygon5);

		Assert.assertTrue(hyperbola.inRightBrunch(polygon5));
		Assert.assertFalse(hyperbola.inRightBrunch(polygon3));
		Assert.assertFalse(hyperbola.inRightBrunch(polygon6));
	}
}
