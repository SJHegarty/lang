package majel.lang.descent.lithp;

import majel.lang.util.TokenStream_Char;
import majel.lang.util.TokenStream_Obj;
import majel.stream.Token$Char;

public interface PrefixLithpExpression extends LithpExpression{
	char headToken();
	LithpExpression wrapped();
	@Override
	default TokenStream_Obj<Token$Char> decompose(){
		return TokenStream_Char.of(headToken())
			.wrap()
			.andThen(wrapped()::decompose);
	}
}
