
public enum Output {
	JSON(0x01), TEXT(0x02);
	
	private int val;
	Output(int value)
	{
		this.val = value;
	}
	
	public int type()
	{
		return val;
	}
}
