package majel.lang.descent.structure;

public record Delta(int dx, int dy){
	Delta delta(int dx, int dy){
		return new Delta(this.dx + dx, this.dy + dy);
	}
	Delta delta(Delta delta){
		return new Delta(this.dx + delta.dx, this.dy + delta.dy);
	}
}
