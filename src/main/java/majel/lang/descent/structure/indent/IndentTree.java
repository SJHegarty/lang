package majel.lang.descent.structure.indent;

import majel.lang.descent.structure.Line;
import majel.lang.util.TokenStream;
import majel.util.ObjectUtils;

public record IndentTree(
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
	public TokenStream<Line> regress(){
		return TokenStream
			.of(new Line(0, content, true))
			.concat(
				() -> TokenStream.of(children)
					.unwrap(IndentToken::regress)
					.map(
						line -> new Line(
							line.indent() + 1,
							line.content(),
							line.terminated()
						)
					)
			);
	}

	@Override
	public void reconstitute(StringBuilder builder, int depth){
		builder
			.append(new String(ObjectUtils.repeating('\t', depth)))
			.append(content)
			.append('\n');

		for(var c: children){
			c.reconstitute(builder, depth + 1);
		}
	}
}
