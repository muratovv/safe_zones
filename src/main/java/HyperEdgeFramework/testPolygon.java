package HyperEdgeFramework;

import org.jgrapht.graph.WeightedMultigraph;

/**
 * Created by Muratov on 08.11.15.
 */
public class testPolygon
{
	public static void main(String[] args)
	{
		WeightedMultigraph<String, HyperEdgeAlgorithm.EdgeWrapper> graph = new WeightedMultigraph<>(HyperEdgeAlgorithm.EdgeWrapper.class);
		System.out.println(graph.addVertex("1"));
		System.out.println(graph.addVertex("2"));
		System.out.println(graph.addVertex("3"));
		System.out.println(graph.addEdge("1", "2", new HyperEdgeAlgorithm.EdgeWrapper(11)));
		System.out.println(graph.addEdge("2", "3", new HyperEdgeAlgorithm.EdgeWrapper(11)));
		System.out.println(graph);
	}
}
