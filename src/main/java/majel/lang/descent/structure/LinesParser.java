package majel.lang.descent.structure;

import majel.lang.automata.fsa.FSA;
import majel.lang.automata.fsa.StringProcessor;
import majel.lang.descent.context.NullContext;
import majel.lang.descent.lithp.Lithp1;
import majel.lang.descent.lithp.Lithp2;
import majel.lang.descent.structure.indent2.FooToken;
import majel.lang.descent.structure.indent2.Line;
import majel.lang.descent.structure.indent2.SimpleTree;
import majel.lang.err.IllegalToken;
import majel.lang.util.*;
import majel.stream.StringToken;
import majel.stream.Token;
import majel.stream.Token$Char;
import majel.util.LambdaUtils;
import majel.util.Opt;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static majel.lang.descent.structure.indent2.UnclosedTreeParser.CLOSE_BRACKETS;
import static majel.lang.descent.structure.indent2.UnclosedTreeParser.OPEN_BRACKETS;

/*
TODO:
	Add expression regeneration to FSA class
	There can be no guarantee that the returned expression will equal the parsed expression
		However upon reparsing the XOR of the two machines should be the empty language.
 */
public class LinesParser{
	public static void main(String...args) throws IOException{
		Function<String, TokenStream_Char> streams = path -> {
			try{
				return TokenStream_Char.of(
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
		var rootPipe = Pipe.<NullContext, Token$Char>nop().buffered();
		var lithpPipe = rootPipe.retain(t -> !t.is('\n') && !t.is('\r'))
			.andThen(new Lithp1())
			.andThen(new Lithp2());

		var lithpSrc = streams.apply(".lithp/Test.lithp");
		var lithp = LambdaUtils.benchmark(() -> lithpPipe.parse(NullContext.instance, lithpSrc.wrap()));
		System.err.println(lithp.time());
		var all = FSA.or(lithp.result().collect(ArrayList::new));



/*
TODO: remove empty lines. This requires look-ahead two filtering, a later Yak to shave

TODO: Nowish: Add filtering of empty lines.
  At the moment they are being factored into the building of the tree structure,
  which can cause premature EOT (end-of-tree).
 */

		List<IndexedToken<StringToken>> filtered = new ArrayList<>();
		record ClosingContext(){}
		interface BarToken extends Token{

		}
		class TreeCloser implements Pipe<NullContext, FooToken, BarToken>{

			@Override
			public TokenStream_Obj<BarToken> parse(NullContext context, TokenStream_Obj<FooToken> tokens){
				return new TokenStream_Obj<BarToken>(){
					@Override
					public BarToken poll(){
						final var head = tokens.poll();
						final TokenStream_Obj<StringToken> headStream = TokenStream_Obj.from(
							head.headTokens()
						);

						final List<BarToken> children = Opt.Gen.tryGet(((SimpleTree) head)::children)
							.map(c -> parse(context, TokenStream_Obj.from(c)))
							.orGet(TokenStream_Obj::emptyStream)
							.collect(ArrayList::new);

						final TokenStream_Obj<StringToken> tailStream = TokenStream_Obj.lazy(
							() -> Opt.Gen
								.when(!tokens.empty(), tokens::poll)
								.tryMap(t -> TokenStream_Obj.from(((Line)t).headTokens()))
								.orGet(TokenStream_Obj::emptyStream)
						);

						record BarTree(){

						}
						class Builder{
							public BarToken build(){
								var mark = headStream.mark();
								var head = headStream.poll();
								var labels = head.labels();
								if(labels.contains(OPEN_BRACKETS)){
									/*
									Figure out the expected terminator
									Scan through the head tokens until it is found
									if it's unavailable in the head find it in the tail
									If it's not in the tail, explode.
									 */
								}
								else if(labels.contains(CLOSE_BRACKETS)){
									mark.reset();
									throw new IllegalToken(tokens);
								}
								else{

								}
								throw new UnsupportedOperationException();
							}
						}

				/*
				TODO:
					Parse the head, if it's self closing it has no tail.
					If it's not self-closing the next element must be a line, and must close the head.
					Parse the children by opening the list as a indent stream parsing and collecting.
				 */
						return null;
					}

					@Override
					public boolean touched(){
						return tokens.touched();
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
		var fooPipe = rootPipe
			.andThen(new StringProcessor(all))
			/*.andThen(new Dealiaser<>())
			.exclude(
				2,
				tokens -> {
					var labels = tokens.poll().labels();
					if(labels.contains(WHITE_SPACE)){
						return true;
					}
					if(tokens.empty() || !labels.contains(LINE_HEAD)){
						return false;
					}
					return tokens.poll().labels().contains(LINE_HEAD);
				},
				filtered::add
			)
			.andThen(new UnclosedTreeParser())*/;
/*
TODO:
	At some point, buffering should be introduced.
		Every time that any stream is reset, all of the parsers in the pipe-line are rerun.
			This is despite the fact that universally they will all return the same results when re-executed.
			This could be optimised, but it does not break the API. It's a delayable yak.
	There should be context aware parsers:
		The next parser level should operate on the line head,
		 - from here, it generates a LineContext object,
		  - the lineContext should be a record containing the token depth
			(
				it should need nothing else,
				 however additional context aware parsers can be arbitrarily introduced at greater depths
			)
		  - It can be fed in as a parameter to LineHandlers
		  	The subparser is fed a substream that terminates at the first line-head token
		  	SimpleHandler: Everything that's not a bracket.
		  	BracketHandlers
		  	 - Complexity here revolves around the requirement to also read the tail if the brackets are not terminated in the head.
		  	 The sub-parser that empties the head keeps the children.
		  		The children are those elements that have a greater indent than the line context,
		  			halting at the first element that does not.
		  		The tail is the first element after the children if it has the same indent as the context.
		  		If the tail is required but is not there, an explosion occurs - tail expected but was not present, unclosed Brackets.
		  		If the tail is untouched, the head was self-closing, there is no tail - so it is left as a peeked item.
		  		If the tail is touched, it is expected to be fully consumed and it is polled.
		  			(
						That the result is valid should be enforced rather effectively by premature ends of streams
							The error messaging in these situations could be improved significantly.
		  				A more bespoke implementation could provide a message similar to the one above:
		  					end of tail reached unexpectedly, unclosed brackets (opened @ index:x), expected e.g.: ']'
		  			)
		  		This is fairly elegant, other than that I need to universally implement the touched method.
		  			This would be a quicker job if all of my wrapper classes used some sort of proxy instead.
		  			But they don't.
		  				And I'm happy to waste a bit of time implementing pain in the arse functionality, knowing that I'll do it better later.
		  				I'm not in the mood to shave that yak right now.
		So:
			Implement touched.
			Brackets parsers.
			Simple Parsers.

 */
		var foo = streams.apply(".lithp/Test.txt").withHead('\n');
		var bench = LambdaUtils.benchmark(
			() -> fooPipe.parse(
				NullContext.instance,
				foo.wrap()
			)
				.collect(ArrayList::new)
		);
		System.err.println(bench.time());
		bench.result().forEach(System.err::println);
		System.exit(0);
	}

}
