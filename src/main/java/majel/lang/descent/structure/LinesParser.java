package majel.lang.descent.structure;

import majel.lang.Parser;
import majel.lang.descent.structure.indent.IndentToken;
import majel.lang.util.Mark;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

import java.util.ArrayList;

public class LinesParser implements Parser<SimpleToken, Line>{
	public static void main(String...args){
		String content = """
			This is a test
				this is a line
						overdent
					yo
					foo
					
					bar
				back
			More things
			""";
		System.err.println(content);
		var sink = new ArrayList<TokenStream.IndexedToken<Line>>();
		String reconstructed = SimpleTokenStream.of(
			new IndentParser().parse(
				new LinesParser().parse(
					SimpleTokenStream.from(content).wrap()
				)
				.retain(l -> !l.empty(), sink::add)
			)
			.unwrap(IndentToken::decompose)
			.incorporate(TokenStream.from(sink))
			.unwrap(Line::decompose)
		)
		.remaining();

		System.err.println(sink);
		System.err.println(reconstructed);
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
