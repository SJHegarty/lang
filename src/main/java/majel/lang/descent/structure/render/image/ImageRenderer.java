package majel.lang.descent.structure.render.image;

import majel.lang.util.Pipe;
import majel.lang.util.TokenStream;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;

public class ImageRenderer implements Pipe<Image, RenderResult>{
	private final BufferedImage content;
	private final Function<Image, RenderResult> renderer;

	public ImageRenderer(){
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
