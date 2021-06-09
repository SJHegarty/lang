package majel.lang.descent.structure.indent;

import majel.lang.descent.structure.Line;
import majel.lang.util.TokenStream$Obj;

public record IndentLine(int lineNumber, String content, boolean terminated) implements IndentToken{
	@Override
	public TokenStream$Obj<Line> decompose(){
		return TokenStream$Obj.of(new Line(lineNumber, 0, content, terminated));
	}

}
