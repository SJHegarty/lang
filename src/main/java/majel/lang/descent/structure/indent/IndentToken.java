package majel.lang.descent.structure.indent;

import majel.lang.descent.Decomposable;
import majel.lang.descent.structure.Line;
import majel.lang.util.TokenStream;

public interface IndentToken extends Decomposable<Line>{

	@Override
	TokenStream<Line> decompose();
}
