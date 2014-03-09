
public enum LineRegex {
	IRSSI_LOG_OPENED("... Log opened .*"),
	IRSSI_LOG_CLOSED("... Log closed .*"),
	IRSSI_DAY_CHANGED("... Day changed .*"),
	NOTICE_NICK_CHANGE("..:.. -!- .* is now known as .*"),
	CHAT_LINE("..:.. <.*> .*"),
	ACTION_LINE("..:.. \\* .*"),
	LINE_WITH_TIME("..:.. .*"), 
	LINE_WITH_DATE("--- (Log opened)|(Log closed)|(Day changed) .*");
	
	
	private String reg;
	LineRegex(String _reg)
	{
		reg = _reg;
	}
	
	public String regex()
	{
		return reg;
	}
}
