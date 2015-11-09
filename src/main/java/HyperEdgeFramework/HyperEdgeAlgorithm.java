package HyperEdgeFramework;

import HyperEdgeFramework.Util.AdapterUtil;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.jgrapht.graph.SimpleWeightedGraph;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

public class HyperEdgeAlgorithm
{
	public static SimpleWeightedGraph<String, EdgeWrapper> algorithm1Circle(RTree<String, Circle> rtree, List<Entry<String, Geometry>> notVisited)
	{
		SimpleWeightedGraph<String, EdgeWrapper> graph = new SimpleWeightedGraph<>(EdgeWrapper.class);
		while (notVisited.size() > 1)
		{
			Entry<String, Geometry> vZone = arbitraryNode(notVisited);
			Polygon vZonePoly = AdapterUtil.polygon(new GeometryFactory(), (Circle) vZone.geometry(), 5);
			graph.addVertex(vZone.value());

			ArrayList<Hyperbola> hyperbolas = new ArrayList<>();
			int currentNearest = 2;

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
				} else
				{
					break;
				}
			}
		}
		return graph;
	}

	private static <V, K extends Geometry> Entry<String, Geometry> arbitraryNode(List<Entry<String, Geometry>> notVisited)
	{
		Entry<String, Geometry> visited = notVisited.remove(0);
		return visited;
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
