package majel.lang.descent.structure;

import majel.lang.automata.fsa.FSA;
import majel.lang.automata.fsa.StringProcessor;
import majel.lang.descent.lithp.Lithp1;
import majel.lang.descent.lithp.Lithp2;
import majel.lang.util.Pipe;
import majel.lang.descent.structure.indent.IndentToken;
import majel.lang.descent.structure.indent.IndentTree;
import majel.lang.util.IndexedToken;
import majel.lang.util.Mark;
import majel.lang.util.TokenStream$Char;
import majel.lang.util.TokenStream;
import majel.stream.Token$Char;
import majel.stream.Token;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.function.Function;

public class LinesParser implements Pipe<Token$Char, Line>{
	public static void main(String...args) throws IOException{
		Function<String, TokenStream$Char> streams = path -> {
			try{
				return TokenStream$Char.of(
					Thread.currentThread()
						.getContextClassLoader()
						.getResource(path)
						.openStream()
						.readAllBytes()
				);
			}
			catch(IOException e){
				throw new UncheckedIOException(e);
			}
		};
		var rootPipe = Pipe.<Token$Char>nop().retain(t -> t.value() != '\r');
		var lithpPipe = rootPipe.retain(t -> t.value() != '\n')
			.andThen(new Lithp1())
			.andThen(new Lithp2());

		var lithpSrc = streams.apply(".lithp/Test.lithp");
		var lithp = lithpPipe.parse(lithpSrc.wrap());
		var all = FSA.or(lithp.collect(ArrayList::new));

		var fooPipe = rootPipe.andThen(new StringProcessor(all));
		fooPipe.parse(
			streams.apply(".bspl/Simple.bspl").withHead('\n').wrap()
		)
		.forEach(System.err::println);

		System.exit(0);
		var resource = Thread.currentThread().getContextClassLoader().getResource(".bspl/Simple.bspl");

		var sink = new ArrayList<IndexedToken<Line>>();

		/*
		TODO:
			create or enhance tree structure parsing to incorporate recursive descent into the head for brackets parsing.
			[({<>})]
		 */
		interface TToken extends Token{}
		var headParser = new HeadParser();
		var parser = Pipe.<Token$Char>nop()
			.exclude(c -> c.value() == '\r')
			.andThen(new LinesParser())
			.exclude(Line::empty, sink::add)
			.andThen(new IndentParser())
			.andThen(new Pipe<IndentToken, TToken>(){
				@Override
				public TokenStream<TToken> parse(TokenStream<IndentToken> tokens){
					return new TokenStream<TToken>(){
						@Override
						public TToken poll(){
							var head = tokens.poll();
							if(head instanceof IndentTree t){
								headParser.parse(TokenStream.from(t.content())).poll();
							}
							throw new UnsupportedOperationException(head.getClass().getSimpleName());
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
			});

	}

	@Override
	public TokenStream<Line> parse(TokenStream<Token$Char> tokens){
		/*
		TODO:
			Spilt stream at target token, return lazy evaluated result
		 */
		return new TokenStream<>(){
			int lineNumber;
			@Override
			public Line peek(){
				var mark = mark();
				var rv = poll();
				mark.reset();
				return rv;
			}

			@Override
			public Line poll(){
				final var simple = TokenStream$Char.of(tokens);
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

				return new Line(lineNumber++, indent, content.toString(), newline);

			}

			@Override
			public boolean empty(){
				return tokens.empty();
			}

			@Override
			public Mark mark(){
				int m0 = lineNumber;
				var m1 = tokens.mark();

				return () -> {
					lineNumber = m0;
					m1.reset();
				};
			}
		};
	}

}
