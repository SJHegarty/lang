package majel.lang.descent.structure.indent2;

import majel.stream.StringToken;

import java.util.List;

public record Line(StringToken indent, List<StringToken> headTokens) implements FooToken{

	@Override
	public int depth(){
		return indent.value().length() - 1;
	}
}