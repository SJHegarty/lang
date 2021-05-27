package majel.lang.descent.structure;

import majel.lang.Parser;
import majel.lang.descent.structure.indent.*;
import majel.lang.util.Mark;
import majel.lang.util.TokenStream;

import java.util.ArrayList;
import java.util.List;

public class IndentParser implements Parser<Line, IndentToken>{

	@Override
	public TokenStream<IndentToken> parse(TokenStream<Line> tokens){
		return new TokenStream<>(){
			@Override
			public IndentToken peek(){
				var mark = mark();
				var rv = poll();
				mark.reset();
				return rv;
			}

			@Override
			public IndentToken poll(){
				final Line head = tokens.poll();
				final List<IndentToken> children = new ArrayList<>();
				final int depth = head.indent();

				for(;;){
					final var next = tokens.peek();
					final int indent = next.indent();

					if(indent <= depth){
						break;
					}
					var child = poll();
					final int hiddenDepth = indent - (depth + 1);
					for(int i = 0; i < hiddenDepth; i++){
						child = new IndentHidden(child);
					}
					children.add(child);

				}
				if(children.isEmpty()){
					return new IndentLine(head.content(), head.terminated());
				}
				return new IndentTree(head.content(), children.toArray(IndentToken[]::new));
			};

			@Override
			public boolean empty(){
				return tokens.empty();
			}

			@Override
			public Mark mark(){
				return tokens.mark();
			}
		};
	}
}
