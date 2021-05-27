package majel.lang.descent.structure.indent;

public record IndentHidden(IndentToken wrapped) implements IndentToken{

	@Override
	public void reconstitute(StringBuilder builder, int depth){
		wrapped.reconstitute(builder, depth + 1);
	}
}
