package majel.lang.descent.lithp;

import majel.lang.descent.context.NullContext;
import majel.lang.util.Pipe;
import majel.lang.automata.fsa.FSA;
import majel.lang.descent.lithp.expressions.*;
import majel.lang.util.TokenStream_Char;
import majel.lang.util.TokenStream_Obj;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Lithp2 implements Pipe<NullContext, LithpExpression, FSA>{

	final Map<Class<? extends LithpExpression>, Function<LithpExpression, FSA>> builders;
	final Map<String, FSA> named;
	public Lithp2(){
		builders = new HashMap<>();
		named = new HashMap<>();
		registerHandler(
			AndExpression.class,
			a -> FSA.and(parse(NullContext.instance, a.expressions()))
		);
		registerHandler(
			AndNotExpression.class,
			a -> {
				var expressions = parse(
					NullContext.instance,
					Arrays.asList(
						a.expr0(),
						a.expr1()
					)
				);
				return FSA.and(
					expressions.get(0),
					expressions.get(1).negate()
				);
			}
		);
		registerHandler(
			EscapeExpression.class,
			e -> new FSA(e.represented())
		);
		registerHandler(
			KleenExpression.class,
			k -> parse(NullContext.instance, k.wrapped()).kleene()
		);
		registerHandler(
			LiteralExpression.class,
			l -> FSA.literal(l.value())
		);
		registerHandler(
			LookupExpression.class,
			l -> Optional.ofNullable(named.get(l.identifier())).orElseThrow()
		);
		registerHandler(
			NamedExpression.class,
			n -> {
				var name = n.name();
				var lower = name.toLowerCase();
				var base = parse(NullContext.instance, n.wrapped());
				var rv = base.named(lower);

				var shortForm = new StringBuilder();
				for(String s: TokenStream_Char.from(name).split('-')){
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
				named.put(lower, rv);
				named.put(shortForm.toString(), rv);
				return rv;
			}
		);
		registerHandler(
			NegationExpression.class,
			n -> parse(NullContext.instance, n.wrapped()).negate()
		);
		registerHandler(
			OptionalExpression.class,
			o -> parse(NullContext.instance, o.wrapped()).optional()
		);
		registerHandler(
			OrExpression.class,
			o -> FSA.or(parse(NullContext.instance, o.elements()))
		);
		registerHandler(
			ParenthesisExpression.class,
			p -> FSA.concatenate(parse(NullContext.instance, p.elements()))
		);
		registerHandler(
			RangeExpression.class,
			r -> new FSA(c -> r.c0() <= c && c <= r.cN())
		);
		registerHandler(
			RepetitionExpression.class,
			r -> parse(NullContext.instance, r.base()).repeating(r.lower(), r.upper())
		);
		registerHandler(
			WildCardExpression.class,
			expr -> new FSA(c -> true)
		);
	}
	@Override
	public TokenStream_Obj<FSA> parse(NullContext ignored, TokenStream_Obj<LithpExpression> tokens){
		return tokens.map(
			expr ->
				builders
					.get(expr.getClass())
					.apply(expr)
		);
	}

	public <T extends LithpExpression> void registerHandler(Class<T> type, Function<T, FSA> builder){
		builders.put(
			type,
			expr -> builder.apply((T)expr)
		);
	}


}