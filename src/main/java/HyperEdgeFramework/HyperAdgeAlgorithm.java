package HyperEdgeFramework;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.jgrapht.graph.SimpleWeightedGraph;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

public class HyperAdgeAlgorithm
{
	public static SimpleWeightedGraph<String, EdgeWrapper> algorithm1Circle(RTree<String, Circle> rtree)
	{
		SimpleWeightedGraph<String, EdgeWrapper> graph = new SimpleWeightedGraph<>(EdgeWrapper.class);
		while (rtree.size() > 1)
		{
			Entry<String, Circle> vZone = arbitraryNode(rtree);

			Polygon vZonePoly = AdapterUtil.polygon(new GeometryFactory(), vZone.geometry(), 5);
			graph.addVertex(vZone.value());
			rtree = rtree.delete(vZone);

			ArrayList<Hyperbola> hyperbolas = new ArrayList<>();

			int currentNearest = 1;
			while (true)
			{
				if (rtree.size() == 0)
					break;
				Observable<Entry<String, Circle>> nearest = rtree.nearest(vZone.geometry().mbr(),
						Double.POSITIVE_INFINITY,
						currentNearest);
				List<Entry<String, Circle>> nearestList = nearest.toList().toBlocking().single();

				if (nearestList.size() <= currentNearest - 1)
					break;

				Entry<String, Circle> uZone = nearestList.get(currentNearest - 1);
				Polygon uZonePoly = AdapterUtil.polygon(new GeometryFactory(), uZone.geometry(), 5);
				if (!anyCover(hyperbolas, uZonePoly))
				{
					graph.addVertex(uZone.value());
					graph.addEdge(vZone.value(), uZone.value(), new EdgeWrapper<>(vZonePoly.distance(uZonePoly)));
					hyperbolas.add(Hyperbola.create(new GeometryFactory(), vZonePoly, uZonePoly));

					currentNearest++;
				} else break;
			}
		}
		return graph;
	}

	private static <V, K extends Geometry> Entry<V, K> arbitraryNode(RTree<V, K> tree)
	{
		List<Entry<V, K>> last = tree.nearest(Point.create(0, 0), Double.POSITIVE_INFINITY, 1).toList().toBlocking().single();
		return last.get(0);
	}

	private static boolean anyCover(List<Hyperbola> hyperbolas, Polygon polygon)
	{
		for (Hyperbola hyperbola : hyperbolas)
		{
			if (hyperbola.inRightBrunch(polygon))
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
