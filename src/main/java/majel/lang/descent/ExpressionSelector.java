package majel.lang.descent;

import majel.lang.automata.fsa.FSA;
import majel.lang.automata.fsa.Node;
import majel.lang.automata.fsa.StringProcessor;
import majel.lang.descent.lithp.Lithp;
import majel.lang.err.IllegalExpression;
import majel.lang.util.TokenStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpressionSelector<T> implements HandlerSelector<T>{

	private final Map<String, ExpressionHandler<T>> handlers = new HashMap<>();
	private StringProcessor processor;

	protected void registerHandler(ExpressionHandler<T> handler){
		processor = null;
		handlers.put(Long.toString(System.nanoTime()), handler);
	}

	protected void validate(){
		if(processor != null){
			return;
		}
		final FSA tail = new Lithp().build("?*.");
		/*
		TODO:
			build a lookup table mapping generated named to parsers.
			Lookup is performed by getting the name associate with the terminating state of the found machine.
		*/
		final FSA[] extended = handlers.entrySet().stream()
			.map(
				entry -> FSA
					.concatenate(
						entry.getValue().headProcessor().automaton(),
						tail
					)
					.named(entry.getKey())
			)
			.toArray(FSA[]::new);

		final var or = FSA.or(extended).dfa();
		final var collisions = or.nodes().stream()
			.filter(Node::terminating)
			.filter(n -> n.labels().size() > 1)
			.collect(Collectors.toList());

		if(!collisions.isEmpty()){
			throw new IllegalStateException(collisions.toString());
		}

		this.processor = new StringProcessor(
			FSA.or(
				handlers.entrySet().stream()
					.map(
						entry -> entry.getValue().headProcessor().automaton().named(entry.getKey())
					)
					.toArray(FSA[]::new)
			)
		);
	}

	@Override
	public Handler<T> handlerFor(TokenStream tokens){
		if(processor == null){
			throw new IllegalStateException();
		}
		var mark = tokens.mark();
		for(var label: processor.process(tokens).node().labels()){
			var handler = handlers.get(label);
			if(handler != null){
				return handler;
			}
		}
		mark.reset();
		throw new IllegalExpression(tokens);
	}
}
