package HyperEdgeFramework;

import HyperEdgeFramework.Util.AdapterUtil;
import HyperEdgeFramework.Util.DoubleUtil;
import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Point;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class PreferredZoneTest
{


	@Test
	public void testDistance() throws Exception
	{
		GeometryFactory factory = new GeometryFactory();
		Polygon polygon = factory.createPolygon(new Coordinate[]{
				new Coordinate(0, 0),
				new Coordinate(1, 0),
				new Coordinate(1, 1),
				new Coordinate(0, 1),
				new Coordinate(0, 0),
		});
		PreferredZone preferredZone = new PreferredZone(polygon, .5);
		Assert.assertEquals(1, preferredZone.distance(factory.createPoint(new Coordinate(-1, 0))), DoubleUtil.eps);
	}

	@Test
	public void testDistanceInside() throws Exception
	{
		GeometryFactory factory = new GeometryFactory();
		Polygon polygon = factory.createPolygon(new Coordinate[]{
				new Coordinate(0, 0),
				new Coordinate(1, 0),
				new Coordinate(1, 1),
				new Coordinate(0, 1),
				new Coordinate(0, 0),
		});
		PreferredZone preferredZone = new PreferredZone(polygon, 0.5);
		Assert.assertEquals(Math.sqrt(2) / 2, preferredZone.distanceInside(factory.createPoint(new Coordinate(1, 1)),
				factory.createPoint(new Coordinate(0, 0))), DoubleUtil.eps);
	}

	@Test
	public void testDistanceToCentroid() throws Exception
	{
		ArrayList<Circle> circles = Grid.linearGrid(1, Point.create(0, 0), 1, 0);
		Polygon polygon = AdapterUtil.polygon(new GeometryFactory(), circles.get(0), 5);
		PreferredZone preferredZone = new PreferredZone(polygon, 0.5);
		Assert.assertEquals(1.5,
				preferredZone.distanceToCentroid(new GeometryFactory().createPoint(new Coordinate(2, 0))),
				DoubleUtil.eps);
	}
}