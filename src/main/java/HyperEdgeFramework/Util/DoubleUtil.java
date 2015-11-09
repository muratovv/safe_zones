package HyperEdgeFramework.Util;

public class DoubleUtil
{
	public static double eps = 0.0001;


	/**
	 * @return {@param d1} <= {@param d2}
	 */
	public static boolean le(double d1, double d2)
	{
		return d2 - d1 <= 2 * eps;
	}


	/**
	 * @return {@param d1} >= {@param d2}
	 */
	public static boolean ge(double d1, double d2)
	{
		return q(d1, d2) || !le(d1, d2);
	}

	/**
	 * @return {@param d1} == {@param d2}
	 */
	public static boolean q(double d1, double d2)
	{
		return Math.abs(d1 - d2) <= 2 * eps;
	}

}
