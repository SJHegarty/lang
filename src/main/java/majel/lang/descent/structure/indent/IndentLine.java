package majel.lang.descent.structure.indent;

import majel.lang.descent.structure.Line;
import majel.lang.util.TokenStream;
import majel.util.ObjectUtils;

public record IndentLine(String content, boolean terminated) implements IndentToken{
	@Override
	public TokenStream<Line> decompose(){
		return TokenStream.of(new Line(0, content, terminated));
	}

}
