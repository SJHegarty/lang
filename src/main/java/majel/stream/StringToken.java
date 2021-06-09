package majel.stream;

import majel.lang.descent.Decomposable;
import majel.lang.util.TokenStream_Obj;
import majel.lang.util.TokenStream_Char;
import majel.util.ObjectUtils;

import java.util.Set;

public record StringToken(String value, Set<String> labels) implements Decomposable<Token$Char>{
	public StringToken{
		value = ObjectUtils.escape(value);
	}

	@Override
	public TokenStream_Obj<Token$Char> decompose(){
		return TokenStream_Char
			.from(ObjectUtils.descape(value))
			.wrap();
	}
}
