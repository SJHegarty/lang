package majel.lang.descent;

public interface Expression<T>{
	String reconstitute();
	T build(RecursiveDescentBuildContext<T> context);
}
