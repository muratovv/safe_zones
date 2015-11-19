package HyperEdgeFramework.HyperEdgeFlow;

import HyperEdgeFramework.Hyperbola;
import HyperEdgeFramework.PreferredZone;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import org.jgrapht.graph.SimpleWeightedGraph;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

public class Algorithm
{
	public static SimpleWeightedGraph<Integer, EdgeWrapper> algorithm1Circle(RTree<Integer, PreferredZone> rtree, List<PreferredZone> zones)
	{
		SimpleWeightedGraph<Integer, EdgeWrapper> graph = new SimpleWeightedGraph<>(EdgeWrapper.class);
		while (zones.size() > 1)
		{
			PreferredZone vZone = zones.remove(0);
			graph.addVertex(vZone.getIndex());

			ArrayList<Hyperbola> hyperbolas = new ArrayList<>();

			int currentNearest = 2;

			while (true)
			{
				Observable<Entry<Integer, PreferredZone>> nearest = rtree.nearest(vZone.mbr(),
						Double.POSITIVE_INFINITY,
						currentNearest);
				List<Entry<Integer, PreferredZone>> nearestList = nearest.toList().toBlocking().single();

				if (nearestList.size() <= currentNearest - 1)
					break;

				PreferredZone uZone = nearestList.get(currentNearest - 1).geometry();

				if (!anyCover(hyperbolas, uZone))
				{
					graph.addVertex(uZone.getIndex());
					graph.addEdge(vZone.getIndex(), uZone.getIndex(), new EdgeWrapper<>(vZone.distance(uZone)));
					hyperbolas.add(Hyperbola.create(vZone, uZone));

					currentNearest++;
				} else
				{
					break;
				}
			}
		}
		return graph;
	}


	public static boolean anyCover(List<Hyperbola> hyperbolas, PreferredZone zone)
	{
		for (Hyperbola hyperbola : hyperbolas)
		{
			if (hyperbola.inRightBrunch(zone.getPoly()))
				return true;
		}
		return false;
	}

	public static class EdgeWrapper<T>
	{
		final T value;

		public EdgeWrapper(T value)
		{
			this.value = value;
		}

		public T value()
		{
			return value();
		}

		@Override
		public boolean equals(Object o)
		{
			return false;
		}

		@Override
		public int hashCode()
		{
			return value.hashCode();
		}

		@Override
		public String toString()
		{
			return value + "";
		}
	}
}
