import HyperEdgeFramework.PreferredZone;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.index.strtree.GeometryItemDistance;
import com.vividsolutions.jts.index.strtree.ItemDistance;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PreferredZoneTest
{

	private static ItemDistance distance = new GeometryItemDistance();
	GeometryFactory factory = new GeometryFactory();
	Coordinate[] coordinates = {
			new Coordinate(2, 1),
			new Coordinate(1, 2),
			new Coordinate(2, 4),
			new Coordinate(5, 4),
			new Coordinate(6, 2),
			new Coordinate(5, 1),
			new Coordinate(2, 1),
	};

	@Test
	public void testCreation() throws Exception
	{
		Polygon polygon1 = factory.createPolygon(coordinates);
		//creating ok zone
		PreferredZone zone1 = new PreferredZone(polygon1, "0", 0);
		Coordinate[] coordinatesB = {
				new Coordinate(2, 1),
				new Coordinate(1, 2),
				new Coordinate(2, 4),
				new Coordinate(3, 3),
				new Coordinate(5, 4),
				new Coordinate(6, 2),
				new Coordinate(5, 1),
				new Coordinate(2, 1),
		};
		Polygon polygon2 = factory.createPolygon(coordinatesB);
		try
		{
			//creating non convex zone, await illegal argument exception
			PreferredZone zone2 = new PreferredZone(polygon2, "0", 0);
			throw new Exception();
		} catch (IllegalArgumentException ignored)
		{
		}
	}

	@Test
	public void testInternalDistance() throws Exception
	{
		PreferredZone zone = new PreferredZone(factory.createPolygon(coordinates), "0", 0);
		assertEquals(0, zone.internalDistance(distance, coordinates[1], coordinates[4]), 0.01);
		zone = new PreferredZone(factory.createPolygon(coordinates), "0", .5);
		assertEquals(2.5, zone.internalDistance(distance, coordinates[1], coordinates[4]), 0.01);
	}
}