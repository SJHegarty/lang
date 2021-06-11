package majel.util;

import majel.lang.err.IllegalToken;
import majel.lang.util.TokenStream_Char;

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

	public static String descape(String s){
		TokenStream_Char stream = TokenStream_Char.from(s);
		var builder = new StringBuilder();
		while(!stream.empty()){
			char c = stream.poll();
			if(c != '\\'){
				builder.append(c);
			}
			else{
				var mark = stream.mark();
				char e = stream.poll();
				builder.append(
					switch(e){
						case 'n' -> '\n';
						case 't' -> '\t';
						case 'r' -> '\r';
						case 's' -> ' ';
						default -> {
							mark.reset();
							throw new IllegalToken(stream.wrap());
						}
					}
				);
			}
		}
		return builder.toString();
	}

	public static String escape(String s){
		var builder = new StringBuilder();
		for(char c: s.toCharArray()){
			builder.append(
				switch(c){
					case '\n' -> "\\n";
					case '\t' -> "\\t";
					case '\r' -> "\\r";
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
