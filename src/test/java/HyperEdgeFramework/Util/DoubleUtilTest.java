package HyperEdgeFramework.Util;

import org.junit.Assert;
import org.junit.Test;

public class DoubleUtilTest
{
	
	@Test
	public void testLe() throws Exception
	{
		double x1 = 2.0;
		double x2 = 2.0 + DoubleUtil.eps / 2;
		Assert.assertTrue(DoubleUtil.le(x1, x2));
		Assert.assertTrue(DoubleUtil.le(x2, x1));
		Assert.assertTrue(DoubleUtil.le(x2 + DoubleUtil.eps / 2, x1));
	}

	@Test
	public void testGe() throws Exception
	{
		double x1 = 2.0;
		double x2 = 2.0 + DoubleUtil.eps / 2;
		Assert.assertTrue(DoubleUtil.ge(x1, x2));
		Assert.assertTrue(DoubleUtil.ge(x2, x1));
		Assert.assertTrue(DoubleUtil.ge(x2 + DoubleUtil.eps / 2, x1));
	}

	@Test
	public void testQ() throws Exception
	{
		double x1 = 2.0;
		double x2 = 2.0 + DoubleUtil.eps / 2;
		Assert.assertTrue(DoubleUtil.q(x1, x2));
		Assert.assertTrue(DoubleUtil.q(x2, x1));
		Assert.assertTrue(DoubleUtil.q(x2 + DoubleUtil.eps / 2, x1));
	}
}