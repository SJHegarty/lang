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

	public static String escape(String s){
		var builder = new StringBuilder();
		for(char c: s.toCharArray()){
			builder.append(
				switch(c){
					case '\n' -> "\\n";
					case '\t' -> "\\t";
					case '\r' -> "\\r";
					case ' ' -> "\\s";
					default -> Character.toString(c);
				}
			);
		}
		return builder.toString();
	}

	public static char lastChar(String content){
		return content.charAt(content.length() - 1);
	}
}
