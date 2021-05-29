package majel.lang.descent;

import majel.lang.util.TokenStream;
import majel.stream.Token;

public interface Decomposable<LastType extends Token> extends Token{
	TokenStream<LastType> decompose();
}
