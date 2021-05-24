package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.automata.fsa.StringProcessor;
import majel.lang.descent.*;
import majel.lang.err.IllegalExpression;
import majel.lang.util.TokenStream;

public class Lookup implements Handler<FSA>{

	private static final char HEAD_TOKEN = '@';
	private static final char TERMINATING_TOKEN = ';';

	@Override
	public char headToken(){
		return HEAD_TOKEN;
	}

	private transient StringProcessor processor;

	@Override
	public Expression<FSA> parse(RecursiveDescentParser<FSA> parser, TokenStream tokens){
		if(processor == null){
			var exprLC = "(*[a...z]?*('-'*[a...z]))";
			var exprUC = "*[A...Z]";
			var expr = "+(" + exprLC + ", " + exprUC + ")";
			processor = new StringProcessor(
				parser.build(expr)
			);
		}
		checkHead(tokens);
		var name = processor.process(tokens).value();
		if(name.length() == 0){
			throw new IllegalExpression(tokens);
		}
		tokens.read(TERMINATING_TOKEN);
		return new Expression<>(){
			@Override
			public String reconstitute(){
				return HEAD_TOKEN + name + TERMINATING_TOKEN;
			}

			@Override
			public FSA build(RecursiveDescentBuildContext<FSA> context){
				return context.named(name);
			}
		};
	}
}
