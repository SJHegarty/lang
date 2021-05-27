package majel.lang.descent;

import majel.stream.Token;

public interface Expression extends Token{
	String reconstitute();
//	T build(RecursiveDescentBuildContext<T> context);
}
