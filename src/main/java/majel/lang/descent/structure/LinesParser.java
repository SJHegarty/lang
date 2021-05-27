package majel.lang.descent.structure;

import majel.lang.Parser;
import majel.lang.util.Mark;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

public class LinesParser implements Parser<SimpleToken, Line>{
	public static void main(String...args){
		String content = """
			This is a test
				this is a line
					yo
					foo
					bar
				back
			More things
			""";
		System.err.println(content);
		var builder = new StringBuilder();
		for(var l:
			new IndentParser().parse(
				new LinesParser().parse(
					SimpleTokenStream.from(content).wrap()
				)
			)
		){
			System.err.println(l);
			builder.append(l.reconstitute());
		}
		if(!builder.toString().equals(content)){
			throw new IllegalStateException();
		}
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
