package majel.util;

public class MathUtils{
	public static int nextPowerOfTwo(int v){
		if((v & (v - 1)) != 0){
			v |= v >> 0x01;
			v |= v >> 0x02;
			v |= v >> 0x04;
			v |= v >> 0x08;
			v |= v >> 0x10;
			v += 1;
		}
		return v;
	}

}
