package Application.Statistic;

import HyperEdgeFramework.Grid;
import HyperEdgeFramework.Inflater;
import HyperEdgeFramework.PreferredZone;
import HyperEdgeFramework.Util.DoubleUtil;
import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Point;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class SourceDataSetTest
{
	SourceDataSet sourceDataSet;

	@Before
	public void setUp()
	{
		ArrayList<Circle> circles = Grid.linearGrid(2, Point.create(0, 0), 1, 1);
		ArrayList<PreferredZone> zones = Inflater.map(circles, 0, 20);
		sourceDataSet = new SourceDataSet(zones);
	}
	
	@Test
	public void testQuantityOfZones() throws Exception
	{
		Assert.assertEquals(2, sourceDataSet.quantityOfZones());
	}

	@Test
	public void testZonesArea() throws Exception
	{
		Assert.assertEquals(2 * Math.PI * 1, sourceDataSet.zonesArea(), .01);
	}

	@Test
	public void testAllArea() throws Exception
	{
		Assert.assertEquals(10., sourceDataSet.allArea(), DoubleUtil.eps);
	}

	@Test
	public void testMaxEdges() throws Exception
	{
		Assert.assertEquals(1, sourceDataSet.maxEdges());
	}

	@Test
	public void testAveragePolygonSize() throws Exception
	{
		Assert.assertEquals(81, sourceDataSet.averagePolygonSize(), DoubleUtil.eps);
	}
}