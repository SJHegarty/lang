package majel.lang.descent.structure.indent2;

import majel.stream.StringToken;

import java.util.List;

public record SimpleTree(Line line, List<FooToken> children) implements FooToken{
	public SimpleTree{
		if(children.isEmpty()){
			throw new IllegalStateException();
		}
	}

	@Override
	public int depth(){
		return line.depth();
	}

	@Override
	public List<StringToken> headTokens(){
		return line.headTokens();
	}
}
