package HyperEdgeFramework;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Point;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Test;

import java.util.ArrayList;

public class AlgorithmTest
{
	@Test
	public void algorithm1CircleTest()
	{
		ArrayList<Circle> circles = Grid.linearGrid(4, Point.create(0, 0), 1, 1);
		RTree<String, Circle> rTree = RTree.create();
		for (int i = 0; i < circles.size(); i++)
		{
			Circle circle = circles.get(i);
			rTree = rTree.add(new Entry<>(i + "", circle));
		}
		SimpleWeightedGraph<String, HyperAdgeAlgorithm.EdgeWrapper> graph = HyperAdgeAlgorithm.algorithm1Circle(rTree);
		System.out.println(graph);
	}


}
