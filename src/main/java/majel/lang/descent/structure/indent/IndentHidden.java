package majel.lang.descent.structure.indent;

import majel.lang.descent.structure.Line;
import majel.lang.util.TokenStream;

public record IndentHidden(IndentToken wrapped) implements IndentToken{

	@Override
	public TokenStream<Line> decompose(){
		return wrapped.decompose().map(
			line -> new Line(line.lineNumber(), line.indent() + 1, line.content(), line.terminated())
		);
	}

	public int lineNumber(){
		return wrapped.lineNumber();
	}
}
