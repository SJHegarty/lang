package majel.lang.descent.lithp;

import majel.lang.util.TokenStream$Char;
import majel.lang.util.TokenStream;
import majel.stream.Token$Char;

public interface PrefixLithpExpression extends LithpExpression{
	char headToken();
	LithpExpression wrapped();
	@Override
	default TokenStream<Token$Char> decompose(){
		return TokenStream$Char.of(headToken())
			.wrap()
			.concat(wrapped()::decompose);
	}
}
