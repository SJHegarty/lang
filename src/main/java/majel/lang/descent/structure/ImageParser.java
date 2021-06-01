package majel.lang.descent.structure;

import majel.lang.Parser;
import majel.lang.descent.structure.indent.IndentHidden;
import majel.lang.descent.structure.indent.IndentLine;
import majel.lang.descent.structure.indent.IndentToken;
import majel.lang.descent.structure.indent.IndentTree;
import majel.lang.descent.structure.render.image.Image;
import majel.lang.util.Mark;
import majel.lang.util.TokenStream;

import java.util.ArrayList;
import java.util.function.Function;

public class ImageParser implements Parser<IndentToken, Image>{

	public Image parseSingle(IndentToken token){
			if(token instanceof IndentTree t){
				/*Image header = new Image.ColourBlock(t.content().length(), 1, 0xff0080ff);
				Image[] children;

				Y_LAYOUT.parse(
					TokenStream.of(t.children()).map(this::parseSingle)
				);

				TokenStream.of(t.children()).indexed().map(
					indexed -> {
						int index = indexed.index();
						return parseSingle(t.children()[index]);

					}
				);*/
			}
			if(token instanceof IndentLine line){
				return new Image.BlockImage(0, 0, line.content().length(), 1, 0xff0080ff);
			}
			if(token instanceof IndentTree tree){
				var head = new Image.BlockImage(0, 0, tree.content().length(), 1, 0xffff8000);
				var elements = TokenStream.of((Image)head).concat(
					() -> Y_LAYOUT
						.parse(
							TokenStream.of(tree.children())
								.map(this::parseSingle)
						)
						.map(i -> i.offset(4, 1))
				)
					.collect(ArrayList::new);

				for(var e: elements){
					System.err.println("??? " + e.y0() + " " + e.x0());
				}
				var rv = new Image.CompositeImage(elements);
				System.err.println("..........." + rv.height());
				return rv;
			}
			if(token instanceof IndentHidden h){
				return parseSingle(h.wrapped()).offset(4, 0);
			}
			throw new UnsupportedOperationException(token.getClass().toString());
	}
	@Override
	public TokenStream<Image> parse(TokenStream<IndentToken> tokens){
		return tokens.map(this::parseSingle);
	}

	public static final Parser<Image, Image> Y_LAYOUT = new Deltad(i -> new Delta(0, i.height()));
	private static final Parser<Image, Image> X_LAYOUT = new Deltad(i -> new Delta(i.width(), 0));

	private static final class Deltad implements Parser<Image, Image>{
		private final Function<Image, Delta> deltasBuilder;

		public Deltad(Function<Image, Delta> deltasBuilder){
			this.deltasBuilder = deltasBuilder;
		}

		@Override
		public TokenStream<Image> parse(TokenStream<Image> tokens){
			return new TokenStream<Image>(){
				Delta delta = new Delta(0, 0);
				@Override
				public Image peek(){
					return tokens.peek().offset(delta);
				}

				@Override
				public Image poll(){
					var rv = tokens.poll().offset(delta);
					delta = delta.delta(deltasBuilder.apply(rv));
					return rv;
				}

				@Override
				public boolean empty(){
					return tokens.empty();
				}

				@Override
				public Mark mark(){
					var m0 = delta;
					var m1 = tokens.mark();
					return () -> {
						delta = m0;
						m1.reset();
					};
				}
			};
		}
	};

	record Location(int x, int y){}
}
