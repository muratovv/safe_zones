package HyperEdgeFramework;

import HyperEdgeFramework.HyperEdgeFlow.Algorithm;
import HyperEdgeFramework.HyperEdgeFlow.Inserter;
import HyperEdgeFramework.Util.AdapterUtil;
import HyperEdgeFramework.Util.GeomUtil;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Point;
import com.vividsolutions.jts.geom.Coordinate;
import javafx.util.Pair;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AlgorithmTest
{
	double alpha;

	@Test
	public void algorithm1CircleTestLinear()
	{
		ArrayList<Circle> circles = Grid.linearGrid(4, Point.create(0, 0), 1, 1);

		TreeInflater treeInflater = new TreeInflater(map(circles)).invoke();
		RTree<Integer, PreferredZone> rTree = treeInflater.getRTree();
		ArrayList<PreferredZone> notVisited = treeInflater.getNotVisited();
		SimpleWeightedGraph<Integer, Algorithm.Edge> graph = Algorithm.hyperEdgeAlgorithm(rTree, notVisited);
		System.out.println(graph);
		Assert.assertEquals(4, graph.vertexSet().size());
		Assert.assertEquals(3, graph.edgeSet().size());
	}

	@Test
	public void algorithm1CircleTestSquare()
	{
		ArrayList<Circle> circles = Grid.squareGrid(3, Point.create(0, 0), 1, 1);
		circles.remove(4);
		TreeInflater inflater = new TreeInflater(map(circles)).invoke();
		SimpleWeightedGraph<Integer, Algorithm.Edge> graph
				= Algorithm.hyperEdgeAlgorithm(inflater.getRTree(), inflater.getNotVisited());
		Assert.assertEquals(8, graph.vertexSet().size());
		Assert.assertEquals(14, graph.edgeSet().size());
		System.out.println(graph);
	}

	@Test
	public void testInsert()
	{
		ArrayList<Circle> circles = Grid.squareGrid(3, Point.create(0, 0), 1, 1);
		circles.remove(4);
		TreeInflater inflater = new TreeInflater(map(circles)).invoke();
		SimpleWeightedGraph<Integer, Algorithm.Edge> graph
				= Algorithm.hyperEdgeAlgorithm(inflater.getRTree(), inflater.getNotVisited());

		Inserter.insert(graph, inflater.getNotVisited(), new Pair<>(-1, GeomUtil.factory().createPoint(new Coordinate(-2, -3))));
		System.out.println(graph);
		Assert.assertEquals(3, graph.edgesOf(-1).size());


	}

	@Test
	public void testShortestPath() throws Exception
	{
		ArrayList<Circle> circles = Grid.squareGrid(3, Point.create(0, 0), 1, 1);
		circles.remove(4);
		TreeInflater inflater = new TreeInflater(map(circles)).invoke();
		SimpleWeightedGraph<Integer, Algorithm.Edge> graph
				= Algorithm.hyperEdgeAlgorithm(inflater.getRTree(), inflater.getNotVisited());
		DijkstraShortestPath<Integer, Algorithm.Edge> shortestPath = new DijkstraShortestPath<>(graph, 0, 7);
		Assert.assertEquals(4, shortestPath.getPathEdgeList().size());
	}

	private List<PreferredZone> map(List<Circle> lst)
	{
		return lst.stream().map(circle -> new PreferredZone(AdapterUtil.polygon(GeomUtil.factory(), circle, 4), alpha))
				.collect(Collectors.toCollection(ArrayList::new));
	}

}
