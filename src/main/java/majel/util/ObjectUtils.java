package majel.util;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ObjectUtils{

	public static char[] repeating(char c, int count){
		char[] rv = new char[count];
		Arrays.fill(rv, c);
		return rv;
	}

	public static <T> T[] repeating(T t, int count){
		T[] rv = (T[])Array.newInstance(t.getClass(), count);
		Arrays.fill(rv, t);
		return rv;
	}

	public static char lastChar(String content){
		return content.charAt(content.length() - 1);
	}
}
