import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;

import org.joda.time.Days;
import org.joda.time.LocalDate;

/**
 * @author Simon Cooksey
 * @category irc-log-parser
 * 
 */
public class Parser
{
	private String					logFile;
	// private final boolean doReferences;
	private HashMap<String, Person>	people;
	private TreeMap<Date, Day>		dayData;
	private Date					today;
	private final int				TOP_N_NICKS = 15;

	public Parser(String file, boolean references)
	{
		logFile = file;
		// doReferences = references;
		people = new HashMap<>();
		dayData = new TreeMap<>();
	}

	public void parse() throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader(logFile));
		String currentLine;
		currentLine = in.readLine();
		do
		{
			if(matches(LineRegex.CHAT_LINE, currentLine))
				parseChatLine(currentLine);
			if(matches(LineRegex.ACTION_LINE, currentLine))
				parseActionLine(currentLine);
			if(matches(LineRegex.IRSSI_DAY_CHANGED, currentLine))
				parseDateChange(currentLine);
			if(matches(LineRegex.IRSSI_LOG_CLOSED, currentLine))
				parseLogClosed(currentLine);
			if(matches(LineRegex.IRSSI_LOG_OPENED, currentLine))
				parseLogOpened(currentLine);
			if(matches(LineRegex.NOTICE_NICK_CHANGE, currentLine))
				parseNickChange(currentLine);

			currentLine = in.readLine();

		} while(currentLine != null);

		in.close();

	}

	private boolean matches(LineRegex r, String line)
	{
		return line.matches(r.regex());
	}

	private void parseChatLine(String line)
	{
		String nick = line.split("..:.. <.")[1].split(">")[0];

		Person p = findPersonByKey(nick);
		Day d = getDayByDate(today);

		int hour = 0;

		try
		{
			hour = getHourFromLine(line);
		}
		catch(NumberFormatException | LineFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		d.refTime(hour);
		p.sayLine(line);
	}

	private void parseActionLine(String line)
	{

	}

	private void parseDateChange(String line)
	{
		parseDateLine(line);
	}

	private void parseLogOpened(String line)
	{
		parseDateLine(line);
	}

	private void parseLogClosed(String line)
	{
		parseDateLine(line);
	}

	private void parseDateLine(String line)
	{
		try
		{
			today = getDateFromLine(line);
		}
		catch(ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseNickChange(String line)
	{
		// ..:.. -!- oldnick is now known as newnick

		String oldNick = line.split(" ")[2];
		String newNick = line.split(" ")[7];

		changeNickByKey(oldNick, newNick);
	}

	public int[] getTotalHours()
	{
		int[] hours = new int[24];

		for(Day d : dayData.values())
			for(int i = 0; i < hours.length; i++)
				hours[i] += d.getHour(i);

		return hours;
	}

	public int getTotalLines()
	{
		int i = 0;
		for(Person p : people.values())
			i += p.getLines();

		return i;
	}
	
	public ArrayList<Person> getTopUsers()
	{
		Person persons[] = new Person[TOP_N_NICKS];
		
		for(Person p : people.values())
		{
			for(int i = 0; i < persons.length; i++)
			{
				if(persons[i] == null)
				{
					persons[i] = p;
				}
				if (p.getLines() > persons[i].getLines())
				{
					for(int j = persons.length - 1; j > i; j--)
					{
						persons[j] = persons[j - 1];
					}
					
					persons[i] = p;
					break;
				}
			}
		}
		
		int total = 0;
		ArrayList<Person> output = new ArrayList<Person>();
		
		for(Person p : persons)
		{
			total += p.getLines();
			output.add(p);
		}
		
		EveryoneElse everyoneElse = new EveryoneElse("Everyone Else");
		everyoneElse.setLnes(getTotalLines() - total);
		output.add(everyoneElse);
		
		return output;
	}
	
	public ArrayList<Integer> getLinesByDay()
	{
		ArrayList<Integer> output = new ArrayList<>();
		
		LocalDate yesterday = LocalDate.fromDateFields(dayData.firstKey());
		for(Day d : dayData.values())
		{
			LocalDate today = LocalDate.fromDateFields(d.getDate());
			
			int days = Days.daysBetween(yesterday, today).getDays() - 1;
			for (int i=0; i < days; i++) {
				output.add(null);
			}

			output.add(d.getTotalLines());
			yesterday = LocalDate.fromDateFields(d.getDate());
		}

		return output;
	}

	private Date getDateFromLine(String line) throws ParseException
	{
		DateFormat df;
		if(line.startsWith("--- Log opened ")
				|| line.startsWith("--- Log closed "))
			df = new SimpleDateFormat("EEE MMM dd kk:mm:ss yyyy",
					Locale.ENGLISH);
		else
			df = new SimpleDateFormat("EEE MMM dd yyyy", Locale.ENGLISH);

		Date currentDay = df.parse(line
				.split("--- (Log opened )|(Log closed )|(Day changed )")[1]);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDay);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	private int getHourFromLine(String line) throws LineFormatException,
			NumberFormatException
	{
		if(!matches(LineRegex.LINE_WITH_TIME, line))
			throw new LineFormatException(line,
					LineRegex.LINE_WITH_TIME.regex());

		return Integer.parseInt(line.split(":")[0]);
	}

	private Day getDayByDate(Date key)
	{
		Day d = dayData.get(key);
		if(d == null)
		{
			d = new Day(key);
			dayData.put(key, d);
		}

		return d;
	}

	private Person findPersonByKey(String key)
	{
		Person p = people.get(key);
		if(p == null)
		{
			p = new Person(key);
			people.put(key, p);
		}

		return p;
	}

	/**
	 * Move an item in the people HashMap to a new key, destroying the old one
	 * 
	 * @param key
	 *            current nick of the person
	 * @param newNick
	 *            the new nick of the person
	 */
	private void changeNickByKey(String key, String newNick)
	{
		Person p = findPersonByKey(key); 		// Get current instance
		people.remove(key); 					// Remove instance from HashMap
		p.changeNick(newNick); 					// Change nick in instance
		people.put(newNick, p); 				// Add instance back to HashMap with new key
	}
}
