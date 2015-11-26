package Application.Statistic;

import HyperEdgeFramework.HyperEdgeFlow.Algorithm;
import org.jgrapht.alg.DijkstraShortestPath;

/**
 * Getting statistic of shortest path
 */
public class ShortestPathStat
{
	DijkstraShortestPath<Integer, Algorithm.Edge> shortestPath;

	int usedZones;
	double distance;

	public ShortestPathStat(DijkstraShortestPath<Integer, Algorithm.Edge> shortestPath, double distance)
	{
		this.shortestPath = shortestPath;
		usedZones = shortestPath.getPathEdgeList().size();

		this.distance = distance;
	}

	public double getDistance()
	{
		return distance;
	}

	public int getUsedZones()
	{
		return usedZones;
	}
}
