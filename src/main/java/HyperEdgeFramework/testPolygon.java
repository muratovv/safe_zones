package HyperEdgeFramework;

import HyperEdgeFramework.HyperEdgeFlow.Algorithm;
import org.jgrapht.graph.WeightedMultigraph;

public class testPolygon
{
	public static void main(String[] args)
	{
		WeightedMultigraph<String, Algorithm.EdgeWrapper> graph = new WeightedMultigraph<>(Algorithm.EdgeWrapper.class);
		System.out.println(graph.addVertex("1"));
		System.out.println(graph.addVertex("2"));
		System.out.println(graph.addVertex("3"));
		System.out.println(graph.addEdge("1", "2", new Algorithm.EdgeWrapper(11)));
		System.out.println(graph.addEdge("2", "3", new Algorithm.EdgeWrapper(11)));
		System.out.println(graph);
	}
}
