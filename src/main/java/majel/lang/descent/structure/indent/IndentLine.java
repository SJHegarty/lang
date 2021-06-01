package majel.lang.descent.structure.indent;

import majel.lang.descent.structure.Line;
import majel.lang.util.TokenStream;
import majel.util.ObjectUtils;

public record IndentLine(int lineNumber, String content, boolean terminated) implements IndentToken{
	@Override
	public TokenStream<Line> decompose(){
		return TokenStream.of(new Line(lineNumber, 0, content, terminated));
	}

}
