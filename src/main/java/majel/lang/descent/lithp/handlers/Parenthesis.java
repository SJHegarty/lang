package majel.lang.descent.lithp.handlers;

import majel.lang.descent.CharHandler;
import majel.lang.descent.lithp.LithpExpression;
import majel.lang.descent.lithp.expressions.ParenthesisExpression;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

import java.util.ArrayList;
import java.util.Collections;

public class Parenthesis implements CharHandler<LithpExpression>{

	@Override
	public char headToken(){
		return LithpExpression.OPENING_PARENTHESIS;
	}

//	@Override
//	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
//		checkHead(tokens);
//		var expressions = parser.parseUntil(tokens, LithpExpression.CLOSING_PARENTHESIS);
//		tokens.poll();
//		return new Expression<>(){
//			@Override
//			public String reconstitute(){
//				var builder = new StringBuilder().append(LithpExpression.OPENING_PARENTHESIS);
//				for(var expr: expressions){
//					builder.append(expr.reconstitute());
//				}
//				return builder.append(LithpExpression.CLOSING_PARENTHESIS).toString();
//			}
//
//			/*@Override
//			public FSA build(RecursiveDescentBuildContext<FSA> context){
//				return FSA.concatenate(
//					expressions.stream()
//						.map(expr -> expr.build(context))
//						.toArray(FSA[]::new)
//				);
//			}*/
//		};
//	}

	@Override
	public LithpExpression parse(TokenStream<SimpleToken> tokens, TokenStream<LithpExpression> parsed){
		checkHead(tokens);
		var simple = SimpleTokenStream.of(tokens);
		var elements = new ArrayList<LithpExpression>();
		while(simple.peek() != LithpExpression.CLOSING_PARENTHESIS){
			elements.add(parsed.poll());
		}
		simple.poll();
		return new ParenthesisExpression(Collections.unmodifiableList(elements));
	}
}
