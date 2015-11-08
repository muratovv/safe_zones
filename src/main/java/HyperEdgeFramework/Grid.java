package HyperEdgeFramework;

import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Point;

import java.util.ArrayList;

public class Grid
{
	public static ArrayList<Circle> linearGrid(int quantity, Point start, double rad, double between)
	{
		ArrayList<Circle> linear = new ArrayList<>();
		double x = start.x();
		double y = start.y();
		for (int i = 0; i < quantity; i++)
		{
			linear.add(Circle.create(x + i * (2 * rad + between),
					y,
					rad));
		}
		return linear;
	}
}
