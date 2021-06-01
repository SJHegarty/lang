package majel.lang.descent.structure;

import majel.lang.descent.Decomposable;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;
import majel.util.ObjectUtils;
import majel.util.functional.TokenStreamBuilder;

public record Line(int lineNumber, int indent, String content, boolean terminated) implements Decomposable<SimpleToken>{

	public boolean empty(){
		return content.length() == 0;
	}

	@Override
	public TokenStream<SimpleToken> decompose(){
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
