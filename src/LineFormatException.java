public class LineFormatException extends Exception
{

	/**
	 * Serial Version UID
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * The line being parsed
	 */
	private final String		line;

	/**
	 * regex the expected format of that line
	 */
	private final String		regex;

	/**
	 * Line format exception
	 * 
	 * @param line
	 *            the line being parsed
	 * @param regex
	 *            the expected format of that line
	 */
	public LineFormatException(String line, String regex)
	{
		this.line = line;
		this.regex = regex;
	}

	/**
	 * Message
	 */
	public String getMessage()
	{
		return line + " did not match expected format: " + regex;
	}

}
