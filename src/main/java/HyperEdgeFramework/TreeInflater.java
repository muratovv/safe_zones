package HyperEdgeFramework;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Circle;

import java.util.ArrayList;

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
