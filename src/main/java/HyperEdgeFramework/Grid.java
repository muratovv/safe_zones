package HyperEdgeFramework;

import HyperEdgeFramework.HyperEdgeFlow.Inserter;
import HyperEdgeFramework.Util.GeomUtil;
import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Point;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Random;

public class Grid
{
	public static ArrayList<Circle> linearGrid(int quantity, Point start, double rad, double between)
	{
		ArrayList<Circle> linear = new ArrayList<>();
		double x = start.x();
		double y = start.y();
		for (int i = 0; i < quantity; i++)
		{
			linear.add(Circle.create(x + i * (2 * rad + between), y, rad));
		}
		return linear;
	}

	public static ArrayList<Circle> squareGrid(int line, Point start, double rad, double between)
	{
		ArrayList<Circle> square = new ArrayList<>();
		for (int i = 0; i < line; i++)
		{
			double x = start.x();
			double y = start.y() - i * (2 * rad + between);
			square.addAll(linearGrid(line, Point.create(x, y), rad, between));
		}
		return square;
	}

	public static class DataSetGenerator
	{
		private static Random random = new Random();

		public static ArrayList<Pair<ArrayList<Polygon>, ArrayList<Pair<Coordinate, Coordinate>>>>
		generateEasyRandomDataSets(int qOfDatasets, int qOfPathFinding, int fromQOfZone, int toQOfZones, int polySplit)
		{

			ArrayList<Pair<ArrayList<Polygon>, ArrayList<Pair<Coordinate, Coordinate>>>> dataSets = new ArrayList<>();

			for (int i = 1; i <= qOfDatasets + 1; i++)
			{
				EasyRandomDataset easyRandomDataset = new EasyRandomDataset(qOfPathFinding, fromQOfZone, toQOfZones,
						polySplit, i, i * 2 + 11);
				dataSets.add(easyRandomDataset.generate());
			}
			return dataSets;
		}

		public static com.vividsolutions.jts.geom.Point generatePoint(MultiPolygon stat, ArrayList<Polygon> polygons)
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

			com.vividsolutions.jts.geom.Point point;
			do
			{
				int xRand = random.nextInt((maxX - minX) + 1) + minX;
				int yRand = random.nextInt((maxY - minY) + 1) + minY;
				point = GeomUtil.factory().createPoint(new Coordinate(xRand, yRand));
			} while (Inserter.inAnyZone(polygons, point));
			return point;
		}

		public static class EasyRandomDataset
		{
			private int qOfPathFinding;
			private int fromQOfZone;
			private int toQOfZones;
			private int polySplit;
			private double radius;
			private double distanse;
			private ArrayList<Polygon> polygons;
			private ArrayList<Pair<Coordinate, Coordinate>> paths;

			public EasyRandomDataset(int qOfPathFinding, int fromQOfZone, int toQOfZones, int polySplit, double radius, double distance)
			{
				this.qOfPathFinding = qOfPathFinding;
				this.fromQOfZone = fromQOfZone;
				this.toQOfZones = toQOfZones;
				this.polySplit = polySplit;
				this.radius = radius;
				this.distanse = distance;
				invoke();
			}

			public ArrayList<Polygon> getPolygons()
			{
				return polygons;
			}

			public ArrayList<Pair<Coordinate, Coordinate>> getPaths()
			{
				return paths;
			}

			private EasyRandomDataset invoke()
			{
				polygons = Inflater.map(squareGrid(toQOfZones, Point.create(0, 0), radius, distanse), polySplit);
				int zonesForRemove = Math.abs(random.nextInt((toQOfZones - fromQOfZone) + 1));

				for (int j = 0; j < zonesForRemove; j++)
				{
					polygons.remove(Math.abs(random.nextInt(polygons.size())));
				}

				MultiPolygon multiPolygon = GeomUtil.factory().createMultiPolygon(polygons.toArray(new Polygon[polygons.size()]));


				paths = new ArrayList<>();
				for (int j = 0; j < qOfPathFinding; j++)
				{
					com.vividsolutions.jts.geom.Point start = generatePoint(multiPolygon, polygons);
					com.vividsolutions.jts.geom.Point end = generatePoint(multiPolygon, polygons);
					paths.add(new Pair<>(start.getCoordinate(), end.getCoordinate()));
				}
				return this;
			}

			public Pair<ArrayList<Polygon>, ArrayList<Pair<Coordinate, Coordinate>>> generate()
			{
				return new Pair<>(getPolygons(), getPaths());
			}
		}
	}
}
