package HyperEdgeFramework;

import HyperEdgeFramework.Util.AdapterUtil;
import HyperEdgeFramework.Util.DoubleUtil;
import HyperEdgeFramework.Util.GeomUtil;
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
		return GeomUtil.getReducer().reduce(centrePt.buffer(radius, polygonSize));
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
		double radius = .11;
		double x = 1;
		Geometry c1 = createCircle(new Coordinate(-x, 0), radius, 3);
		Geometry c2 = createCircle(new Coordinate(x, 0), radius, 3);
		PreferredZone z1 = new PreferredZone(((Polygon) c1), 0);
		PreferredZone z2 = new PreferredZone(((Polygon) c2), 0);
		Hyperbola hyperbola = Hyperbola.create(z1, z2);
		System.out.println(hyperView(hyperbola, -2, 5, 0.05));
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

		PreferredZone z1 = new PreferredZone(polygons.get(0), 0);
		PreferredZone z2 = new PreferredZone(polygons.get(1), 0);
		Hyperbola hyperbola = Hyperbola.create(z1, z2);
		Assert.assertFalse(hyperbola.inRightBrunch(polygons.get(0)));
		for (int i = 1; i < polygons.size(); i++)
		{
			Polygon polygon = polygons.get(i);
			Assert.assertTrue(hyperbola.inRightBrunch(polygon));
		}
	}

	@Test
	public void testHyperbolaRightBrunch() throws Exception
	{
		Circle circle3 = Circle.create(0, 0, 1);
		Circle circle5 = Circle.create(3, 0, 1);
		Polygon polygon3 = AdapterUtil.polygon(new GeometryFactory(), circle3, 3);
		Polygon polygon5 = AdapterUtil.polygon(new GeometryFactory(), circle5, 3);

		PreferredZone z1 = new PreferredZone(polygon3, 0);
		PreferredZone z2 = new PreferredZone(polygon5, 0);
		Hyperbola hyperbola = Hyperbola.create(z1, z2);
		Assert.assertFalse(hyperbola.inRightBrunch(z1.getPoly()));
		Assert.assertTrue(hyperbola.inRightBrunch(new Coordinate(2, 0)));
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

		PreferredZone z1 = new PreferredZone(polygon3, 0);
		PreferredZone z2 = new PreferredZone(polygon5, 0);
		Hyperbola hyperbola = Hyperbola.create(z1, z2);

		Assert.assertTrue(hyperbola.inRightBrunch(polygon5));
		Assert.assertFalse(hyperbola.inRightBrunch(polygon3));
		Assert.assertFalse(hyperbola.inRightBrunch(polygon6));
	}

	@Test
	public void testHyperbolaStaticDistance() throws Exception
	{
		Circle circle0 = Circle.create(0, 0, 1);
		Circle circle5 = Circle.create(0, 5, 1);
		Polygon polygon0 = AdapterUtil.polygon(new GeometryFactory(), circle0, 3);
		Polygon polygon5 = AdapterUtil.polygon(new GeometryFactory(), circle5, 3);
		PreferredZone z1 = new PreferredZone(polygon0, .5);
		PreferredZone z2 = new PreferredZone(polygon5, .5);
		Hyperbola hyperbola = Hyperbola.create(z1, z2);
		Assert.assertEquals(4., hyperbola.static_distance, DoubleUtil.eps);
	}
}
