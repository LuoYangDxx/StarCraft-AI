package computation;

public class StringUtil {
	
	
	public static String putLeft(String s, int len)
	{
		String t = "";
		for(int i = 0; i < len - s.length(); i++)
			t += " ";
		return s + t;
	}
	
	public static String putRight(String s, int len)
	{
		String t = "";
		for(int i = 0; i < len - s.length(); i++)
			t += " ";
		return t + s;
	}
	
}
