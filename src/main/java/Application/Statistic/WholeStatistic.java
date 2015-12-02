package Application.Statistic;

import com.carrotsearch.sizeof.RamUsageEstimator;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Generate whole statistic for zone framework
 */
public class WholeStatistic
{
	Pair<ComputedGraphStat, DynamicStat> graphStat;
	Pair<SourceDataSetStat, DynamicStat> sourceDataSetStat;
	ArrayList<Pair<ShortestPathStat, DynamicStat>> pathStat = new ArrayList<>();

	public static String time(Long ns)
	{
		long hours = TimeUnit.NANOSECONDS.toHours(ns);
		long minutes = TimeUnit.NANOSECONDS.toMinutes(ns) - hours * 60;
		long seconds = TimeUnit.NANOSECONDS.toSeconds(ns) - minutes * 60 - hours * 60;
		long miliseconds = TimeUnit.NANOSECONDS.toMillis(ns) - seconds * 1000 - minutes * 1000 * 60 - hours * 1000 * 60 * 60;
		StringBuilder stringBuilder = new StringBuilder();
		if (hours > 0)
			stringBuilder.append(hours).append("h ");
		if (minutes > 0)
			stringBuilder.append(minutes).append("m ");
		if (seconds > 0)
			stringBuilder.append(seconds).append("s ");
		if (miliseconds > 0)
			stringBuilder.append(miliseconds).append("ms");
		return stringBuilder.toString();
	}

	public static String memory(Long bytes)
	{
		return RamUsageEstimator.humanReadableUnits(bytes);
	}

	public void setGraphStat(ComputedGraphStat graphStat, long time, long memory)
	{

		DynamicStat dynamicStat = new DynamicStat();
		dynamicStat.setParam("time", time);
		dynamicStat.setParam("memory", memory);
		this.graphStat = new Pair<>(graphStat, dynamicStat);
	}

	public void setDataSetStat(SourceDataSetStat dataSetStat, long time, long memory)
	{
		DynamicStat dynamicStat = new DynamicStat();
		dynamicStat.setParam("time", time);
		dynamicStat.setParam("memory", memory);
		this.sourceDataSetStat = new Pair<>(dataSetStat, dynamicStat);
	}

	public void append(ShortestPathStat pathStat, long insertTime, long shortestPathTime, long memory)
	{
		DynamicStat dynamicStat = new DynamicStat();
		dynamicStat.setParam("shortestPathTime", shortestPathTime);
		dynamicStat.setParam("insertTime", insertTime);
		dynamicStat.setParam("memory", memory);
		this.pathStat.add(new Pair<>(pathStat, dynamicStat));
	}

	public String generateDataSetStat()
	{
		DynamicStat dynamic = sourceDataSetStat.getValue();
		return sourceDataSetStat.getKey() + ", loadTime=" + time(dynamic.getParam("time"))
				+ (dynamic.getParam("memory") != 0 ? ", memory=" + memory(dynamic.getParam("memory")) : "");
	}

	public String generateGraphStat()
	{
		DynamicStat dynamic = graphStat.getValue();
		return graphStat.getKey() + ", createTime=" + time(dynamic.getParam("time"))
				+ (dynamic.getParam("memory") != 0 ? ", memory=" + memory(dynamic.getParam("memory")) : "");
	}

	public String generatePathStat(int index)
	{
		Pair<ShortestPathStat, DynamicStat> pathStat = this.pathStat.get(index);
		return pathStat.getKey().toString()
				+ ", insert=" + time(pathStat.getValue().getParam("insertTime"))
				+ ", compute=" + time(pathStat.getValue().getParam("shortestPathTime"))
				+ ", memory=" + memory(pathStat.getValue().getParam("memory"));
	}

	public String generateShortestPaths()
	{
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < pathStat.size(); i++)
		{
			stringBuilder.append("\t\t").append(generatePathStat(i))
					.append('\n');
		}
		return stringBuilder.toString();
	}

	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder
				.append(generateDataSetStat()).append('\n')
				.append('\t').append(generateGraphStat()).append('\n')
				.append(generateShortestPaths());
		return stringBuilder.toString();
	}


	public static class DynamicStat
	{
		HashMap<String, Long> params = new HashMap<>();

		public DynamicStat()
		{

		}

		public void setParam(String param, Long value)
		{
			params.put(param, value);
		}

		public Long getParam(String param)
		{
			return params.get(param);
		}

	}
}
