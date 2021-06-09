package majel.lang.descent.structure.indent2;

import java.util.List;

record SimpleTree(Line line, List<FooToken> children) implements FooToken{
	SimpleTree{
		if(children.isEmpty()){
			throw new IllegalStateException();
		}
	}

	@Override
	public int depth(){
		return line.depth();
	}
}
