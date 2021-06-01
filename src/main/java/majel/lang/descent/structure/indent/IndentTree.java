package majel.lang.descent.structure.indent;

import majel.lang.descent.structure.Line;
import majel.lang.util.TokenStream;
import majel.util.ObjectUtils;

public record IndentTree(
	int lineNumber,
	String content,
	IndentToken[] children
)
implements IndentToken{

	@Override
	public String toString(){
		var builder = new StringBuilder();
		buildString(builder, 0);
		return builder.toString();
	}

	private void buildString(StringBuilder builder, int depth){
		var indentString = new String(ObjectUtils.repeating('\t', depth));
		builder
			.append(indentString)
			.append(getClass().getSimpleName())
			.append('[').append(content).append("]");

		for(var c: children){
			builder.append('\n');
			if(c instanceof IndentTree t){
				t.buildString(builder, depth + 1);
			}
			else{
				builder
					.append(indentString)
					.append('\t')
					.append(c);
			}
		}
	}

	@Override
	public TokenStream<Line> decompose(){
		return TokenStream
			.of(new Line(lineNumber, 0, content, true))
			.concat(
				() -> TokenStream.of(children)
					.unwrap(IndentToken::decompose)
					.map(
						line -> new Line(
							line.lineNumber(),
							line.indent() + 1,
							line.content(),
							line.terminated()
						)
					)
			);
	}

	public int childCount(){
		return children.length;
	}
}
