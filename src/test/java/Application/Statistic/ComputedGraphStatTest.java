package Application.Statistic;

import HyperEdgeFramework.HyperEdgeFlow.Algorithm;
import junit.framework.Assert;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Before;
import org.junit.Test;

public class ComputedGraphStatTest
{
	ComputedGraphStat graphStat;

	@Before
	public void setUp() throws Exception
	{
		SimpleWeightedGraph<Integer, Algorithm.Edge> computedGraph = new SimpleWeightedGraph<>(Algorithm.Edge.class);
		computedGraph.addVertex(1);
		computedGraph.addVertex(2);
		computedGraph.addEdge(1, 2);
		graphStat = new ComputedGraphStat(computedGraph);
	}

	@Test
	public void testGetEdges() throws Exception
	{
		Assert.assertEquals(1, graphStat.getEdges());
	}
}