package HyperEdgeFramework;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Test;

import java.util.ArrayList;

public class AlgorithmTest
{
	@Test
	public void algorithm1CircleTestLinear()
	{
		ArrayList<Circle> circles = Grid.linearGrid(4, Point.create(0, 0), 1, 1);

		RTree<String, Circle> rTree = RTree.create();
		ArrayList<Entry<String, Geometry>> notVisited = new ArrayList<>();
		for (int i = 0; i < circles.size(); i++)
		{
			Circle circle = circles.get(i);
			rTree = rTree.add(new Entry<>(i + "", circle));
			notVisited.add(new Entry<>(i + "", circle));
		}
		SimpleWeightedGraph<String, HyperEdgeAlgorithm.EdgeWrapper> graph = HyperEdgeAlgorithm.algorithm1Circle(rTree, notVisited);
		System.out.println(graph);
	}

	@Test
	public void algorithm1CircleTestSquare()
	{
		ArrayList<Circle> circles = Grid.squareGrid(3, Point.create(0, 0), 1, 1);
		circles.remove(4);
		RTree<String, Circle> rTree = RTree.create();
		ArrayList<Entry<String, Geometry>> notVisited = new ArrayList<>();
		for (int i = 0; i < circles.size(); i++)
		{
			Circle circle = circles.get(i);
			rTree = rTree.add(new Entry<>(i + "", circle));
			notVisited.add(new Entry<>(i + "", circle));
		}
		SimpleWeightedGraph<String, HyperEdgeAlgorithm.EdgeWrapper> graph = HyperEdgeAlgorithm.algorithm1Circle(rTree, notVisited);
		System.out.println(graph);
	}


}
