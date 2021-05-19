package majel.util;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ObjectUtils{
	public static <T> T[] repeating(T t, int count){
		T[] rv = (T[])Array.newInstance(t.getClass(), count);
		Arrays.fill(rv, t);
		return rv;
	}
}
