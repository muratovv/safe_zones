package Application.Statistic;

import HyperEdgeFramework.PreferredZone;
import HyperEdgeFramework.Util.GeomUtil;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import java.util.ArrayList;

/**
 * Getting statistic of source dataset
 */
public class SourceDataSetStat
{
	ArrayList<PreferredZone> zones;

	int quantityOfZones;
	double zonesArea = .0;
	double allArea;
	int maxEdges;
	double averagePolygonSize;

	public SourceDataSetStat(ArrayList<PreferredZone> zones)
	{
		this.zones = zones;

		quantityOfZones = zones.size();

		for (PreferredZone zone : zones)
		{
			zonesArea += zone.getPoly().getArea();
		}

		Polygon[] polygons = new Polygon[zones.size()];
		for (int i = 0; i < zones.size(); i++)
		{
			PreferredZone zone = zones.get(i);
			polygons[i] = zone.getPoly();
		}
		MultiPolygon multiPolygon = GeomUtil.factory().createMultiPolygon(polygons);
		allArea = multiPolygon.getEnvelope().getArea();

		maxEdges = zones.size() * (zones.size() - 1) / 2;

		averagePolygonSize = multiPolygon.getCoordinates().length / zones.size();
	}

	public int quantityOfZones()
	{
		return quantityOfZones;
	}

	public double zonesArea()
	{
		return zonesArea;
	}

	public double allArea()
	{
		return allArea;
	}

	public int maxEdges()
	{
		return maxEdges;
	}

	public double averagePolygonSize()
	{
		return averagePolygonSize;
	}
}
