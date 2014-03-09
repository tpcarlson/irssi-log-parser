import java.util.Date;

public class Day
{
	private int[]	hours	= new int[24];
	private Date	date;

	public Day(Date _date)
	{
		date = _date;
	}

	public void refTime(int hour)
	{
		hours[hour]++;
	}

	public int[] getHours()
	{
		return hours;
	}

	public int getHour(int h)
	{
		return hours[h];
	}

	public Date getDate()
	{
		return date;
	}

	public int getTotalLines()
	{
		int i = 0;
		for(int h : hours)
			i += h;

		return i;
	}
}
