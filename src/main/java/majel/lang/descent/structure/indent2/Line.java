package majel.lang.descent.structure.indent2;

import majel.stream.StringToken;

import java.util.List;

public record Line(StringToken token, List<StringToken> elements) implements FooToken{

	@Override
	public int depth(){
		return token.value().length() - 1;
	}
}