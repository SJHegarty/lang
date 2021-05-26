package majel.stream;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Supplier;

public interface MajelStream<T extends Token> extends Iterable<T>{
	static <S extends Token> MajelStream<S> of(S...elements){
		return new MajelStream<>(){
			int index;

			@Override
			public boolean finite(){
				return true;
			}

			@Override
			public boolean empty(){
				return index < elements.length;
			}

			@Override
			public S next(){
				return elements[index++];
			}
		};
	}

	boolean finite();
	boolean empty();
	T next();

	default Iterator<T> iterator(){
		return new Iterator<T>(){
			@Override
			public boolean hasNext(){
				return !empty();
			}

			@Override
			public T next(){
				return next();
			}
		};
	}

	default <C extends Collection<T>> C collect(Supplier<C> collectionBuilder){
		var rv = collectionBuilder.get();
		for(var t: this){
			rv.add(t);
		}
		return rv;
	}
}
