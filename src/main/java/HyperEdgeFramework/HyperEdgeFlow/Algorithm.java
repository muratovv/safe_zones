package HyperEdgeFramework.HyperEdgeFlow;

import HyperEdgeFramework.Hyperbola;
import HyperEdgeFramework.PreferredZone;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import org.jgrapht.graph.DefaultWeightedEdge;
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
						java.lang.Double.POSITIVE_INFINITY,
						currentNearest);
				List<Entry<Integer, PreferredZone>> nearestList = nearest.toList().toBlocking().single();

				if (nearestList.size() <= currentNearest - 1)
					break;

				PreferredZone uZone = nearestList.get(currentNearest - 1).geometry();

				if (!anyCover(hyperbolas, uZone))
				{
					graph.addVertex(uZone.getIndex());
					EdgeWrapper edge = graph.addEdge(vZone.getIndex(), uZone.getIndex());
					if (edge != null)
					{
						double distance = vZone.distance(uZone);
						edge.setEdgeWeight(distance);
						edge.setV1(uZone.getIndex());
						edge.setV2(vZone.getIndex());
						graph.setEdgeWeight(edge, distance);
					}
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

	public static class EdgeWrapper extends DefaultWeightedEdge
	{
		private double edgeWeight;
		private int v1, v2;

		public double getEdgeWeight()
		{
			return edgeWeight;
		}

		public void setEdgeWeight(double edgeWeight)
		{
			this.edgeWeight = edgeWeight;
		}

		public int getV1()
		{
			return v1;
		}

		public void setV1(int v1)
		{
			this.v1 = v1;
		}

		public int getV2()
		{
			return v2;
		}

		public void setV2(int v2)
		{
			this.v2 = v2;
		}
	}
}
