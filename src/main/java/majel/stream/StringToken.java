package majel.stream;

import majel.lang.descent.Decomposable;
import majel.lang.util.TokenStream$Obj;
import majel.lang.util.TokenStream$Char;
import majel.util.ObjectUtils;

import java.util.Set;

public record StringToken(String value, Set<String> labels) implements Decomposable<Token$Char>{
	public StringToken{
		value = ObjectUtils.escape(value);
	}

	public int length(){
		return value.length();
	}

	@Override
	public TokenStream$Obj<Token$Char> decompose(){
		return TokenStream$Char
			.from(ObjectUtils.descape(value))
			.wrap();
	}
}
