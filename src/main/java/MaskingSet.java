import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;

public class MaskingSet<T> implements Set<T> {

	private Set<T> wrapped;
	private final Supplier<Set<T>> builder;

	public MaskingSet(Supplier<Set<T>> builder) {
		this.builder = builder;
	}

	private Set<T> current(){
		return (wrapped == null) ? Set.of() : wrapped;
	}
	@Override
	public int size() {
		return current().size();
	}

	@Override
	public boolean isEmpty() {
		return current().isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return current().contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return current().iterator();
	}

	@Override
	public Object[] toArray() {
		return current().toArray();
	}

	@Override
	public <T1> T1[] toArray(T1[] a) {
		return current().toArray(a);
	}

	@Override
	public boolean add(T t) {
		if (wrapped == null){
			wrapped = builder.get();
		}
		return wrapped.add(t);
	}

	@Override
	public boolean remove(Object o) {
		return current().remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return current().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		if(wrapped == null && c.size() != 0){
			wrapped = builder.get();
		}
		return current().addAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return current().retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return current().removeAll(c);
	}

	@Override
	public void clear() {
		current().clear();
	}
}
