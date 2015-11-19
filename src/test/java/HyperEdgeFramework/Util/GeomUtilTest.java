package HyperEdgeFramework.Util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;

public class GeomUtilTest
{
	
	@Test
	public void testApplyTransformationOnCoordinateParallelMove() throws Exception
	{
		GeometryFactory factory = new GeometryFactory();
		GeomUtil.Transformation rule = new GeomUtil.Transformation(factory.createPoint(new Coordinate(0, 0)),
				factory.createPoint(new Coordinate(2, 3)));
		rule.transformation = new Pair<>(new Coordinate(1, 1), 0.);


		Coordinate coordinate = new Coordinate(2, 2);
		Coordinate newCoord = rule.transform(coordinate);
		Assert.assertEquals(1, newCoord.x, 0.01);
		Assert.assertEquals(1, newCoord.y, 0.01);
	}

	@Test
	public void testApplyTransformationOnCoordinateRotate1() throws Exception
	{
		GeometryFactory factory = new GeometryFactory();
		GeomUtil.Transformation rule = new GeomUtil.Transformation(factory.createPoint(new Coordinate(0, 0)),
				factory.createPoint(new Coordinate(2, 3)));
		rule.transformation = new Pair<>(new Coordinate(0, 0), Math.PI / 2);


		Coordinate coordinate = new Coordinate(2, 3);
		Coordinate newCoord = rule.transform(coordinate);
		Assert.assertEquals(3, newCoord.x, 0.01);
		Assert.assertEquals(-2, newCoord.y, 0.01);
	}

	@Test
	public void testApplyTransformationOnCoordinateRotate2() throws Exception
	{
		GeometryFactory factory = new GeometryFactory();
		GeomUtil.Transformation rule = new GeomUtil.Transformation(factory.createPoint(new Coordinate(0, 0)),
				factory.createPoint(new Coordinate(2, 3)));
		rule.transformation = new Pair<>(new Coordinate(0, 0), -Math.PI / 2);


		Coordinate coordinate = new Coordinate(2, 3);
		Coordinate newCoord = rule.transform(coordinate);
		Assert.assertEquals(-3, newCoord.x, 0.01);
		Assert.assertEquals(2, newCoord.y, 0.01);
	}

	@Test
	public void testApplyTransformationOnCoordinateRotateAndMove() throws Exception
	{
		GeometryFactory factory = new GeometryFactory();
		GeomUtil.Transformation rule = new GeomUtil.Transformation(factory.createPoint(new Coordinate(0, 0)),
				factory.createPoint(new Coordinate(2, 3)));
		rule.transformation = new Pair<>(new Coordinate(0, -3), -Math.PI / 2);


		Coordinate coordinate = new Coordinate(2, -1);
		Coordinate newCoord = rule.transform(coordinate);
		Assert.assertEquals(-2, newCoord.x, 0.01);
		Assert.assertEquals(2, newCoord.y, 0.01);
	}
}