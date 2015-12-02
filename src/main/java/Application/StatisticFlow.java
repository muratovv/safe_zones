package Application;

import Application.Statistic.ComputedGraphStat;
import Application.Statistic.ShortestPathStat;
import Application.Statistic.SourceDataSetStat;
import Application.Statistic.WholeStatistic;
import HyperEdgeFramework.HyperEdgeFlow.Algorithm;
import HyperEdgeFramework.HyperEdgeFlow.ComputedGraphDistance;
import HyperEdgeFramework.HyperEdgeFlow.Inserter;
import HyperEdgeFramework.Inflater;
import HyperEdgeFramework.PreferredZone;
import HyperEdgeFramework.Util.GeomUtil;
import com.carrotsearch.sizeof.RamUsageEstimator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import javafx.util.Pair;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static HyperEdgeFramework.Grid.DataSetGenerator.generateEasyRandomDataSet;

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
		this.writer = new FileWrapper(new FileWriter(file, true));
		this.datasets = datasets;
		this.alpha = alpha;
		this.metric = metric;
	}

	public static void main(String[] args) throws IOException
	{
		double alpha = 0.;
		GeomUtil.Metric metric = new GeomUtil.Metric.Euclidean();
		ArrayList<Pair<ArrayList<Polygon>, ArrayList<Pair<Coordinate, Coordinate>>>> datasets
				= generateEasyRandomDataSet(0, 10, 30, 30, 4);

		StatisticFlow statisticFlow = new StatisticFlow(new File("test.txt"), datasets, alpha, metric);
		statisticFlow.runAll();
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

		long startTime = System.nanoTime();

		Pair<ArrayList<Polygon>, ArrayList<Pair<Coordinate, Coordinate>>> dataset = datasets.get(next);
		ArrayList<PreferredZone> zones = generatePreferredZones(dataset.getKey());
		ArrayList<Pair<Point, Point>> points = generatePoints(dataset.getValue());
		Inflater inflater = new Inflater(zones).invoke();

		SourceDataSetStat dataSetStat = new SourceDataSetStat(zones);

		long endTime = System.nanoTime();

		wholeStatistic.setDataSetStat(dataSetStat, endTime - startTime,
				RamUsageEstimator.sizeOfAll(dataSetStat, inflater, zones, dataset));

		System.out.println(wholeStatistic.generateDataSetStat());

		startTime = System.nanoTime();

		SimpleWeightedGraph<Integer, Algorithm.Edge> G = computeGraph(inflater);

		endTime = System.nanoTime();

		ComputedGraphStat graphStat = new ComputedGraphStat(G);


		wholeStatistic.setGraphStat(graphStat, endTime - startTime, RamUsageEstimator.sizeOfAll(graphStat));
		System.out.println(wholeStatistic.generateGraphStat());

		for (Pair<Point, Point> point : points)
		{
			startTime = System.nanoTime();

			Inserter.insert(G, inflater.getNotVisited(), new Pair<>(-1, point.getKey()));
			Inserter.insert(G, inflater.getNotVisited(), new Pair<>(-2, point.getValue()));

			long insertTime = System.nanoTime() - startTime; // now not consider time for adding points


			startTime = System.nanoTime();
			DijkstraShortestPath<Integer, Algorithm.Edge> shortestPath = shortestPath(G);

			double distance = ComputedGraphDistance.compute(shortestPath.getPathEdgeList(), inflater.getNotVisited());
			endTime = System.nanoTime();

			ShortestPathStat stat = new ShortestPathStat(shortestPath, distance);
			long shortestPathTime = endTime - startTime;

			wholeStatistic.append(stat, insertTime, shortestPathTime, RamUsageEstimator.sizeOfAll(stat));

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

	private ArrayList<Pair<Point, Point>> generatePoints(ArrayList<Pair<Coordinate, Coordinate>> rowCoordinates)
	{
		return rowCoordinates.stream().map(coordPair -> new Pair<>(GeomUtil.factory().createPoint(coordPair.getKey()),
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
