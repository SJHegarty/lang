package majel.lang.descent.structure;

import majel.stream.Token;

record Line(int indent, String content, boolean terminated) implements Token{

	public boolean empty(){
		return content.length() == 0;
	}
}
