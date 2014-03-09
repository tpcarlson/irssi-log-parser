import java.io.IOException;
import java.util.List;

public class Main
{
	static Output	OUTPUT;
	static String	logFile;
	static String	outFile;

	public static void main(String[] argv)
	{
		if (argv.length < 1)
		{
			System.out.println("Usage: ");
			System.out.println("irssi-log-parser "
					+ "<irssi log file> [<options> <output>]");
			return;
		}

		logFile = argv[0];

		Parser p = new Parser(logFile, false);
		long startTime = System.currentTimeMillis();
		try
		{
			p.parse();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long timeTaken = System.currentTimeMillis() - startTime;

		if (argv.length > 2 && argv[1] == "--json")
		{
			OUTPUT = Output.JSON;
			outFile = argv[2];
		} else
			OUTPUT = Output.JSON;

		switch (OUTPUT)
		{
			case JSON:
				// JSON output
				System.out.println("{");
				System.out.println("\"parse-time\" : " + timeTaken + ",");
				System.out.println("\"parse-date\" : " + startTime + ",");
				System.out.println("\"total-lines\" : " + p.getTotalLines() + ",");
				System.out.println("\"hours\" : " + serializeArrayToJSON(p.getTotalHours()) + ",");
				System.out.println("\"dates\" : " + serializeArrayToJSON(p.getLinesByDay()) + ",");
				System.out.println("\"top\" : " + Person.serializePersonsCollectionToJson(p.getTopUsers()));
				System.out.println("}");
				break;
			case TEXT:
				// Text Output
				System.out.println("Time Taken: " + timeTaken + "ms");
				break;
			default:
				// Default case
				break;
		}
	}
	
	static String serializeArrayToJSON(Object[] array)
	{
		String s = "[";
		for(int i = 0; i < array.length - 1; i++)
			s += array[i].toString() + ",";
		s += array[array.length - 1] + "]";
		
		return s;
	}
	
	static String serializeArrayToJSON(@SuppressWarnings("rawtypes") List array)
	{
		String s = "[";
		for(int i = 0; i < array.size() - 1; i++)
		{
			Object o = array.get(i);
			if(o == null)
				s += "null,";
			else
				s += o.toString() + ",";
		}
		s += array.get(array.size() - 1) + "]";
		
		return s;
	}
	
	static String serializeArrayToJSON(int[] array)
	{
		String s = "[";
		for(int i = 0; i < array.length - 1; i++)
			s += array[i] + ",";
		s += array[array.length - 1] + "]";
		
		return s;
	}

}
