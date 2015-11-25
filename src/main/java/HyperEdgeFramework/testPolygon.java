package HyperEdgeFramework;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Point;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Polygon;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class testPolygon
{
	public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
	{
//		TreeInflater inflater = new TreeInflater(Grid.squareGrid(3, com.github.davidmoten.rtree.geometry.Point.create(0, 0), 1, 1)).invoke();
//		SimpleWeightedGraph<String, Algorithm.Edge> preCalcGraph = Algorithm.hyperEdgeAlgorithm(inflater.getRTree(), inflater.getNotVisited());
//		Set<Entry<String, com.github.davidmoten.rtree.geometry.Point>> setOfPoints = new HashSet<>();
//		setOfPoints.add(Entry.entry("s", com.github.davidmoten.rtree.geometry.Point.create(-2, -3)));
//		setOfPoints.add(Entry.entry("e", com.github.davidmoten.rtree.geometry.Point.create(12, -12)));
//		Inserter.insert(preCalcGraph, inflater.getNotVisited(), setOfPoints);
//		List<Algorithm.Edge> pathBetween = DijkstraShortestPath.findPathBetween(preCalcGraph, "s", "e");
//		DijkstraShortestPath<String, Algorithm.Edge> stringEdgeWrapperDijkstraShortestPath = new DijkstraShortestPath<>(preCalcGraph, "s", "e");
//		System.out.println(pathBetween);

		GeometryFactory geometryFactory = new GeometryFactory();
		LineSegment segment = new LineSegment(
				new Coordinate(-1, -1),
				new Coordinate(2, 2));
		Polygon polygon1 = geometryFactory.createPolygon(new Coordinate[]{
				new Coordinate(0, 0),
				new Coordinate(1, 0),
				new Coordinate(1, 1),
				new Coordinate(0, 1),
				new Coordinate(0, 0)});
		Polygon polygon2 = geometryFactory.createPolygon(new Coordinate[]{
				new Coordinate(10, 0),
				new Coordinate(11, 0),
				new Coordinate(11, 1),
				new Coordinate(10, 1),
				new Coordinate(10, 0)});

		// TODO: 26.11.15 check performance
		ArrayList<Circle> circles = Grid.linearGrid(10, Point.create(0, 0), 5, 1);
		RTree<Integer, Circle> rTree = RTree.create();
		for (Circle circle : circles)
		{
			rTree = rTree.add(1, circle);
		}
		final int[] counter = {0};
		rTree.nearest(Point.create(0, 0), Double.POSITIVE_INFINITY, circles.size())
				.doOnNext(integerCircleEntry -> {
					System.out.println(integerCircleEntry.geometry());
					if (counter[0] == 5)
						throw new RuntimeException();
					else counter[0]++;
				})
				.doOnError(Throwable::printStackTrace)
				.doOnSubscribe(() -> System.out.println("subscribed"))
				.subscribe();
	}
}
