package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.automata.fsa.StringProcessor;
import majel.lang.descent.Expression;
import majel.lang.descent.CharHandler;
import majel.lang.descent.RecursiveDescentBuildContext;
import majel.lang.descent.RecursiveDescentParser;
import majel.lang.util.TokenStream;

import static majel.lang.descent.lithp.Lithp.*;

public class Named implements CharHandler<FSA>{

	private static final char HEAD_TOKEN = '<';

	@Override
	public char headToken(){
		return HEAD_TOKEN;
	}

	private transient StringProcessor processor;

	@Override
	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
		if(processor == null){
			var word = "(*[a...z]?[A...Z]?*[a...z])";
			var expr = new StringBuilder()
				.append("(")
				.append(word)
				.append("?*('-'").append(word).append(")")
				.append(")");

			processor = new StringProcessor(
				parser.build(expr.toString())
			);
		}
		checkHead(tokens);
		tokens.read(OPENING_PARENTHESIS);
		var name = processor.process(tokens).value();
		tokens.read(DELIMITER);
		var base = parser.parse(tokens);
		tokens.read(CLOSING_PARENTHESIS);

		return new Expression<>(){
			@Override
			public String reconstitute(){
				return new StringBuilder()
					.append(HEAD_TOKEN)
					.append(OPENING_PARENTHESIS)
					.append(name)
					.append(DELIMITER)
					.append(base.reconstitute())
					.append(CLOSING_PARENTHESIS)
					.toString();
			}

			@Override
			public FSA build(RecursiveDescentBuildContext<FSA> context){

				var lower = name.toLowerCase();
				var rv = base.build(context).named(lower);

				var shortForm = new StringBuilder();
				for(String s: new TokenStream(name).split('-')){
					final char segchar;
					final char[] chars = s.toCharArray();
					block:{
						for(int i = 1; i < chars.length; i++){
							if(Character.isUpperCase(chars[i])){
								segchar = chars[i];
								break block;
							}
						}
						segchar = Character.toUpperCase(chars[0]);
					}
					shortForm.append(segchar);
				}
				context.register(lower, rv);
				context.register(shortForm.toString(), rv);
				return rv;
			}
		};
	}
}
