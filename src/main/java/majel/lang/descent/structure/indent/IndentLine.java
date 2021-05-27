package majel.lang.descent.structure.indent;

import majel.util.ObjectUtils;

public record IndentLine(String content, boolean terminated) implements IndentToken{
	@Override
	public void reconstitute(StringBuilder builder, int depth){
		builder
			.append(new String(ObjectUtils.repeating('\t', depth)))
			.append(content);

		if(terminated){
			builder.append('\n');
		}
	}
}
