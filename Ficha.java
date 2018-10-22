import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;

public class Ficha {

	public byte[] marcas = new byte[2];// pasarlo a bitset?
	static final int ARR = 0, AB = 1;

	public byte get(int i) {
		return marcas[i];
	}

	public byte getArr() {
		return marcas[ARR];
	}

	public byte getAb() {
		return marcas[AB];
	}

	public void setArr(byte val) {
		marcas[ARR] = val;
	}

	public void setAb(byte val) {
		marcas[AB] = val;
	}

	public void set(int i, int val) {
		marcas[i] = (byte) val;
	}

	public Ficha(byte[] marcas) {
		this.marcas = marcas;
	}

	public Ficha() {
	}

	public Ficha(byte arr, byte ab) {
		marcas[ARR] = arr;
		marcas[AB] = ab;
	}

	@Override
	public String toString() {
		return "[" + marcas[ARR] + "-" + marcas[AB] + "]";
	}

	@Override
	public boolean equals(Object obj) {
		Ficha other = (Ficha) obj;
		Ficha otherB = new Ficha(other.getAb(), other.getArr());

		return (equalsA(other) || equalsA(otherB));
	}

	public boolean equalsA(Object obj) {
		Ficha other = (Ficha) obj;
		if (!Arrays.equals(marcas, other.marcas))
			return false;
		return true;
	}

}
