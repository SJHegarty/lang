package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.descent.*;
import majel.lang.err.IllegalToken;
import majel.lang.util.TokenStream;
import majel.util.functional.CharPredicate;

import java.util.function.IntSupplier;

import static majel.lang.descent.lithp.Lithp.*;

public class Repetition implements CharHandler<FSA>{

	private static final CharPredicate DIGITS = CharPredicate.inclusiveRange('0', '9');
	private static final char HEAD_TOKEN = '#';
	private static final char UNBOUND = '+';
	private static final String CONTINUATION = "...";

	@Override
	public char headToken(){
		return HEAD_TOKEN;
	}

	@Override
	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
		checkHead(tokens);
		tokens.read(OPENING_PARENTHESIS);
		IntSupplier intReader = () -> {
			var builder = new StringBuilder();
			while(DIGITS.test(tokens.peek())){
				builder.append(tokens.poll());
			}
			return Integer.parseInt(builder.toString());
		};
		final int lower = intReader.getAsInt();

		final int upper = switch(tokens.peek()){
			case '.' -> {
				tokens.read(CONTINUATION);
				yield intReader.getAsInt();
			}
			case UNBOUND -> {
				tokens.poll();
				yield Integer.MAX_VALUE;
			}
			case ',' -> lower;
			default -> throw new IllegalToken(tokens);
		};

		tokens.read(DELIMITER);
		var base = parser.parse(tokens);
		tokens.read(CLOSING_PARENTHESIS);

		return new Expression<>(){
			@Override
			public String reconstitute(){
				var builder = new StringBuilder()
					.append(HEAD_TOKEN)
					.append(OPENING_PARENTHESIS)
					.append(lower);

				if(upper != lower){
					if(upper == Integer.MAX_VALUE){
						builder.append(UNBOUND);
					}
					else{
						builder.append(CONTINUATION).append(upper);
					}
				}

				return builder
					.append(DELIMITER)
					.append(base.reconstitute())
					.append(CLOSING_PARENTHESIS)
					.toString();
			}

			@Override
			public FSA build(RecursiveDescentBuildContext<FSA> context){
				return base.build(context).repeating(lower, upper);
			}
		};
	}
}
