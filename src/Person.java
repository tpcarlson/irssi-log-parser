import java.util.LinkedList;
import java.util.ArrayList;

public class Person
{
	LinkedList<String>	knownAs;
	int					lines	= 0;

	public Person(String nick)
	{
		knownAs = new LinkedList<>();
		knownAs.add(nick);
	}

	public String currentNick()
	{
		return knownAs.getLast();
	}

	public void changeNick(String newNick)
	{
		knownAs.add(newNick);
	}

	public void sayLine(String line)
	{
		lines++;
	}

	public int getLines()
	{
		return lines;
	}
	
	static String serializePersonsCollectionToJson(ArrayList<Person> input)
	{
		String output = "[";
		
		for(int i = 0; i < input.size() - 1; i++)
		{
			Person current = input.get(i);
			output += "[\"" + current.currentNick() + "\"," + current.getLines() + "],";
		}
		Person current = input.get(input.size() - 1);
		output += "[\"" + current.currentNick() + "\"," + current.getLines() + "]]";
		
		return output;
	}
}
