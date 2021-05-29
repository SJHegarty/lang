package majel.lang.descent.structure;

import majel.lang.Parser;
import majel.lang.descent.structure.indent.IndentToken;
import majel.lang.util.Mark;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

import java.io.IOException;
import java.util.ArrayList;

public class LinesParser implements Parser<SimpleToken, Line>{
	public static void main(String...args) throws IOException{
		var resource = Thread.currentThread().getContextClassLoader().getResource(".bspl/Test.bspl");
		final String content = SimpleTokenStream.of(resource.openStream()).drain();
		var sink = new ArrayList<TokenStream.IndexedToken<Line>>();
		var returns = new ArrayList<TokenStream.IndexedToken<SimpleToken>>();

		String reconstructed = SimpleTokenStream.of(
			new IndentParser().parse(
				new LinesParser().parse(
					SimpleTokenStream.from(content).wrap()
						.retain(t -> t.character() != '\r', returns::add)
				)
				.retain(l -> !l.empty(), sink::add)
			)
			.unwrap(IndentToken::decompose)
			.incorporate(TokenStream.from(sink))
			.unwrap(Line::decompose)
			.incorporate(TokenStream.from(returns))
		)
		.remaining();

		System.err.println(content.equals(reconstructed));

	}

	@Override
	public TokenStream<Line> parse(TokenStream<SimpleToken> tokens){
		return new TokenStream<>(){
			@Override
			public Line peek(){
				var mark = mark();
				var rv = poll();
				mark.reset();
				return rv;
			}

			@Override
			public Line poll(){
				final var simple = SimpleTokenStream.of(tokens);
				final int indent;
				{
					int count = 0;
					while(!simple.empty() && simple.peek() == '\t'){
						simple.poll();
						count++;
					}
					indent = count;
				}
				final var content = new StringBuilder();
				while(!simple.empty() && simple.peek() != '\n'){
					content.append(simple.poll());
				}
				final boolean newline;
				if(simple.empty()){
					newline = false;
				}
				else{
					newline = true;
					simple.read('\n');
				}

				return new Line(indent, content.toString(), newline);

			}

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
