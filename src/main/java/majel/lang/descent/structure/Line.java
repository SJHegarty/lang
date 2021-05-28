package majel.lang.descent.structure;

import majel.lang.descent.Reconstitutable;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;
import majel.util.ObjectUtils;
import majel.util.functional.TokenStreamBuilder;

public record Line(int indent, String content, boolean terminated) implements Reconstitutable<SimpleToken>{

	public boolean empty(){
		return content.length() == 0;
	}

	//@Override
	public String reconstitute(){
		return SimpleTokenStream.of(regress()).remaining();
	}

	@Override
	public TokenStream<SimpleToken> regress(){
		var builder = new TokenStreamBuilder();

		builder
			.feed(ObjectUtils.repeating('\t', indent))
			.feed(content);

		if(terminated){
			builder.feed('\n');
		}

		return builder.immutableView().wrap();
	}
}
