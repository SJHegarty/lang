package majel.lang.descent.structure.indent;

import majel.lang.descent.structure.Line;
import majel.lang.util.TokenStream;

public record IndentHidden(IndentToken wrapped) implements IndentToken{

	@Override
	public TokenStream<Line> regress(){
		return wrapped.regress().map(
			line -> new Line(line.indent()/* + 1*/, line.content(), line.terminated())
		);
	}

	@Override
	public void reconstitute(StringBuilder builder, int depth){
		wrapped.reconstitute(builder, depth + 1);
	}
}
