package Application;

import HyperEdgeFramework.Grid;
import HyperEdgeFramework.HyperEdgeFlow.Algorithm;
import HyperEdgeFramework.HyperEdgeFlow.ComputedGraphDistance;
import HyperEdgeFramework.HyperEdgeFlow.Inserter;
import HyperEdgeFramework.Inflater;
import HyperEdgeFramework.PreferredZone;
import HyperEdgeFramework.Util.AdapterUtil;
import HyperEdgeFramework.Util.GeomUtil;
import com.github.davidmoten.rtree.geometry.Circle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import javafx.util.Pair;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class app
{
	private static double alpha = 0;

	public static void main(String[] args)
	{
		flow();
	}

	private static void flow()
	{
		//Set global parameters
		GeomUtil.setMetric(new GeomUtil.Metric.Eucludean());
		alpha = 0.5;

		//Construct dataset
		List<PreferredZone> zones = getterOfZones.getZones();
		Inflater inflater = new Inflater(zones).invoke();

		//compute graph G
		SimpleWeightedGraph<Integer, Algorithm.Edge> graph
				= Algorithm.hyperEdgeAlgorithm(inflater.getRTree(), inflater.getNotVisited());

		//construct start and end points
		Set<Pair<Integer, Point>> points = getterOfZones.getPoints();
		Inserter.insert(graph, inflater.getNotVisited(), points);
		DijkstraShortestPath<Integer, Algorithm.Edge> shortestPath = new DijkstraShortestPath<>(graph, -1, -2);

		System.out.println(graph);
		List<Algorithm.Edge> pathEdgeList = shortestPath.getPathEdgeList();
		System.out.println(pathEdgeList);
		System.out.println(ComputedGraphDistance.compute(pathEdgeList, inflater.getNotVisited()));


	}

	private static List<PreferredZone> map(List<Circle> lst)
	{
		return lst.stream().map(circle -> new PreferredZone(((Polygon) GeomUtil.getReducer().reduce(AdapterUtil.polygon(GeomUtil.factory(), circle, 2))), alpha))
				.collect(Collectors.toCollection(ArrayList::new));
	}


	static class getterOfZones
	{
		static List<PreferredZone> getZones()
		{
			return gridZones1();
		}

		static Set<Pair<Integer, Point>> getPoints()
		{
			Set<Pair<Integer, Point>> pointsBundle = new HashSet<>();

			Coordinate start = new Coordinate(-2, -3);
			Coordinate end = new Coordinate(-2, 17);

			pointsBundle.add(new Pair<>(-1, GeomUtil.factory().createPoint(start)));
			pointsBundle.add(new Pair<>(-2, GeomUtil.factory().createPoint(end)));
			return pointsBundle;
		}

		static List<PreferredZone> gridZones1()
		{
			ArrayList<Circle> circles = Grid.squareGrid(100, com.github.davidmoten.rtree.geometry.Point.create(0, 0), 5, 5);
			return map(circles);
		}
	}
}
