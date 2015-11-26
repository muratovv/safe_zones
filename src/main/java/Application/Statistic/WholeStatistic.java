package Application.Statistic;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Generate whole statistic for zone framework
 */
public class WholeStatistic
{
	Pair<ComputedGraphStat, Long> graphStat;
	Pair<SourceDataSetStat, Long> sourceDataSetStat;
	ArrayList<Pair<ShortestPathStat, Long>> pathStat = new ArrayList<>();

	public void setGraphStat(ComputedGraphStat graphStat, long time)
	{
		this.graphStat = new Pair<>(graphStat, time);
	}

	public void setDataSetStat(SourceDataSetStat dataSetStat, long time)
	{
		this.sourceDataSetStat = new Pair<>(dataSetStat, time);
	}

	public void append(ShortestPathStat pathStat, long time)
	{
		this.pathStat.add(new Pair<>(pathStat, time));
	}

	public String generateDataSetStat()
	{
		return sourceDataSetStat.getKey() + ", loadTime=" + generateTimeOutput(sourceDataSetStat.getValue());
	}

	public String generateGraphStat()
	{
		return graphStat.getKey() + ", createTime=" + generateTimeOutput(graphStat.getValue());
	}

	public String generatePathStat(int index)
	{
		Pair<ShortestPathStat, Long> pathStatPair = pathStat.get(index);
		return pathStatPair.getKey().toString() + ", computeTime=" + generateTimeOutput(pathStatPair.getValue());
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

	private String generateTimeOutput(long ns)
	{
		long hours = TimeUnit.NANOSECONDS.toHours(ns);
		long minutes = TimeUnit.NANOSECONDS.toMinutes(ns) - hours * 60;
		long seconds = TimeUnit.NANOSECONDS.toSeconds(ns) - minutes * 60;
		long miliseconds = TimeUnit.NANOSECONDS.toMillis(ns) - seconds * 1000;
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
}
