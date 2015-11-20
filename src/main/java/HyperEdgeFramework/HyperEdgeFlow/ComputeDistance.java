package HyperEdgeFramework.HyperEdgeFlow;

import HyperEdgeFramework.PreferredZone;
import HyperEdgeFramework.Util.GeomUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import java.util.List;

public class ComputeDistance
{

	/**
	 * @param path  shortest path
	 * @param zones zones in sorted order by index
	 * @return return distance
	 */
	public static double compute(List<Algorithm.Edge> path, List<PreferredZone> zones)
	{
		double distance = 0;
		for (int i = 1; i < path.size() - 2; i++)
		{
			double edgeWeight = path.get(i).getEdgeWeight();
			distance += edgeWeight;
			double insideZone = pathInsideZone(path.get(i), path.get(i + 1), zones);
			distance += insideZone;
		}
		distance += path.get(0).getEdgeWeight();
		distance += path.get(path.size() - 1).getEdgeWeight();
		return distance;
	}

	private static double pathInsideZone(Algorithm.Edge e1, Algorithm.Edge e2, List<PreferredZone> zones)
	{
		swapIfNeed(e1, e2);
		Coordinate[] e1Coord = DistanceOp.nearestPoints(zones.get(e1.getV1()).getPoly(), zones.get(e1.getV2()).getPoly());
		Coordinate[] e2Coord = DistanceOp.nearestPoints(zones.get(e2.getV1()).getPoly(), zones.get(e2.getV2()).getPoly());
		return zones.get(e1.getV2()).distanceInside(
				GeomUtil.factory().createPoint(e1Coord[1]),
				GeomUtil.factory().createPoint(e2Coord[0]));
	}

	private static void swapIfNeed(Algorithm.Edge e1, Algorithm.Edge e2)
	{
		Integer commonVertex = getCommonVertex(e1, e2);
		if (e1.getV1() == commonVertex)
			swap(e1);
		if (e2.getV2() == commonVertex)
			swap(e2);
	}

	private static Integer getCommonVertex(Algorithm.Edge e1, Algorithm.Edge e2)
	{
		if (e1.getV1() == e2.getV1() || e1.getV1() == e2.getV2())
			return e1.getV1();
		else return e1.getV2();
	}

	private static void swap(Algorithm.Edge e1)
	{
		int temp = e1.getV1();
		e1.setV1(e1.getV2());
		e1.setV2(temp);
	}

}
