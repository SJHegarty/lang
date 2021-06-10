package majel.lang.descent.structure.indent2;

import majel.stream.StringToken;
import majel.stream.Token;

import java.util.List;

public interface FooToken extends Token{
	int depth();

	List<StringToken> headTokens();
}