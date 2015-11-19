package HyperEdgeFramework;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TreeInflater
{
	private RTree<Integer, PreferredZone> rTree;
	private ArrayList<PreferredZone> notVisited = new ArrayList<>();

	public TreeInflater(List<PreferredZone> zones)
	{
		notVisited.addAll(zones.stream().collect(Collectors.toList()));
	}

	public RTree<Integer, PreferredZone> getRTree()
	{
		return rTree;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<PreferredZone> getNotVisited()
	{
		return (ArrayList<PreferredZone>) notVisited.clone();
	}

	public TreeInflater invoke()
	{
		rTree = RTree.create();
		for (PreferredZone preferredZone : notVisited)
		{
			rTree = rTree.add(new Entry<>(preferredZone.getIndex(), preferredZone));
		}
		return this;
	}
}
