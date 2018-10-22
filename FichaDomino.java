import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class FichaDomino extends JButton {
	private String ruta;
	private Icon cuad;
	private boolean rotado = false;
	private boolean negra = true;
	private byte arr, ab;
	private static String rutaIm = "img/";
	public static final Icon neg = new ImageIcon("img/cuadro.png");

	public byte getArr() {
		return arr;
	}

	public void setArr(byte arr) {
		this.arr = arr;
	}

	public byte getAb() {
		return ab;
	}

	public void setAb(byte ab) {
		this.ab = ab;
	}

	public String getRuta() {
		return ruta;
	}

	public boolean isRotado() {
		return rotado;
	}

	public boolean isNegra() {
		return negra;
	}

	public void setNegra(boolean negra) {
		this.negra = negra;
	}

	public void setHorizontal(boolean rot) {
		// rotar = rot;
		if (rot && !rotado) {
			ruta = "R" + ruta;
			rotado = true;
		} else if (!rot) {
			if (ruta.startsWith("R")) {
				ruta = ruta.substring(1);
				rotado = false;
			}
		} else if (rot && rotado && !ruta.startsWith("R")) {
			rotado = false;
			setHorizontal(rot);
		}
		// dbg(ruta);
		cambiaIcono(ruta, arr, ab);
	}

	private void dbg(Object... o) {
		System.out.println(Arrays.deepToString(o));
	}

	public void cambiaIcono(String rut, int arrIn, int abIn) {
		arr = (byte) arrIn;
		ab = (byte) abIn;
		ruta = rut;
		cuad = new ImageIcon(rutaIm + rut);
		this.setIcon(cuad);
		negra = false;
	}

	public void reset() {
		this.setIcon(neg);
		negra = true;
	}

	// public void paint(Graphics g) {
	// // simply draw the buffered image
	// if (!rotado) {
	// AffineTransform at = new AffineTransform();
	// at.rotate(90 * Math.PI / 180);
	// ((Graphics2D) g).setTransform(at);
	// super.paint(g);
	// } else {
	// super.paint(g);
	// }
	// }

	public int getPreferredWidth() {
		return getPreferredSize().width;
	}

	public int getPreferredHeight() {
		return getPreferredSize().height;
	}

	public Dimension getPreferredSize() {
		if (neg == null) {
			return new Dimension(100, 100);
		} else {
			// make sure the window is not two small to be seen
			return new Dimension(neg.getIconWidth(), neg.getIconHeight());
		}
	}

	public FichaDomino() {
		super();
		ruta = "cuadro.png";
		this.setIcon(neg);
	}

	public FichaDomino(String rutaFot, int arrIn, int abIn) {
		super();
		arr = (byte) arrIn;
		ab = (byte) abIn;
		ruta = rutaFot;
		cuad = new ImageIcon(rutaIm + rutaFot);
		this.setIcon(cuad);
		negra = false;
	}

	@Override
	public String toString() {
		return "FichaDomino [ruta=" + ruta + ", rotado=" + rotado + ", arr="
				+ arr + ", ab=" + ab + "]";
	}

	public Ficha toFicha() {
		return new Ficha(getArr(), getAb());
	}

	// public FichaDomino(String rutaFot, String mensaje) {
	// super();
	// ruta = rutaFot;
	// cuad = new ImageIcon(rutaIm + rutaFot);
	// this.setIcon(cuad);
	// this.setText(mensaje);
	// negra = false;
	// }

}