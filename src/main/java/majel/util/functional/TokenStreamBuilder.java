package majel.util.functional;

import majel.lang.util.Mark;
import majel.lang.util.TokenStream$Char;
import majel.util.MathUtils;

public class TokenStreamBuilder implements CharGobbler, TokenStream$Char{

	record Bounds(int head, int tail){
		Bounds{
			if(head > tail){
				throw new IllegalStateException();
			}
		}
		Bounds shiftHead(int delta){
			return withHead(head + delta);
		}
		Bounds withHead(int head){
			return new Bounds(head, tail());
		}

		Bounds shiftTail(int delta){
			return withTail(tail + delta);
		}
		Bounds withTail(int tail){
			return new Bounds(head(), tail);
		}
		int unwrapIndex(int offsetIndex){
			return head + offsetIndex;
		}
		int size(){
			return tail - head;
		}
	}
	private Bounds bounds = new Bounds(0, 0);
	private char[] buffer = new char[1 << 3];

	@Override
	public CharGobbler feed(char c){
		final int tail = bounds.tail();;
		if(buffer.length == tail){
			ensureCapacity(tail << 1);
		}
		shadowFeed(c);
		return this;
	}

	private void ensureCapacity(int minSize){
		final int tsize = bounds.tail();
		if(tsize < minSize){
			final char[] buffernew = new char[MathUtils.nextPowerOfTwo(minSize)];
			for(int i = 0; i < tsize; i++){
				buffernew[i] = buffer[i];
			}
			buffer = buffernew;
		}
	}

	@Override
	public CharGobbler feed(char[] chars){
		ensureCapacity(bounds.tail() + chars.length);
		for(char c: chars){
			shadowFeed(c);
		}
		return this;
	}
	private void shadowFeed(char c){
		buffer[bounds.tail()] = c;
		bounds = bounds.shiftTail(1);
	}

	@Override
	public char peek(){
		return buffer[bounds.head()];
	}

	@Override
	public char poll(){
		char rv = peek();
		bounds = bounds.shiftHead(1);
		return rv;
	}

	@Override
	public boolean empty(){
		return bounds.size() == 0;
	}

	@Override
	public Mark mark(){
		final int head = bounds.head();
		return () -> bounds = bounds.withHead(head);
	}

	public TokenStream$Char immutableView(){
		return new TokenStream$Char(){
			@Override
			public char peek(){
				return TokenStreamBuilder.this.peek();
			}

			@Override
			public char poll(){
				return TokenStreamBuilder.this.poll();
			}

			@Override
			public boolean empty(){
				return TokenStreamBuilder.this.empty();
			}

			@Override
			public Mark mark(){
				return TokenStreamBuilder.this.mark();
			}
		};
	}
}
