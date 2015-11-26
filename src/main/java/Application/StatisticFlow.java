package Application;

import Application.Statistic.ComputedGraphStat;
import Application.Statistic.ShortestPathStat;
import Application.Statistic.SourceDataSetStat;
import Application.Statistic.WholeStatistic;
import HyperEdgeFramework.Grid;
import HyperEdgeFramework.HyperEdgeFlow.Algorithm;
import HyperEdgeFramework.HyperEdgeFlow.ComputedGraphDistance;
import HyperEdgeFramework.HyperEdgeFlow.Inserter;
import HyperEdgeFramework.Inflater;
import HyperEdgeFramework.PreferredZone;
import HyperEdgeFramework.Util.GeomUtil;
import com.github.davidmoten.rtree.geometry.Circle;
import com.vividsolutions.jts.geom.*;
import javafx.util.Pair;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class StatisticFlow
{
	FileWrapper writer;

	/*
		Dataset represents like
		List of pairs, where pair
			Dataset(in list) plus dataset of start and end points
	 */
	ArrayList<Pair<ArrayList<Polygon>, ArrayList<Pair<Coordinate, Coordinate>>>> datasets;
	double alpha;
	GeomUtil.Metric metric;

	public StatisticFlow(File file,
	                     ArrayList<Pair<ArrayList<Polygon>, ArrayList<Pair<Coordinate, Coordinate>>>> datasets,
	                     double alpha,
	                     GeomUtil.Metric metric) throws IOException
	{
		this.writer = new FileWrapper(new FileWriter(file));
		this.datasets = datasets;
		this.alpha = alpha;
		this.metric = metric;
	}

	public static void main(String[] args) throws IOException
	{
		double alpha = 0.1;
		GeomUtil.Metric metric = new GeomUtil.Metric.Euclidean();
		ArrayList<Circle> circles = Grid.squareGrid(20, com.github.davidmoten.rtree.geometry.Point.create(0, 0), 2, 2);
		ArrayList<Polygon> polygons = Inflater.map(circles, 3);
		Pair<Coordinate, Coordinate> startEnd = new Pair<>(new Coordinate(-10, 0), new Coordinate(500, -500));
//
		ArrayList<Pair<ArrayList<Polygon>, ArrayList<Pair<Coordinate, Coordinate>>>> datasets = new ArrayList<>();
		ArrayList<Pair<Coordinate, Coordinate>> points = new ArrayList<>();
		points.add(startEnd);
		datasets.add(new Pair<>(polygons, points));
		datasets = generateDataSet();

		StatisticFlow statisticFlow = new StatisticFlow(new File("test.txt"), datasets, alpha, metric);
		statisticFlow.runAll();
	}

	public static ArrayList<Pair<ArrayList<Polygon>, ArrayList<Pair<Coordinate, Coordinate>>>> generateDataSet()
	{
		int datasetSize = 10;
		int pathSize = 10;

		int polySplit = 10;

		ArrayList<Pair<ArrayList<Polygon>, ArrayList<Pair<Coordinate, Coordinate>>>> dataSets = new ArrayList<>();
		for (int i = 1; i <= datasetSize; i++)
		{
			ArrayList<Polygon> polygons = Inflater.map(Grid.squareGrid(i + 5, com.github.davidmoten.rtree.geometry.Point.create(0, 0), i * 2, i + 3), polySplit);
			MultiPolygon multiPolygon = GeomUtil.factory().createMultiPolygon(polygons.toArray(new Polygon[polygons.size()]));

			ArrayList<Pair<Coordinate, Coordinate>> paths = new ArrayList<>();
			for (int j = 0; j < pathSize; j++)
			{
				Point start = generatePoint(multiPolygon, polygons);
				Point end = generatePoint(multiPolygon, polygons);
				paths.add(new Pair<>(start.getCoordinate(), end.getCoordinate()));
			}
			dataSets.add(new Pair<>(polygons, paths));
		}
		return dataSets;
	}

	private static Point generatePoint(MultiPolygon stat, ArrayList<Polygon> polygons)
	{
		Random random = new Random();
		Geometry envelope = stat.getEnvelope();

		int minX = 0, minY = 0;
		int maxX = 0, maxY = 0;
		for (Coordinate coordinate : envelope.getCoordinates())
		{
			if (minX > coordinate.x)
				minX = ((int) coordinate.x);
			if (minY > coordinate.y)
				minY = ((int) coordinate.y);
			if (maxX < coordinate.x)
				maxX = ((int) coordinate.x);
			if (maxY < coordinate.y)
				maxY = ((int) coordinate.y);
		}

		Point point = null;
		do
		{
			int xRand = random.nextInt((maxX - minX) + 1) + minX;
			int yRand = random.nextInt((maxY - minY) + 1) + minY;
			point = GeomUtil.factory().createPoint(new Coordinate(xRand, yRand));
		} while (Inserter.inAnyZone(polygons, point));
		return point;
	}

	public void runAll() throws IOException
	{
		System.out.print(writer.write(String.format("Metric %s, alpha = %f\n", metric, alpha), 0));
		for (int i = 0; i < datasets.size(); i++)
		{
			runNextDataSet(i);
		}
		writer.close();
	}

	public void runNextDataSet(int next) throws IOException
	{
		System.out.print(String.format("Dataset %d...\n", next + 1));

		WholeStatistic wholeStatistic = new WholeStatistic();

		long start = System.nanoTime();

		Pair<ArrayList<Polygon>, ArrayList<Pair<Coordinate, Coordinate>>> dataset = datasets.get(next);
		ArrayList<PreferredZone> zones = generatePreferredZones(dataset.getKey());
		ArrayList<Pair<Point, Point>> points = generatePoints(dataset.getValue());
		Inflater inflater = new Inflater(zones).invoke();

		SourceDataSetStat dataSetStat = new SourceDataSetStat(zones);

		long end = System.nanoTime();
//		System.out.print(writer.write(dataSetStat.toString(), 0));
//		System.out.print(writer.write(String.format(", load on %d\n", TimeUnit.NANOSECONDS.toSeconds(end - start)), 0));

		wholeStatistic.setDataSetStat(dataSetStat, end - start);

		System.out.println(wholeStatistic.generateDataSetStat());

		start = System.nanoTime();
		SimpleWeightedGraph<Integer, Algorithm.Edge> G = computeGraph(inflater);
		end = System.nanoTime();

		ComputedGraphStat graphStat = new ComputedGraphStat(G);

//		System.out.print(writer.write(graphStat.toString(), 1));
//		System.out.print(writer.write(String.format(", computed on %d\n", TimeUnit.NANOSECONDS.toSeconds(end - start)), 0));

		wholeStatistic.setGraphStat(graphStat, end - start);
		System.out.println(wholeStatistic.generateGraphStat());

		for (Pair<Point, Point> point : points)
		{
			start = System.nanoTime();
			Inserter.insert(G, inflater.getNotVisited(), new Pair<>(-1, point.getKey()));
			Inserter.insert(G, inflater.getNotVisited(), new Pair<>(-2, point.getValue()));
			end = System.nanoTime();

			long insertTime = end - start;

//			System.out.print(writer.write(String.format("points added on %d\n", TimeUnit.NANOSECONDS.toSeconds(end - start)), 2));


			start = System.nanoTime();
			DijkstraShortestPath<Integer, Algorithm.Edge> shortestPath = shortestPath(G);

//			System.out.println("shortest path size " + shortestPath.getPathEdgeList().size());

			double distance = ComputedGraphDistance.compute(shortestPath.getPathEdgeList(), inflater.getNotVisited());
			end = System.nanoTime();

			ShortestPathStat stat = new ShortestPathStat(shortestPath, distance);
//			System.out.print(writer.write(String.format("%s, create on %d\n", stat, TimeUnit.NANOSECONDS.toSeconds(end - start)), 3));

			long shortestPathTime = end - start;

			wholeStatistic.append(stat, insertTime + shortestPathTime);

			G.removeVertex(-1);
			G.removeVertex(-2);
		}
		System.out.println(wholeStatistic.generateShortestPaths());
		writer.write(wholeStatistic.toString(), 0);
	}

	private SimpleWeightedGraph<Integer, Algorithm.Edge> computeGraph(Inflater inflater)
	{
		return Algorithm.hyperEdgeAlgorithm(inflater.getRTree(), inflater.getNotVisited());
	}

	private DijkstraShortestPath<Integer, Algorithm.Edge> shortestPath(SimpleWeightedGraph<Integer, Algorithm.Edge> graph)
	{
		return new DijkstraShortestPath<>(graph, -1, -2);
	}

	private ArrayList<PreferredZone> generatePreferredZones(ArrayList<Polygon> polygons)
	{
		PreferredZone.updateIndex();
		return polygons.stream().map(polygon -> new PreferredZone(((Polygon) GeomUtil.getReducer().reduce(polygon)), alpha))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private ArrayList<Pair<Point, Point>> generatePoints(ArrayList<Pair<Coordinate, Coordinate>> rowCoordinatates)
	{
		return rowCoordinatates.stream().map(coordPair -> new Pair<>(GeomUtil.factory().createPoint(coordPair.getKey()),
				GeomUtil.factory().createPoint(coordPair.getValue()))).collect(Collectors.toCollection(ArrayList<Pair<Point, Point>>::new));
	}


	private static class FileWrapper
	{
		FileWriter writer;

		public FileWrapper(FileWriter writer)
		{
			this.writer = writer;
		}

		public String write(String head, int offset) throws IOException
		{
			String result = new String(new char[offset]).replace('\0', '\t') + head;
			writer.append(result);
			return result;
		}

		public void close() throws IOException
		{
			if (writer != null)
				writer.close();
		}
	}

}
