package HyperEdgeFramework.HyperEdgeFlow;

import HyperEdgeFramework.Hyperbola;
import HyperEdgeFramework.Util.AdapterUtil;
import HyperEdgeFramework.Util.DoubleUtil;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Point;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Inserter
{
	public static void insert(SimpleWeightedGraph<String, Algorithm.EdgeWrapper> graph,
	                          List<Entry<String, Circle>> zones, Set<Entry<String, Point>> points)
	{
		for (Entry<String, Point> point : points)
		{
			insert(graph, zones, point);
		}
	}

	public static void insert(SimpleWeightedGraph<String, Algorithm.EdgeWrapper> graph,
	                          List<Entry<String, Circle>> zones, Entry<String, Point> pointBundle)
	{
		graph.addVertex(pointBundle.value());

		List<Hyperbola> hyperbolas = new ArrayList<>();
		GeometryFactory gFactory = new GeometryFactory();

		Coordinate coordinate = AdapterUtil.point(pointBundle.geometry());
		Geometry circle = createCircle(gFactory, coordinate, DoubleUtil.eps * 2, 5);

		zones.sort((o1, o2) -> {
			com.vividsolutions.jts.geom.Point point = gFactory.createPoint(coordinate);
			Polygon p1 = AdapterUtil.polygon(gFactory, o1.geometry(), 5);
			Polygon p2 = AdapterUtil.polygon(gFactory, o2.geometry(), 5);
			double d1 = p1.distance(point);
			double d2 = p2.distance(point);
			if (DoubleUtil.q(d1, d2))
				return 0;
			return DoubleUtil.l(d1, d2) ? -1 : 1;
		});
		for (Entry<String, Circle> zone : zones)
		{
			Polygon zPoly = AdapterUtil.polygon(gFactory, zone.geometry(), 5);
			double distance = zPoly.distance(gFactory.createPoint(coordinate));
			if (!DoubleUtil.q(distance, 0))
			{
				if (!Algorithm.anyCover(hyperbolas, zPoly))
				{
					graph.addEdge(pointBundle.value(), zone.value(), new Algorithm.EdgeWrapper<>(distance));
					hyperbolas.add(Hyperbola.create(gFactory, circle, zPoly));
				}
			} else
			{
				// TODO now, not supported points, that inside in zones
				throw new IllegalArgumentException(
						String.format("%s inside zone %s", pointBundle.value(), zone.value()));
			}
		}
	}

	static Geometry createCircle(GeometryFactory gFactory, Coordinate centre, double radius, int polygonSize)
	{
		Geometry centrePt = gFactory.createPoint(centre);
		return centrePt.buffer(radius, polygonSize);
	}
}
