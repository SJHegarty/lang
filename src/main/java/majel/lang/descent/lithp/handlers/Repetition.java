package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.RepetitionExpression;
import majel.lang.err.IllegalToken;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;
import majel.util.functional.CharPredicate;

import java.util.function.IntSupplier;

import static majel.lang.descent.lithp.expressions.RepetitionExpression.CONTINUATION;
import static majel.lang.descent.lithp.expressions.RepetitionExpression.UNBOUND;

public class Repetition implements CharHandler<LithpExpression>{

	private static final CharPredicate DIGITS = CharPredicate.inclusiveRange('0', '9');

	@Override
	public char headToken(){
		return RepetitionExpression.HEAD_TOKEN;
	}

//	@Override
//	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
//
//		return new Expression<>(){
//			@Override
//			public String reconstitute(){
//				var builder = new StringBuilder()
//					.append(HEAD_TOKEN)
//					.append(LithpExpression.OPENING_PARENTHESIS)
//					.append(lower);
//
//				if(upper != lower){
//					if(upper == Integer.MAX_VALUE){
//						builder.append(UNBOUND);
//					}
//					else{
//						builder.append(CONTINUATION).append(upper);
//					}
//				}
//
//				return builder
//					.append(LithpExpression.DELIMITER)
//					.append(base.reconstitute())
//					.append(LithpExpression.CLOSING_PARENTHESIS)
//					.toString();
//			}
//
//			/*@Override
//			public FSA build(RecursiveDescentBuildContext<FSA> context){
//				return base.build(context).repeating(lower, upper);
//			}*/
//		};
//	}

	@Override
	public LithpExpression parse(TokenStream<SimpleToken> tokens, TokenStream<LithpExpression> parsed){
		checkHead(tokens);
		var simple = SimpleTokenStream.of(tokens);
		simple.read(LithpExpression.OPENING_PARENTHESIS);
		IntSupplier intReader = () -> {
			var builder = new StringBuilder();
			while(DIGITS.test(simple.peek())){
				builder.append(simple.poll());
			}
			return Integer.parseInt(builder.toString());
		};
		final int lower = intReader.getAsInt();

		final int upper = switch(simple.peek()){
			case '.' -> {
				simple.read(CONTINUATION);
				yield intReader.getAsInt();
			}
			case UNBOUND -> {
				tokens.poll();
				yield Integer.MAX_VALUE;
			}
			case ',' -> lower;
			default -> throw new IllegalToken(tokens);
		};

		simple.read(LithpExpression.DELIMITER);
		var base = parsed.poll();
		simple.read(LithpExpression.CLOSING_PARENTHESIS);

		return new RepetitionExpression(lower, upper, base);
	}
}
