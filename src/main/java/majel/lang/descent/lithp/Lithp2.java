package majel.lang.descent.lithp;

import majel.lang.Parser;
import majel.lang.automata.fsa.FSA;
import majel.lang.descent.lithp.expressions.*;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Lithp2 implements Parser<LithpExpression, FSA>{

	final Map<Class<? extends LithpExpression>, Function<LithpExpression, FSA>> builders;
	final Map<String, FSA> named;
	public Lithp2(){
		builders = new HashMap<>();
		named = new HashMap<>();
		builders.put(
			AndExpression.class,
			expr -> {
				if(!(expr instanceof AndExpression a)){
					throw new IllegalStateException();
				}
				return FSA.and(parse(a.expressions()));
			}
		);
		builders.put(
			AndNotExpression.class,
			expr -> {
				if(!(expr instanceof AndNotExpression a)){
					throw new IllegalStateException();
				}
				var expressions = parse(
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
		builders.put(
			KleenExpression.class,
			expr -> {
				if(!(expr instanceof KleenExpression k)){
					throw new IllegalStateException();
				}
				return parse(k.wrapped()).kleene();
			}
		);
		builders.put(
			LiteralExpression.class,
			expr -> {
				if(!(expr instanceof LiteralExpression l)){
					throw new IllegalStateException();
				}
				return FSA.literal(l.value());
			}
		);
		builders.put(
			LookupExpression.class,
			expr -> {
				if(!(expr instanceof LookupExpression l)){
					throw new IllegalStateException();
				}
				return Optional.ofNullable(named.get(l.identifier())).orElseThrow();
			}
		);
		builders.put(
			NamedExpression.class,
			expr -> {
				if(!(expr instanceof NamedExpression n)){
					throw new IllegalStateException();
				}
				var name = n.name();
				var lower = name.toLowerCase();
				var base = parse(n.wrapped());
				var rv = base.named(lower);

				var shortForm = new StringBuilder();
				for(String s: SimpleTokenStream.from(name).split('-')){
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
		builders.put(
			NegationExpression.class,
			expr -> {
				if(!(expr instanceof NegationExpression n)){
					throw new IllegalStateException();
				}
				return parse(n.wrapped()).negate();
			}
		);
		builders.put(
			OptionalExpression.class,
			expr -> {
				if(!(expr instanceof OptionalExpression o)){
					throw new IllegalStateException();
				}
				return parse(o.wrapped()).optional();
			}
		);
		builders.put(
			OrExpression.class,
			expr -> {
				if(!(expr instanceof OrExpression o)){
					throw new IllegalStateException();
				}
				return FSA.or(parse(o.elements()));
			}
		);
		builders.put(
			ParenthesisExpression.class,
			expr -> {
				if(!(expr instanceof ParenthesisExpression p)){
					throw new IllegalStateException();
				}
				return FSA.concatenate(parse(p.elements()));
			}
		);
		builders.put(
			RangeExpression.class,
			expr -> {
				if(!(expr instanceof RangeExpression r)){
					throw new IllegalStateException();
				}
				return new FSA(c -> r.c0() <= c && c <= r.cN());
			}
		);
		builders.put(
			RepetitionExpression.class,
			expr -> {
				if(!(expr instanceof RepetitionExpression r)){
					throw new IllegalStateException();
				}
				return parse(r.base())
					.repeating(r.lower(), r.upper());
			}
		);
		builders.put(
			WildCardExpression.class,
			expr -> {
				if(!(expr instanceof WildCardExpression)){
					throw new IllegalStateException();
				}
				return new FSA(c -> true);
			}
		);
	}
	@Override
	public TokenStream<FSA> parse(TokenStream<LithpExpression> tokens){
		return tokens.map(
			expr ->
				builders
					.get(expr.getClass())
					.apply(expr)
		);
	}
}