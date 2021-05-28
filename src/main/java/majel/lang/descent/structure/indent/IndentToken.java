package majel.lang.descent.structure.indent;

import majel.lang.descent.Reconstitutable;
import majel.lang.descent.structure.Line;
import majel.lang.util.TokenStream;

public interface IndentToken extends Reconstitutable<Line>{

	//@Override
	default String reconstitute(){
		var builder = new StringBuilder();
		reconstitute(builder, 0);
		return builder.toString();
	}

	@Override
	TokenStream<Line> regress();

	void reconstitute(StringBuilder builder, int depth);
}
