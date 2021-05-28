package majel.lang.descent;

import majel.lang.util.TokenStream;
import majel.stream.Token;

public interface Reconstitutable<LastType extends Token> extends Token{
	//String reconstitute();
//	T build(RecursiveDescentBuildContext<T> context);
	TokenStream<LastType> regress();
}
