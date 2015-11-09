package HyperEdgeFramework.Util;

import com.github.davidmoten.rtree.geometry.Circle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AdapterUtilTest
{
	
	@Test
	public void testPolygonCreation() throws Exception
	{
		double x = 101;
		double y = -2023;
		double rad = 21.123;
		Circle circle = Circle.create(x, y, rad);
		Polygon polygon = AdapterUtil.polygon(new GeometryFactory(), circle, 10);
		Coordinate centroid = polygon.getCentroid().getCoordinate();
		assertEquals(centroid.x, x, 0.01);
		assertEquals(centroid.y, y, 0.01);
		assertEquals(centroid.distance(polygon.getCoordinates()[0]), rad, 0.01);
	}
}