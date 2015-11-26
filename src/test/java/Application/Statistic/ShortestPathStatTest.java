package Application.Statistic;

import HyperEdgeFramework.HyperEdgeFlow.Algorithm;
import HyperEdgeFramework.Util.DoubleUtil;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ShortestPathStatTest
{
	ShortestPathStat stat;

	@Before
	public void setUp() throws Exception
	{
		SimpleWeightedGraph<Integer, Algorithm.Edge> graph = new SimpleWeightedGraph<>(Algorithm.Edge.class);
		graph.addVertex(1);
		graph.addVertex(2);
		graph.addVertex(3);
		graph.addVertex(-1);
		graph.addVertex(-2);
		graph.addEdge(1, 2);
		graph.addEdge(2, 3);
		graph.addEdge(-1, 1);
		graph.addEdge(-2, 3);
		DijkstraShortestPath<Integer, Algorithm.Edge> shortestPath = new DijkstraShortestPath<>(graph, -1, -2);
		stat = new ShortestPathStat(shortestPath, 1);
	}

	@Test
	public void testGetDistance() throws Exception
	{
		Assert.assertEquals(1, stat.getDistance(), DoubleUtil.eps);
	}

	@Test
	public void testGetUsedZones() throws Exception
	{
		Assert.assertEquals(4, stat.getUsedZones());
	}
}