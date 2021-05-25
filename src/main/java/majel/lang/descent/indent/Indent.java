package majel.lang.descent.indent;

import majel.lang.descent.Expression;
import majel.lang.descent.PredicateHandler;
import majel.lang.descent.RecursiveDescentBuildContext;
import majel.lang.descent.RecursiveDescentParser;
import majel.lang.err.IllegalExpression;
import majel.lang.util.TokenStream;
import majel.util.ObjectUtils;
import majel.util.functional.CharPredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class Indent extends RecursiveDescentParser<Indent.Foo>{
	static class Foo{

	}

	public static void main(String...args){
		String sample = """
			test{
				yo.{
					...
					blah
				}
			}
			""";

		System.err.println(sample);
		new Indent().parse(sample);
	}

	static class IndentTree implements Expression<Foo>{
		final List<Expression<Foo>> children = new ArrayList<>();
		private final boolean newline;
		private final boolean terminated;
		private final int depth;
		private final String head;

		IndentTree(RecursiveDescentParser<Foo> parser, TokenStream tokens){
			final IntSupplier depthReader = () -> tokens.readWhile(c -> c == '\t').length();
			this.depth = depthReader.getAsInt();
			{
				final String head = tokens.readWhile(c -> c != '\n');
				int length = head.length();
				block:{
					if(length != 0){
						int limit = length - 1;
						if(head.charAt(limit) == '{'){
							this.head = head.substring(0, limit);
							this.terminated = true;
							break block;
						}
					}
					this.head = head;
					this.terminated = false;
				}
			}
			if(tokens.empty()){
				if(terminated){
					throw new IllegalExpression(tokens);
				}
				this.newline = false;
			}
			else{
				tokens.read('\n');
				while(!tokens.empty()){
					var mark = tokens.mark();
					int childDepth = depthReader.getAsInt();
					if(childDepth > depth || tokens.empty() || tokens.peek() == '\n'){
						mark.reset();
						children.add(parser.parse(tokens));
					}
					else{
						mark.reset();
						break;
					}
				}
				if(terminated){
					if(depthReader.getAsInt() != depth || tokens.poll() != '}'){
						throw new IllegalExpression(tokens);
					}
					if(tokens.empty()){
						newline = false;
					}
					else{
						tokens.read('\n');
						newline = true;
					}
				}
				else{
					newline = children.isEmpty();
				}
			}
		}

		@Override
		public String reconstitute(){
			var indent = new String(ObjectUtils.repeating('\t', depth));
			var builder = new StringBuilder()
				.append(indent)
				.append(head);

			if(terminated){
				builder.append("{\n");
			}
			for(var c: children){
				builder.append(c.reconstitute());
			}
			if(terminated){
				builder.append(indent).append('}');
			}
			if(newline){
				builder.append('\n');
			}
			return builder.toString();
		}

		@Override
		public Foo build(RecursiveDescentBuildContext<Foo> context){
			return null;
		}

	}

	Indent(){
		registerHandler(
			() -> new PredicateHandler<>(){
				@Override
				public CharPredicate headPredicate(){
					return c -> c != '\n';
				}

				@Override
				public Expression<Foo> parse(RecursiveDescentParser<Foo> parser, TokenStream tokens){
					return new IndentTree(parser, tokens);
				}
			}
		);
	}
}
