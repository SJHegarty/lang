package majel.lang.descent.structure;

public enum Brackets{
	ANGLED{
		@Override
		char opening(){
			return '<';
		}

		@Override
		char closing(){
			return '>';
		}
	},
	CURLY{
		@Override
		char opening(){
			return '{';
		}

		@Override
		char closing(){
			return '}';
		}
	},
	ROUND{
		@Override
		char opening(){
			return '(';
		}

		@Override
		char closing(){
			return ')';
		}
	},
	SQUARE{
		@Override
		char opening(){
			return '[';
		}

		@Override
		char closing(){
			return ']';
		}
	};
	abstract char opening();
	abstract char closing();


}
