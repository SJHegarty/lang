package majel.lang.descent.lithp;

import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

public interface PrefixLithpExpression extends LithpExpression{
	char headToken();
	LithpExpression wrapped();
	@Override
	default TokenStream<SimpleToken> regress(){
		return SimpleTokenStream.of(headToken())
			.wrap()
			.concat(wrapped()::regress);
	}
}
