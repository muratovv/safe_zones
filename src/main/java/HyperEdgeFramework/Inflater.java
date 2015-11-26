package HyperEdgeFramework;

import HyperEdgeFramework.Util.AdapterUtil;
import HyperEdgeFramework.Util.GeomUtil;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Circle;
import com.vividsolutions.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Inflater
{
	private RTree<Integer, PreferredZone> rTree;
	private ArrayList<PreferredZone> notVisited = new ArrayList<>();

	public Inflater(List<PreferredZone> zones)
	{
		notVisited.addAll(zones.stream().collect(Collectors.toList()));
	}

	public static ArrayList<PreferredZone> map(List<Circle> lst, double alpha, int vertexesPerPolygon)
	{
		return lst.stream().map(circle -> new PreferredZone(((Polygon) GeomUtil.getReducer().reduce(AdapterUtil.polygon(GeomUtil.factory(), circle, vertexesPerPolygon))), alpha))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public static ArrayList<Polygon> map(List<Circle> lst, int splits)
	{
		return lst.stream().map(circle -> ((Polygon) GeomUtil.getReducer().reduce(AdapterUtil.polygon(GeomUtil.factory(), circle, splits))))
				.collect(Collectors.toCollection(ArrayList::new));
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

	public Inflater invoke()
	{
		rTree = RTree.create();
		for (PreferredZone preferredZone : notVisited)
		{
			rTree = rTree.add(new Entry<>(preferredZone.getIndex(), preferredZone));
		}
		return this;
	}
}
