package majel.lang.descent;

import majel.lang.util.TokenStream_Obj;
import majel.stream.Token;

public interface Decomposable<LastType extends Token> extends Token{
	TokenStream_Obj<LastType> decompose();
}
