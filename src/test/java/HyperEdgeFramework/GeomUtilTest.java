package HyperEdgeFramework;

import HyperEdgeFramework.Util.GeomUtil;
import com.vividsolutions.jts.geom.Coordinate;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;

public class GeomUtilTest
{
	
	@Test
	public void testApplyTransformationOnCoordinateParallelMove() throws Exception
	{
		Coordinate coordinate = new Coordinate(2, 2);
		Pair<Coordinate, Double> transformRule = new Pair<>(new Coordinate(1, 1), 0.);
		Coordinate newCoord = GeomUtil.applyTransformationOnCoordinate(coordinate, transformRule);
		Assert.assertEquals(1, newCoord.x, 0.01);
		Assert.assertEquals(1, newCoord.y, 0.01);
	}

	@Test
	public void testApplyTransformationOnCoordinateRotate1() throws Exception
	{
		Coordinate coordinate = new Coordinate(2, 3);
		Pair<Coordinate, Double> transformRule = new Pair<>(new Coordinate(0, 0), Math.PI / 2);
		Coordinate newCoord = GeomUtil.applyTransformationOnCoordinate(coordinate, transformRule);
		Assert.assertEquals(3, newCoord.x, 0.01);
		Assert.assertEquals(-2, newCoord.y, 0.01);
	}

	@Test
	public void testApplyTransformationOnCoordinateRotate2() throws Exception
	{
		Coordinate coordinate = new Coordinate(2, 3);
		Pair<Coordinate, Double> transformRule = new Pair<>(new Coordinate(0, 0), -Math.PI / 2);
		Coordinate newCoord = GeomUtil.applyTransformationOnCoordinate(coordinate, transformRule);
		Assert.assertEquals(-3, newCoord.x, 0.01);
		Assert.assertEquals(2, newCoord.y, 0.01);
	}

	@Test
	public void testApplyTransformationOnCoordinateRotateAndMove() throws Exception
	{
		Coordinate coordinate = new Coordinate(2, -1);
		Pair<Coordinate, Double> transformRule = new Pair<>(new Coordinate(0, -3), -Math.PI / 2);
		Coordinate newCoord = GeomUtil.applyTransformationOnCoordinate(coordinate, transformRule);
		Assert.assertEquals(-2, newCoord.x, 0.01);
		Assert.assertEquals(2, newCoord.y, 0.01);
	}
}