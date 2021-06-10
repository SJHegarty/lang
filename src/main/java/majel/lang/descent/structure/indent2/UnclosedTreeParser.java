package majel.lang.descent.structure.indent2;

import majel.lang.descent.context.NullContext;
import majel.lang.err.IllegalToken;
import majel.lang.util.Mark;
import majel.lang.util.Pipe;
import majel.lang.util.TokenStream_Obj;
import majel.stream.StringToken;
import majel.util.Opt;

import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class UnclosedTreeParser implements Pipe<NullContext, StringToken, FooToken>{
	public static final String LINE_HEAD = "line-head";
	public static final String WHITE_SPACE = "white-space";
	public static final String OPEN_BRACKETS = "open-brackets";
	public static final String CLOSE_BRACKETS = "close-brackets";

	@Override
	public TokenStream_Obj<FooToken> parse(NullContext context, TokenStream_Obj<StringToken> tokens){
		return new TokenStream_Obj<>(){
			@Override
			public FooToken poll(){
				final Supplier<Opt<StringToken>> witchDoctor = () -> {
					if(tokens.empty()){
						return Opt.Gen.empty();
					}
					var mark = tokens.mark();
					var head = tokens.poll();
					if(!head.labels().contains(LINE_HEAD)){
						mark.reset();
						throw new IllegalToken(tokens);
					}
					return Opt.Gen.of(head);
				};
				final StringToken head = witchDoctor.get().value();
				final ToIntFunction<StringToken> indentCounter = lineHead -> {
					if(!lineHead.labels().contains(LINE_HEAD)){
						throw new IllegalStateException("Bad monkey.");
					}
					return lineHead.value().length() - 1;
				};
				var elements = tokens
					.until(l -> l.labels().contains(LINE_HEAD))
					.collect(ArrayList::new);

				final int headLength = indentCounter.applyAsInt(head);
				var children = parse(
					NullContext.instance,
					tokens
						.until(t -> t.labels().contains(LINE_HEAD) && indentCounter.applyAsInt(t) <= headLength)
				)
				.collect(ArrayList::new);

				var line = new Line(head, elements);
				return children.isEmpty() ? line : new SimpleTree(line, children);
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
		