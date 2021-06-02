package majel.lang.descent.structure;

import majel.lang.Parser;
import majel.lang.descent.structure.indent.IndentToken;
import majel.lang.descent.structure.render.image.Image;
import majel.lang.util.IndexedToken;
import majel.lang.util.Mark;
import majel.lang.util.SimpleTokenStream;
import majel.lang.util.TokenStream;
import majel.stream.SimpleToken;
import majel.stream.Token;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

import static majel.lang.descent.structure.ImageParser.Y_LAYOUT;

public class LinesParser implements Parser<SimpleToken, Line>{
	record RenderResult() implements Token{}
	static class ImageRenderer implements Parser<Image, RenderResult>{
		private final BufferedImage content;
		private final Function<Image, RenderResult> renderer;
		ImageRenderer(){
			this.content = new BufferedImage(1600, 1200, BufferedImage.TYPE_INT_RGB);
			final var panel = new JPanel(){
				@Override
				public void paint(Graphics g){
					g.drawImage(content, 0, 0, this);
				}
			};
			final var frame = new JFrame();
			renderer = i -> {
				for(int dx = 0; dx < i.width(); dx++){
					for(int dy = 0; dy < i.height(); dy++){
						int x = i.x0() + dx;
						int y = i.y0() + dy;
						if(i.contains(x, y)){
							content.setRGB(x, y, i.colourAt(x, y));
						}
					}
				}
				return new RenderResult();
				//throw new UnsupportedOperationException(i.toString());
			};
			frame.getContentPane().add(panel);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}


		@Override
		public TokenStream<RenderResult> parse(TokenStream<Image> tokens){
			return tokens.map(renderer);
		}
	}
	public static void main(String...args) throws IOException{
		var resource = Thread.currentThread().getContextClassLoader().getResource(".bspl/Test.bspl");

		var sink = new ArrayList<IndexedToken<Line>>();

		var parser = Parser.<SimpleToken>empty()
			.exclude(c -> c.character() == '\r')
			.andThen(new LinesParser())
			.exclude(Line::empty, sink::add)
			.andThen(new IndentParser())
			.andThen(new ImageParser())
			.andThen(Y_LAYOUT)
			.map(i -> i.scale(6))
			.andThen(new ImageRenderer());

		final var stream = SimpleTokenStream.of(resource.openStream()).wrap();

		parser.parse(stream).forEach(System.err::println);
	}

	@Override
	public TokenStream<Line> parse(TokenStream<SimpleToken> tokens){
		return new TokenStream<>(){
			int lineNumber;
			@Override
			public Line peek(){
				var mark = mark();
				var rv = poll();
				mark.reset();
				return rv;
			}

			@Override
			public Line poll(){
				final var simple = SimpleTokenStream.of(tokens);
				final int indent;
				{
					int count = 0;
					while(!simple.empty() && simple.peek() == '\t'){
						simple.poll();
						count++;
					}
					indent = count;
				}
				final var content = new StringBuilder();
				while(!simple.empty() && simple.peek() != '\n'){
					content.append(simple.poll());
				}
				final boolean newline;
				if(simple.empty()){
					newline = false;
				}
				else{
					newline = true;
					simple.read('\n');
				}

				return new Line(lineNumber++, indent, content.toString(), newline);

			}

			@Override
			public boolean empty(){
				return tokens.empty();
			}

			@Override
			public Mark mark(){
				int m0 = lineNumber;
				var m1 = tokens.mark();

				return () -> {
					lineNumber = m0;
					m1.reset();
				};
			}
		};
	}

}
