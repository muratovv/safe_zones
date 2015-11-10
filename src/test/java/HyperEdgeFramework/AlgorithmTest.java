package HyperEdgeFramework;

import HyperEdgeFramework.HyperEdgeFlow.Algorithm;
import HyperEdgeFramework.HyperEdgeFlow.Inserter;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Point;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class AlgorithmTest
{
	@Test
	public void algorithm1CircleTestLinear()
	{
		ArrayList<Circle> circles = Grid.linearGrid(4, Point.create(0, 0), 1, 1);

		TreeInflater treeInflater = new TreeInflater(circles).invoke();
		RTree<String, Circle> rTree = treeInflater.getRTree();
		ArrayList<Entry<String, Circle>> notVisited = treeInflater.getNotVisited();
		SimpleWeightedGraph<String, Algorithm.EdgeWrapper> graph = Algorithm.algorithm1Circle(rTree, notVisited);
		Assert.assertEquals(4, graph.vertexSet().size());
		Assert.assertEquals(3, graph.edgeSet().size());
		System.out.println(graph);
	}

	@Test
	public void algorithm1CircleTestSquare()
	{
		ArrayList<Circle> circles = Grid.squareGrid(3, Point.create(0, 0), 1, 1);
		circles.remove(4);
		TreeInflater inflater = new TreeInflater(circles).invoke();
		SimpleWeightedGraph<String, Algorithm.EdgeWrapper> graph
				= Algorithm.algorithm1Circle(inflater.getRTree(), inflater.getNotVisited());
		Assert.assertEquals(8, graph.vertexSet().size());
		Assert.assertEquals(14, graph.edgeSet().size());
		System.out.println(graph);
	}

	@Test
	public void testInsert()
	{
		ArrayList<Circle> circles = Grid.squareGrid(3, Point.create(0, 0), 1, 1);
		circles.remove(4);
		TreeInflater inflater = new TreeInflater(circles).invoke();
		SimpleWeightedGraph<String, Algorithm.EdgeWrapper> graph
				= Algorithm.algorithm1Circle(inflater.getRTree(), inflater.getNotVisited());

		Inserter.insert(graph, inflater.getNotVisited(), Entry.entry("s", Point.create(-2, -3)));
		Assert.assertEquals(3, graph.edgesOf("s").size());
		Assert.assertNotNull(graph.getEdge("s", "3"));
		Assert.assertNotNull(graph.getEdge("s", "0"));
		Assert.assertNotNull(graph.getEdge("s", "5"));
		System.out.println(graph);
	}

	public class TreeInflater
	{
		private ArrayList<Circle> circles;
		private RTree<String, Circle> rTree;
		private ArrayList<Entry<String, Circle>> notVisited;

		public TreeInflater(ArrayList<Circle> circles) {this.circles = circles;}

		public RTree<String, Circle> getRTree()
		{
			return rTree;
		}

		public ArrayList<Entry<String, Circle>> getNotVisited()
		{
			return ((ArrayList<Entry<String, Circle>>) notVisited.clone());
		}

		public TreeInflater invoke()
		{
			rTree = RTree.create();
			notVisited = new ArrayList<>();
			for (int i = 0; i < circles.size(); i++)
			{
				Circle circle = circles.get(i);
				rTree = rTree.add(new Entry<>(i + "", circle));
				notVisited.add(new Entry<>(i + "", circle));
			}
			return this;
		}
	}
}
