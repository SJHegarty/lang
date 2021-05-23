package majel.lang.err;

public class ParseException extends RuntimeException{
	ParseException(){
	}

	ParseException(String message){
		super(message);
	}
}
