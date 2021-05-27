package majel.lang.descent.structure.indent;

import majel.lang.descent.Expression;
import majel.stream.Token;

public interface IndentToken extends Expression{

	@Override
	default String reconstitute(){
		var builder = new StringBuilder();
		reconstitute(builder, 0);
		return builder.toString();
	}

	void reconstitute(StringBuilder builder, int depth);
}
