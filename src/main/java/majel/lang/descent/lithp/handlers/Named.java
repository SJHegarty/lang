package majel.lang.descent.lithp.handlers;

import majel.lang.automata.fsa.FSA;
import majel.lang.automata.fsa.StringProcessor;
import majel.lang.descent.lithp.Handler;
import majel.lang.descent.lithp.RecursiveDescentContext;
import majel.lang.descent.lithp.RecursiveDescentParser;
import majel.lang.descent.lithp.TokenStream;

public class Named extends Handler<FSA>{

	public Named(RecursiveDescentParser<FSA> parser){
		super(parser);
	}

	@Override
	public char headToken(){
		return '<';
	}

	private transient StringProcessor processor;

	@Override
	public FSA parse(RecursiveDescentContext<FSA> context){
		if(processor == null){
			processor = new StringProcessor(
				parser.build("(*[a...z]?*('-'*[a...z]))")
			);
		}
		var tokens = context.tokens();
		checkHead(tokens);
		tokens.read('(');
		var name = processor.process(tokens).value();
		if(name.length() == 0){
			throw new RecursiveDescentParser.IllegalExpression(tokens);
		}
		tokens.read(", ");
		var base = parser.parse(context);
		tokens.poll();
		var rv = base.named(name);
		context.register(name, rv);
		return rv;
	}
}
