package Application.Statistic;

import HyperEdgeFramework.HyperEdgeFlow.Algorithm;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.Set;

/**
 * Getting statistic of computed graph
 */
public class ComputedGraphStat
{
	SimpleWeightedGraph<Integer, Algorithm.Edge> graph;

	int edges;

	public ComputedGraphStat(SimpleWeightedGraph<Integer, Algorithm.Edge> graph)
	{
		this.graph = graph;

		Set<Integer> vertexes = graph.vertexSet();
		for (Integer vertex : vertexes)
		{
			edges += graph.degreeOf(vertex);
		}
		edges = edges / 2;
	}

	public int getEdges()
	{
		return edges;
	}
}
