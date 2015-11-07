package HyperEdgeFramework;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.index.strtree.ItemBoundable;
import com.vividsolutions.jts.index.strtree.ItemDistance;


/**
 * Preferred zone is polygon, where distance between contained points reduced by alpha,
 * 0 <= alpha < 1,
 * if alpha == 0, zone called safe zone.
 * <p>
 * Note: in current implementation allowed only convexed polygons.
 */
public class PreferredZone extends Polygon
{
	
	private String index;
	private double alpha;
	
	public PreferredZone(LinearRing shell, LinearRing[] holes, GeometryFactory factory, String index, double alpha)
	{
		super(shell, holes, factory);
		this.index = index;
		if (!checkAlpha(alpha))
			throw new IllegalArgumentException("Bad alpha");
		else this.alpha = alpha;
		if (!isConvexed(this))
			throw new IllegalArgumentException("Polygon is not convexed");
	}

	public PreferredZone(Polygon polygon, String index, double alpha)
	{
		this(((LinearRing) polygon.getExteriorRing()), getLinearRing(polygon), polygon.getFactory(),
				index, alpha);

	}
	
	private static boolean isConvexed(Polygon polygon)
	{
		Coordinate[] coordinates = polygon.getCoordinates();
		int rotation = angleDirection(coordinates[0], coordinates[1], coordinates[2]);
		for (int i = 1; i < coordinates.length - 2; i++)
		{
			int newRotation = angleDirection(coordinates[i], coordinates[i + 1], coordinates[i + 2]);
			if (rotation == 0)
				rotation = newRotation;
			else if (rotation != newRotation)
				return false;
		}
		return true;
	}
	
	private static int angleDirection(Coordinate from, Coordinate mid, Coordinate to)
	{
		double v = Angle.angleBetweenOriented(mid, from, to);
		if (v > 0)
			return 1;
		else if (v == 0)
			return 0;
		else return -1;
	}
	
	private static LinearRing[] getLinearRing(Polygon polygon)
	{
		int len = polygon.getNumInteriorRing();
		LinearRing[] linearRings = new LinearRing[len];
		for (int i = 0; i < len; i++)
		{
			linearRings[i] = ((LinearRing) polygon.getInteriorRingN(i).clone());
		}
		return linearRings;
	}

	private boolean checkAlpha(double alpha)
	{
		return 0 <= alpha && alpha < 1;
	}

	public double internalDistance(ItemDistance distance, Coordinate coordinate1, Coordinate coordinate2)
	{
		GeometryFactory factory = this.getFactory();
		Point point1 = factory.createPoint(coordinate1);
		Point point2 = factory.createPoint(coordinate2);
		if (this.distance(point1) == 0 && this.distance(point2) == 0)
		{
			return distance.distance(new ItemBoundable(null, point1), new ItemBoundable(null, point2)) * alpha;
		} else
			throw new IllegalArgumentException("coordinates not in preferred zone");
	}

	public String getIndex()
	{
		return index;
	}

	public double getAlpha()
	{
		return alpha;
	}
}
