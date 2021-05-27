package majel.lang.descent.lithp;

import majel.lang.descent.Expression;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface LithpExpression extends Expression{
	char CLOSING_PARENTHESIS = ')';
	char OPENING_PARENTHESIS = '(';
	String DELIMITER = ", ";

	static List<LithpExpression> readList(
		TokenStream<SimpleToken> tokens,
		TokenStream<LithpExpression> parsed
	){

		var simple = SimpleTokenStream.of(tokens);
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
			builder.append(expressions.get(0).reconstitute());
			for(int i = 1; i < expressions.size(); i++){
				builder
					.append(DELIMITER)
					.append(expressions.get(i).reconstitute());
			}
		}

		return builder
			.append(CLOSING_PARENTHESIS)
			.toString();
	}

}
