package majel.lang.descent.lithp;

import majel.lang.descent.Decomposable;
import majel.lang.util.TokenStream$Char;
import majel.lang.util.TokenStream$Obj;
import majel.stream.Token$Char;
import majel.util.functional.TokenStreamBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface LithpExpression extends Decomposable<Token$Char>{
	char CLOSING_PARENTHESIS = ')';
	char OPENING_PARENTHESIS = '(';
	String DELIMITER = ", ";

	static List<LithpExpression> readList(
		TokenStream$Obj<Token$Char> tokens,
		TokenStream$Obj<LithpExpression> parsed
	){

		var simple = TokenStream$Char.of(tokens);
		simple.read(OPENING_PARENTHESIS);
		var elements = new ArrayList<LithpExpression>();
		if(simple.peek() != CLOSING_PARENTHESIS){
			elements.add(parsed.poll());
			while(simple.peek() != CLOSING_PARENTHESIS){
				simple.read(DELIMITER);
				elements.add(parsed.poll());
			}
		}
		simple.read(CLOSING_PARENTHESIS);

		return Collections.unmodifiableList(elements);
	}

	static String reconstituteList(List<LithpExpression> expressions){

		var builder = new StringBuilder()
			.append(OPENING_PARENTHESIS);

		if(!expressions.isEmpty()){
			builder.append(reconstituteSingle(expressions.get(0)));
			for(int i = 1; i < expressions.size(); i++){
				builder
					.append(DELIMITER)
					.append(reconstituteSingle(expressions.get(i)));
			}
		}

		return builder
			.append(CLOSING_PARENTHESIS)
			.toString();
	}

	static String reconstituteSingle(LithpExpression expression){
		return TokenStream$Char.of(expression.decompose()).remaining();
	}

	static TokenStream$Obj<Token$Char> streamList(List<? extends Decomposable<Token$Char>> elements){
		var builder = new TokenStreamBuilder();
		builder
			.feed(OPENING_PARENTHESIS);

		if(!elements.isEmpty()){
			builder.feed(elements.get(0).decompose());
			for(int i = 1; i < elements.size(); i++){
				builder
					.feed(DELIMITER)
					.feed(elements.get(i).decompose());
			}
		}

		builder.feed(CLOSING_PARENTHESIS);
		return builder.immutableView().wrap();
	}
}
