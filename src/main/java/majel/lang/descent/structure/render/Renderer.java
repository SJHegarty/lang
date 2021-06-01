package majel.lang.descent.structure.render;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.AbstractSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class Renderer extends JPanel{

	@FunctionalInterface
	interface ColourWheel{

		default int[] range(int size){
			final double thetamul = 2d * Math.PI / size;
			return IntStream
				.range(0, size)
				.map(i -> toRGB(thetamul * i))
				.toArray();
		}
		default int toRGB(double theta){
			return toRGB((float)theta);
		}

		int toRGB(float theta);
	}
	static class Colour{
		static int toInt(int r, int g, int b){
			return toInt(0xff, r, g, b);
		}
		static int toInt(int a, int r, int g, int b){
			return (a << 0x18)|(r << 0x10)|(g << 0x08)|b;
		}
		static int toGrey(int c){
			int r = 0xff & (c >> 0x10);
			int g = 0xff & (c >> 0x08);
			int b = 0xff & c;
			int s = (r + g + b + 1) / 3;
			return toInt(c >> 0x18, s, s, s);
		}
	}
	public static final ColourWheel FUN_HOUSE = new ColourWheel(){
		final double ONE_THIRD = 2d * Math.PI / 3d;
		final double TWO_THIRD = 4d * Math.PI / 3d;
		@Override
		public int toRGB(float theta){
			int r = (int)(127.5 * (1d + Math.sin(theta)));
			int g = (int)(127.5 * (1d + Math.sin(theta + ONE_THIRD)));
			int b = (int)(127.5 * (1d + Math.sin(theta + TWO_THIRD)));
			return Colour.toInt(r, g, b);
		}
	};
	public static void main(String...args){
	final JFrame frame = new JFrame();
		var pane = new JTextPane();
		Runnable r = () -> {

		};
		pane.addKeyListener(new KeyListener(){
			@Override
			public void keyTyped(KeyEvent e){
				r.run();
			}

			@Override
			public void keyPressed(KeyEvent e){
			}

			@Override
			public void keyReleased(KeyEvent e){
			}
		});
		frame.getContentPane().add(new JScrollPane(pane));
		pane.setBackground(new Color(0xff202020));
		final var doc = pane.getStyledDocument();
		AtomicBoolean needsRefresh = new AtomicBoolean();
		new Thread(() -> {
			for(;;){
				try{
					Thread.sleep(40);
					if(needsRefresh.get()){
						try{
							var text = doc.getText(0, doc.getLength());
							int[] colours = FUN_HOUSE.range(text.length());
							var sc = StyleContext.getDefaultStyleContext();
							IntFunction<AttributeSet> attributes = index -> {
								return sc.addAttribute(
									SimpleAttributeSet.EMPTY,
									StyleConstants.Foreground,
									new Color(colours[index])
								);
							};
							for(int i = 0; i < colours.length; i++){
								doc.setCharacterAttributes(i, 1, attributes.apply(i), true);
							}

						}
						catch(BadLocationException e){
							//throw new IllegalStateException(e);
						}
					}
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}).start();
		final Runnable refresh = () -> {
			SwingUtilities.invokeLater(() -> {
				needsRefresh.set(true);
			});
		};
		doc.addDocumentListener(
			new DocumentListener(){
				@Override
				public void insertUpdate(DocumentEvent e){
					refresh.run();
				}

				@Override
				public void removeUpdate(DocumentEvent e){
					refresh.run();
				}

				@Override
				public void changedUpdate(DocumentEvent e){
					refresh.run();
				}
			}
		);
		System.err.println(pane.getStyledDocument());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private int colour;
	Renderer(){
		new Thread(() -> {
			final int loopsize = 0x100;
			final int[] colours = FUN_HOUSE.range(loopsize);

			int loopIndex = 0;
			for(;;){
				colour = colours[loopIndex & (loopsize - 1)];
				loopIndex += 1;
				repaint();
				try{
					Thread.sleep(10);
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}).start();
	}
	private transient BufferedImage buffer;

	public void paint(Graphics g){
		g.setColor(new Color(colour));
		g.fillRect(0, 0, getWidth(), getHeight());
	}

}
