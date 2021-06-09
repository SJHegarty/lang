package majel.lang.descent;

import majel.lang.util.TokenStream$Obj;
import majel.stream.Token;

public interface Decomposable<LastType extends Token> extends Token{
	TokenStream$Obj<LastType> decompose();
}
