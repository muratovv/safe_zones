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
import java.util.Objects;

public class Algorithm
{
	public static SimpleWeightedGraph<Integer, Edge> hyperEdgeAlgorithm(RTree<Integer, PreferredZone> rtree, List<PreferredZone> zones)
	{

		final int nearest_k = 10;

		SimpleWeightedGraph<Integer, Edge> graph = new SimpleWeightedGraph<>(Edge.class);
		while (zones.size() > 1)
		{
			PreferredZone vZone = zones.remove(0);
			graph.addVertex(vZone.getIndex());

			ArrayList<Hyperbola> hyperbolas = new ArrayList<>();

			int currentNearest = 2;
			List<Entry<Integer, PreferredZone>> nearestListOld = new ArrayList<>();
			nearestListOld.add(new Entry<>(vZone.getIndex(), vZone));
			while (true)
			{
				Observable<Entry<Integer, PreferredZone>> nearest = rtree.nearest(vZone.mbr(),
						java.lang.Double.POSITIVE_INFINITY,
						currentNearest);
				List<Entry<Integer, PreferredZone>> nearestListNew = nearest.toList().toBlocking().single();

				if (nearestListNew.size() <= currentNearest - 1)
					break;

				PreferredZone uZone = nextNearestNeighbor(nearestListOld, nearestListNew).geometry();
//				System.out.println(nearestListOld + " " + nearestListNew);
				System.out.println(uZone);
				nearestListOld = nearestListNew;

				if (!anyCover(hyperbolas, uZone))
				{
					graph.addVertex(uZone.getIndex());
					Edge edge = graph.addEdge(vZone.getIndex(), uZone.getIndex());
					if (edge != null)
					{
						double distance = vZone.distance(uZone);
						edge.setEdgeWeight(distance);
						edge.setV1(uZone.getIndex());
						edge.setV2(vZone.getIndex());
						graph.setEdgeWeight(edge, distance);
						hyperbolas.add(Hyperbola.create(vZone, uZone));
						System.out.println(String.format("hyp %d %d", vZone.getIndex(), uZone.getIndex()));
//						System.out.println(nearestListNew);
					}
					currentNearest++;
				} else
				{
					break;
				}
			}
		}
		return graph;
	}


	private static Entry<Integer, PreferredZone> nextNearestNeighbor(List<Entry<Integer, PreferredZone>> oldList, List<Entry<Integer, PreferredZone>> newList)
	{
		for (int i = oldList.size() - 1; i >= 0; i--)
		{
			if (!Objects.equals(oldList.get(i).value(), newList.get(i + 1).value()))
				return newList.get(i + 1);
		}
		return null;
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

	public static class Edge extends DefaultWeightedEdge
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
