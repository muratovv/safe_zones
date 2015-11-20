package Application;

import HyperEdgeFramework.Grid;
import HyperEdgeFramework.HyperEdgeFlow.Algorithm;
import HyperEdgeFramework.HyperEdgeFlow.Inserter;
import HyperEdgeFramework.PreferredZone;
import HyperEdgeFramework.TreeInflater;
import HyperEdgeFramework.Util.AdapterUtil;
import HyperEdgeFramework.Util.GeomUtil;
import com.github.davidmoten.rtree.geometry.Circle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
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
	private static double alpha = 0.5;

	public static void main(String[] args)
	{
		flow();
	}

	private static void flow()
	{
		List<PreferredZone> zones = getterOfZones.getZones();
		Set<Pair<Integer, Point>> points = getterOfZones.getPoints();

		TreeInflater inflater = new TreeInflater(zones).invoke();

		SimpleWeightedGraph<Integer, Algorithm.Edge> graph
				= Algorithm.hyperEdgeAlgorithm(inflater.getRTree(), inflater.getNotVisited());

		Inserter.insert(graph, inflater.getNotVisited(), points);
		DijkstraShortestPath<Integer, Algorithm.Edge> shortestPath = new DijkstraShortestPath<>(graph, -1, -2);
		System.out.println(graph);
		System.out.println(shortestPath.getPathEdgeList());

	}

	private static List<PreferredZone> map(List<Circle> lst)
	{
		return lst.stream().map(circle -> new PreferredZone(AdapterUtil.polygon(GeomUtil.factory(), circle, 4), alpha))
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
			Coordinate end = new Coordinate(-2, 12);

			pointsBundle.add(new Pair<>(-1, GeomUtil.factory().createPoint(start)));
			pointsBundle.add(new Pair<>(-2, GeomUtil.factory().createPoint(end)));
			return pointsBundle;
		}

		static List<PreferredZone> gridZones1()
		{
			ArrayList<Circle> circles = Grid.squareGrid(3, com.github.davidmoten.rtree.geometry.Point.create(0, 0), 1, 1);
			return map(circles);
		}
	}
}
