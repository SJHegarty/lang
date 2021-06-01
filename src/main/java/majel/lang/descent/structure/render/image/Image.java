package majel.lang.descent.structure.render.image;

import majel.lang.descent.structure.Delta;
import majel.lang.util.TokenStream;
import majel.stream.Token;

import java.util.List;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

public interface Image extends Token{
	default Image scale(int factor){
		return new Image(){
			@Override
			public int x0(){
				return Image.this.x0() * factor;
			}

			@Override
			public int y0(){
				return Image.this.y0() * factor;
			}

			@Override
			public int xn(){
				return Image.this.xn() * factor;
			}

			@Override
			public int yn(){
				return Image.this.yn() * factor;
			}

			@Override
			public int width(){
				return Image.this.width() * factor;
			}

			@Override
			public int height(){
				return Image.this.height() * factor;
			}

			@Override
			public int colourAt(int x, int y){
				return Image.this.colourAt(x/factor, y/factor);
			}

			@Override
			public boolean contains(int x, int y){
				return Image.this.contains(x/factor, y/factor);
			}
		};
	}

	interface DimImage extends Image{
		default int xn(){
			return x0() + width() - 1;
		}
		default int yn(){
			return y0() + height() - 1;
		}
	}
	record BlockImage(int x0, int y0, int width, int height, int colour) implements DimImage{
		@Override
		public int colourAt(int x, int y){
			return colour;
		}

		@Override
		public boolean contains(int x, int y){
			return x >= x0() && x <= xn() && y >= y0() && y <= yn();
		}
	}

	default Image offset(int dx, int dy){
		return offset(new Delta(dx, dy));
	}

	default Image offset(Delta delta){
		return new OffsetImage(this, delta);
	}
	int x0();
	int y0();
	int xn();
	int yn();
	int width();
	int height();
	int colourAt(int x, int y);
	boolean contains(int x, int y);

	record OffsetImage(Image wrapped, Delta delta) implements Image{

		@Override
		public int x0(){
			return wrapped.x0() + delta.dx();
		}

		@Override
		public int y0(){
			return wrapped.y0() + delta.dy();
		}

		@Override
		public int xn(){
			return wrapped.xn() + delta.dx();
		}

		@Override
		public int yn(){
			return wrapped.yn() + delta.dy();
		}

		@Override
		public int width(){
			return wrapped.width();
		}

		@Override
		public int height(){
			return wrapped.height();
		}

		@Override
		public int colourAt(int x, int y){
			return wrapped.colourAt(x - delta.dx(), y - delta.dy());
		}

		@Override
		public boolean contains(int x, int y){
			return wrapped().contains(x - delta.dx(), y - delta.dy());
		}
	}

	record CompositeImage(List<Image> images) implements Image{
		private int v(
			ToIntFunction<Image> dimension,
			IntBinaryOperator op,
			int identity
		){
			int v = identity;
			for(var i: images){
				v = op.applyAsInt(v, dimension.applyAsInt(i));
			}
			return v;
		}

		private int vmin(ToIntFunction<Image> dimension){
			return v(dimension, Math::min, Integer.MAX_VALUE);
		}

		private int vmax(ToIntFunction<Image> dimension){
			return v(dimension, Math::max, Integer.MIN_VALUE);
		}

		@Override
		public int x0(){
			return vmin(Image::x0);
		}

		@Override
		public int y0(){
			return vmin(Image::y0);
		}

		@Override
		public int xn(){
			return vmax(Image::xn);
		}

		@Override
		public int yn(){
			return vmax(Image::yn);
		}

		@Override
		public int width(){
			return xn() + 1 - x0();
		}

		@Override
		public int height(){
			return yn() + 1 - y0();
		}

		@Override
		public int colourAt(int x, int y){
			for(var i:images){
				if(i.contains(x, y)){
					return i.colourAt(x, y);
				}
			}
			return 0;
		}

		@Override
		public boolean contains(int x, int y){
			for(var i:images){
				if(i.contains(x, y)){
					return true;
				}
			}
			return false;
		}
	}
	default Image reduce(TokenStream<Image> images){
		throw new UnsupportedOperationException();
	}
}
