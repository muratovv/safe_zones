package HyperEdgeFramework.HyperEdgeFlow;

import HyperEdgeFramework.Hyperbola;
import HyperEdgeFramework.PreferredZone;
import HyperEdgeFramework.Util.DoubleUtil;
import HyperEdgeFramework.Util.GeomUtil;
import com.vividsolutions.jts.geom.*;
import javafx.util.Pair;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Inserter
{
	public static void insert(SimpleWeightedGraph<Integer, Algorithm.Edge> graph,
	                          List<PreferredZone> zones, Set<Pair<Integer, Point>> points)
	{
		for (Pair<Integer, Point> point : points)
		{
			insert(graph, zones, point);
		}
	}

	public static void insert(SimpleWeightedGraph<Integer, Algorithm.Edge> graph,
	                          List<PreferredZone> zones, Pair<Integer, Point> pointBundle)
	{
		graph.addVertex(pointBundle.getKey());
		PreferredZone pointZone =
				new PreferredZone(
						(Polygon) createCircle(GeomUtil.factory(),
								pointBundle.getValue().getCoordinate(), DoubleUtil.eps * 3, 4), 0);

		List<Hyperbola> hyperbolas = new ArrayList<>();

		zones.sort((o1, o2) -> {
			double d1 = o1.distance(pointZone);
			double d2 = o2.distance(pointZone);
			if (DoubleUtil.q(d1, d2)) return 0;
			return DoubleUtil.l(d1, d2) ? -1 : 1;
		});
		for (PreferredZone zone : zones)
		{
			double distance = zone.distance(pointBundle.getValue());
			if (!DoubleUtil.q(distance, 0))
			{
				if (!Algorithm.anyCover(hyperbolas, zone))
				{
					Algorithm.Edge edge = graph.addEdge(pointBundle.getKey(), zone.getIndex());
					if (edge != null)
					{
						edge.setEdgeWeight(distance);
						edge.setV1(pointBundle.getKey());
						edge.setV2(zone.getIndex());
						graph.setEdgeWeight(edge, distance);
					}
					hyperbolas.add(Hyperbola.create(pointZone, zone));
				}
			}
		}
	}

	static Geometry createCircle(GeometryFactory gFactory, Coordinate centre, double radius, int polygonSize)
	{
		Geometry centrePt = gFactory.createPoint(centre);
		return centrePt.buffer(radius, polygonSize);
	}
}
