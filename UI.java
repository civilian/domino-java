import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class UI extends JPanel implements ActionListener, Identificadores {

	BufferedImage img;
	private static final int cantidadFichas = 28;
	FichaDomino tabAr, tabAb;
	byte arr, ab;// arr es el primer numero
	FichaDomino[] fichasJShow, fichasPcShow;
	FichaDomino centro, act;
	ArrayList<FichaDomino> fichasTableroAr, fichasTableroAb;
	ArrayList<Ficha> fichasJ, fichasPc;
	private JButton nuevoJuego;// y otro boton
	boolean agregoArriba;

	ArrayList<Ficha> fichas = new ArrayList<Ficha>(56);

	int anchoFicha = FichaDomino.neg.getIconWidth(),
			altoFicha = FichaDomino.neg.getIconHeight();

	private void dbg(Object... objects) {
		System.out.println(Arrays.deepToString(objects));
	}

	static final int comenzoJuegoPc = 0, comenzoJuegoJ = 1, pc = 2,
			jtablero = 3, jfichas = 4;

	private void centroEscogido() {
		tabAb = new FichaDomino("", -1, -1);
		tabAb.setVisible(true);
		tabAb.addActionListener(this);
		tabAb.setActionCommand("tablero");
		this.add(tabAb);
		fichasTableroAb.add(tabAb);

		tabAr = new FichaDomino("", -1, -1);
		tabAr.setVisible(true);
		tabAr.addActionListener(this);
		tabAr.setActionCommand("tablero");
		this.add(tabAr);
		fichasTableroAr.add(tabAr);
		// TODO: aqui poner el arriba y el abajo
	}

	private void comenzarJuego() {

		// TODO: PONER suerte depende de a quien le
		// for (int i = 0; i < fichasJShow.length; i++) {
		// fichasJShow[i].setNegra(false);// Todas se pueden usar.
		// }

		// generar las fichas
		fichas.clear();
		centro.reset();
		for (byte i = 0; i < 7; i++) {
			for (byte j = i; j < 7; j++) {
				fichas.add(new Ficha(j, i));// primero el grande
				// creo los strings
			}
		}
		// repartir las fichas.
		int ind, size = fichas.size() / 2;
		Ficha p;
		fichasJ = new ArrayList<Ficha>();
		for (int i = 0; i < size; i++) {
			ind = (int) Math.round(Math.random() * (fichas.size() - 1));
			p = fichas.remove(ind);
			fichasJ.add(p);

			fichasJShow[i].cambiaIcono(p.getArr() + "-" + p.getAb() + ".png",
					p.getArr(), p.getAb());
		}
		fichasPc = fichas;
		for (int i = 0; i < fichasPc.size(); i++) {
			p = fichasPc.get(i);
			fichasPcShow[i].cambiaIcono(p.getArr() + "-" + p.getAb() + ".png",
					p.getArr(), p.getAb());
		}

		fichasTableroAb.clear();
		fichasTableroAr.clear();

		nuevoJuego.setEnabled(false);

		boolean turnoPc = esTurnoDePc();
		// boolean turnoPc = true;
		act = centro;

		if (turnoPc) {
			turnoPc(comenzoJuegoPc);
		} else {
			turnoPc(comenzoJuegoJ);
		}

		// fichasTableroAb.get(tabAb).setEnabled(false);
		// toque ahí se queda mientras carga

	}

	private boolean esTurnoDePc() {
		Ficha mula = new Ficha((byte) 6, (byte) 6);
		return fichasPc.contains(mula);
//		return (Math.random() > 0.5);
	}

	int turnoAct;

	private void turnoPc(int turno) {

		turnoAct = turno;
		if (turno == comenzoJuegoPc) {
			JOptionPane.showMessageDialog(this,
					"Ahora es mi turno }:-). Att: El computador.");

			for (int i = 0; i < fichasJShow.length; i++) {
				fichasJShow[i].setEnabled(false);
			}
			centro.setEnabled(false);

			NodoMiniMaxDomino minimax = new NodoMiniMaxDomino(null, fichasPc,
					fichasJ, new Ficha(arr, ab), MAX, null, false);
			minimax.expandir(0, 6);
			// minimax.imprimir();
			Decision deci = minimax.calcularMiniMax(null);
			if (deci == null) {
				JOptionPane
						.showMessageDialog(this,
								"Eres demasiado bueno en esto. Me has ganado. Felicitaciones");
				return;
			}
			FichaDomino o = null;
			for (int i = 0; i < fichasPcShow.length; i++) {
				if (fichasPcShow[i].toFicha().equals(deci.f)) {
					o = fichasPcShow[i];
				}
			}
			o.reset();
			fichasPc.remove(deci.f);

			for (int i = 0; i < fichasJShow.length; i++) {
				if (fichasJShow[i].isNegra())
					continue;
				fichasJShow[i].setEnabled(true);
			}

			String rut = deci.f.getArr() + "-" + deci.f.getAb() + ".png";
			centro.cambiaIcono(rut, deci.f.getArr(), deci.f.getAb());
			arr = deci.f.getArr();
			ab = deci.f.getAb();
			centro.setEnabled(false);
			centro.setVisible(true);
			centroEscogido();

			if (esVictoria(arr, ab, fichasJ)) {
				JOptionPane.showMessageDialog(this,
						"Mua jaja ha ganado el Computador");
				return;
			}// si es victoria ya se que el turno
				// gano.
			turnoPc(jtablero);

		} else if (turno == comenzoJuegoJ) {
			ab = -1;
			agregoArriba = false;// para que lo coloque normal el centro
			turnoPc(jfichas);
		} else if (turno == pc) {
			JOptionPane.showMessageDialog(this,
					"Ahora es mi turno }:-). Att: El computador.");

			for (int i = 0; i < fichasJShow.length; i++) {
				fichasJShow[i].setEnabled(false);
			}
			centro.setEnabled(false);

			NodoMiniMaxDomino minimax = new NodoMiniMaxDomino(null, fichasPc,
					fichasJ, new Ficha(arr, ab), MAX, null, false);
			minimax.expandir(0, 6);
			// minimax.imprimir();
			Decision deci = minimax.calcularMiniMax(null);
			if (deci == null) {
				JOptionPane
						.showMessageDialog(this,
								"Eres demasiado bueno en esto. Me has ganado. Felicitaciones");
				return;
			}

			FichaDomino o = null;
			for (int i = 0; i < fichasPcShow.length; i++) {
				if (fichasPcShow[i].toFicha().equals(deci.f)) {
					o = fichasPcShow[i];
				}
			}
			FichaDomino n;

			if (deci.dir) {// estoy arriba
				act = tabAr;
				escogidoAct(tabAr);
				n = buscarFicha(arr, Ficha.AB, o, deci.dir, fichasPc);
			} else {
				act = tabAb;
				escogidoAct(tabAb);
				n = buscarFicha(ab, Ficha.ARR, o, deci.dir, fichasPc);
			}

			dbg("n", n);
			dbg("act", act);
			dbg("ficha escogida", deci.f);
			dbg("direccion escogida", deci.dir);
			dbg("arr ab", arr, ab);

			act.cambiaIcono(n.getRuta(), n.getArr(), n.getAb());
			o.reset();
			fichasPc.remove(deci.f);
			act.setVisible(true);// los siguientes?

			for (int i = 0; i < fichasJShow.length; i++) {
				if (fichasJShow[i].isNegra())
					continue;
				fichasJShow[i].setEnabled(true);
			}

			if (esVictoria(arr, ab, fichasJ)) {
				JOptionPane.showMessageDialog(this,
						"Mua jaja ha ganado el Computador");
				return;
			}// si es victoria ya se que el turno
				// gano.

			turnoPc(jtablero);
		} else if (turno == jfichas) {
			JOptionPane.showMessageDialog(this,
					"Escoge la ficha con la que quieras jugar");

			for (int i = 0; i < fichasJShow.length; i++) {
				if (fichasJShow[i].isNegra())
					continue;
				fichasJShow[i].setEnabled(true);
			}
			return;
		} else if (turno == jtablero) {
			JOptionPane.showMessageDialog(this,
					"Escoge la ficha del tablero en la que quieras jugar.");

			tabAb.setVisible(true);
			tabAr.setVisible(true);

			for (int i = 0; i < fichasJShow.length; i++) {
				if (fichasJShow[i].isNegra())
					continue;
				fichasJShow[i].setEnabled(false);
			}
		}
	}

	private boolean esVictoria(byte n, byte n1, ArrayList<Ficha> fichas) {
		// TODO Auto-generated method stub
		return !(estaNumeroEn(n, fichas) || estaNumeroEn(n1, fichas));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// por strings es mas facil
		String comando = e.getActionCommand();
		dbg(comando);
		if (comando.startsWith("jugador") && turnoAct == jfichas) {

			FichaDomino o = (FichaDomino) e.getSource();
			FichaDomino n;
			// o.setEnabled(false);
			dbg("agregoArriba", agregoArriba);
			if (agregoArriba) {
				n = buscarFicha(arr, Ficha.AB, o, agregoArriba, fichasJ);
			} else {
				n = buscarFicha(ab, Ficha.ARR, o, agregoArriba, fichasJ);
			}

			if (n == null) {
				JOptionPane.showMessageDialog(this,
						"No puedes jugar con esa ficha.");
				o.setEnabled(true);

				boolean esta = false;
				dbg("agregorArriba", agregoArriba);

				if (agregoArriba) {
					esta = estaNumeroEn(arr, fichasJ);
				} else {
					esta = estaNumeroEn(ab, fichasJ);
				}
				dbg("esta", esta);
				if (!esta) {
					JOptionPane.showMessageDialog(this, "HAS PERDIDO EL JUEGO");

					juegoAcabado();
				} else {
					turnoPc(jfichas);
				}
				return;
			}

			act.cambiaIcono(n.getRuta(), n.getArr(), n.getAb());
			o.reset();
			act.setVisible(true);// los siguientes?

			dbg("arr", arr, "ab", ab);
			dbg("ruta actual", act.getRuta());
			turnoPc(pc);

		} else if (comando.equals("tablero") && turnoAct == jtablero) {
			act = (FichaDomino) e.getSource();
			escogidoAct(act);
			turnoPc(jfichas);

			if (esVictoria(arr, ab, fichasPc)) {
				JOptionPane
						.showMessageDialog(this,
								"Eres demasiado bueno en esto. Me has ganado. Felicitaciones");
			}// si es victoria ya se que el turno
		} else if (comando.equals("nuevoJuego")) {
			comenzarJuego();
		} else if (comando.equals("pc")) {
			JOptionPane.showMessageDialog(this, "Hey no toques mis fichas");
		}

	}

	private void escogidoAct(FichaDomino escogida) {
		// TODO Auto-generated method stub
		dbg(escogida);
		dbg(tabAb);
		dbg(tabAr);
		if (escogida.equals(tabAb)) {
			tabAb = new FichaDomino("", -1, -1);
			tabAb.setVisible(false);
			tabAb.addActionListener(this);
			tabAb.setActionCommand("tablero");
			this.add(tabAb);
			fichasTableroAb.add(tabAb);

			agregoArriba = false;
		} else if (escogida.equals(tabAr)) {
			tabAr = new FichaDomino("", -1, -1);
			tabAr.setVisible(false);
			tabAr.addActionListener(this);
			tabAr.setActionCommand("tablero");
			this.add(tabAr);
			fichasTableroAr.add(tabAr);
			agregoArriba = true;
		}
		escogida.setEnabled(false);
	}

	private void juegoAcabado() {
		// TODO Auto-generated method stub
		nuevoJuego.setEnabled(true);
	}

	private boolean estaNumeroEn(byte n, ArrayList<Ficha> fichas) {
		dbg("n", n);
		dbg("fichas", fichas);
		for (int i = 0; i < fichas.size(); i++) {
			if (n == fichas.get(i).getArr()) {
				return true;
			}
			if (n == fichas.get(i).getAb()) {
				return true;
			}
		}
		return false;
	}

	private FichaDomino buscarFicha(byte seQueda, int donde, FichaDomino ficha,
			boolean agregoArriba, ArrayList<Ficha> deDonde) {
		// metodo(queda, en, pegarleA, ficha)
		Ficha nueva = new Ficha();

		nueva.set(donde, seQueda);
		dbg("ficha para machear", ficha.toString());
		dbg("arr ab", arr, ab);
		dbg("se queda en producto", seQueda);
		dbg("nueva ficha va", nueva.toString());
		if (ficha.getArr() == seQueda) {// utilizo arr
			nueva.set((donde + 1) % 2, ficha.getAb());

			if (agregoArriba) {
				arr = ficha.getAb();
			} else {
				ab = ficha.getAb();
			}

		} else if (ficha.getAb() == seQueda) {// utilizo ab
			nueva.set((donde + 1) % 2, ficha.getArr());

			if (agregoArriba) {
				arr = ficha.getArr();
			} else {
				ab = ficha.getArr();
			}

		} else if (-1 == ab) {
			nueva.setAb(ficha.getAb());
			nueva.setArr(ficha.getArr());
		} else {
			return null;
		}

		String norm = nueva.getArr() + "-" + nueva.getAb();
		String alrevez = nueva.getAb() + "-" + nueva.getArr();
		String r;

		r = norm + ".png";

		int op;
		if (tabAb == null) {
			do {
				op = JOptionPane.showConfirmDialog(this, String.format(
						"Puesto %s? (Si responde \"No\" sera: %s)", norm,
						alrevez));
				if (op == JOptionPane.NO_OPTION) {
					r = alrevez + ".png";
					byte tmp = nueva.getAb();
					nueva.setAb(nueva.getArr());
					nueva.setArr(tmp);
				}
			} while (op == JOptionPane.CANCEL_OPTION);

			arr = nueva.getArr();
			ab = nueva.getAb();
			centroEscogido();
		}

		deDonde.remove(nueva);

		ficha.reset();
		FichaDomino ans = new FichaDomino(r, nueva.getArr(), nueva.getAb());
		// arriba siempre es el primer numer
		return ans;
	}

	private void ordenarFJugadasAb(int index, int xAnt, int yAnt, boolean maxL,
			boolean puestoV, boolean izq) {// si izq false
											// entonces
											// voy a la derecha

		// dbg("fichasTableroAb");
		// dbg(fichasTableroAb.size());
		if (index >= fichasTableroAb.size()) {
			return;
		}

		if (maxL) {
			if (puestoV) {
				xAnt = xAnt + (altoFicha / 2 * ((izq) ? -1 : 0)); // simple solo
																	// haci
				yAnt = yAnt + altoFicha;

				String r = fichasTableroAb.get(index).getAb() + "-"
						+ fichasTableroAb.get(index).getArr() + ".png";
				fichasTableroAb.get(index).cambiaIcono(r,
						fichasTableroAb.get(index).getArr(),
						fichasTableroAb.get(index).getAb());

				// arriba e izq 1
				// vez y der una vez
				// yAnt se queda igual
				fichasTableroAb.get(index).setHorizontal(true);
				fichasTableroAb.get(index).setBounds(xAnt, yAnt, altoFicha,
						anchoFicha);

				maxL = false;
				puestoV = false;
				ordenarFJugadasAb(index + 1, xAnt, yAnt, maxL, puestoV, izq);

				return;
			} else {
				// si maxL
				xAnt = xAnt + (altoFicha / 2 * ((!izq) ? 1 : 0));
				// vez y der una vez
				yAnt = yAnt + anchoFicha;
				fichasTableroAb.get(index).setHorizontal(false);
				fichasTableroAb.get(index).setBounds(xAnt, yAnt, anchoFicha,
						altoFicha);

				maxL = true;// si voy para el otro lado entonces no es maxL
				puestoV = true;// false por que ya lo puse
				ordenarFJugadasAb(index + 1, xAnt, yAnt, maxL, puestoV, !izq);
				return;
			}

		} else {
			xAnt = xAnt + (altoFicha * ((izq) ? -1 : 1)); // simple solo haci
															// arriba e izq 1

			if (izq) {
				String r = fichasTableroAb.get(index).getAb() + "-"
						+ fichasTableroAb.get(index).getArr() + ".png";
				fichasTableroAb.get(index).cambiaIcono(r,
						fichasTableroAb.get(index).getArr(),
						fichasTableroAb.get(index).getAb());
			}
			// vez y der una vez
			// yAnt se queda igual
			fichasTableroAb.get(index).setHorizontal(true);
			fichasTableroAb.get(index).setBounds(xAnt, yAnt, altoFicha,
					anchoFicha);

			maxL = (xAnt <= Math.round(getWidth() * .20));
			maxL = maxL || (xAnt >= Math.round(getWidth() * .75));// 100-15

			ordenarFJugadasAb(index + 1, xAnt, yAnt, maxL, puestoV, izq);
			return;
		}

	}

	private void ordenarFJugadasArr(int index, int xAnt, int yAnt,
			boolean maxL, boolean puestoV, boolean izq) {// si izq false
															// entonces
															// voy a la derecha

		if (index >= fichasTableroAr.size()) {
			return;
		}

		if (maxL) {
			if (puestoV) {

				// dbg(izq);
				xAnt = xAnt + (altoFicha / 2 * ((izq) ? -1 : 0)); // simple solo
																	// haci
				yAnt = yAnt - altoFicha / 2;

				String r = fichasTableroAr.get(index).getAb() + "-"
						+ fichasTableroAr.get(index).getArr() + ".png";
				fichasTableroAr.get(index).cambiaIcono(r,
						fichasTableroAr.get(index).getArr(),
						fichasTableroAr.get(index).getAb());
				// arriba e izq 1
				// vez y der una vez
				// yAnt se queda igual
				fichasTableroAr.get(index).setHorizontal(true);
				fichasTableroAr.get(index).setBounds(xAnt, yAnt, altoFicha,
						anchoFicha);

				maxL = false;
				puestoV = false;
				ordenarFJugadasArr(index + 1, xAnt, yAnt, maxL, puestoV, izq);
				return;
			} else {
				// si maxL
				// dbg(izq);
				xAnt = xAnt + (altoFicha / 2 * ((!izq) ? 1 : 0));
				// vez y der una vez
				yAnt = yAnt - altoFicha;
				fichasTableroAr.get(index).setHorizontal(false);
				fichasTableroAr.get(index).setBounds(xAnt, yAnt, anchoFicha,
						altoFicha);

				maxL = true;// si voy para el otro lado entonces no es maxL
				puestoV = true;// false por que ya lo puse
				ordenarFJugadasArr(index + 1, xAnt, yAnt, maxL, puestoV, !izq);
				return;
			}

		} else {
			xAnt = xAnt + (altoFicha * ((izq) ? -1 : 1)); // simple solo haci
															// arriba e izq 1

			if (!izq) {
				String r = fichasTableroAr.get(index).getAb() + "-"
						+ fichasTableroAr.get(index).getArr() + ".png";
				fichasTableroAr.get(index).cambiaIcono(r,
						fichasTableroAr.get(index).getArr(),
						fichasTableroAr.get(index).getAb());
			}

			// vez y der una vez
			// yAnt se queda igual
			fichasTableroAr.get(index).setHorizontal(true);
			fichasTableroAr.get(index).setBounds(xAnt, yAnt, altoFicha,
					anchoFicha);

			maxL = (xAnt <= Math.round(getWidth() * .20));
			maxL = maxL || (xAnt >= Math.round(getWidth() * .75));// 100-15

			ordenarFJugadasArr(index + 1, xAnt, yAnt, maxL, puestoV, izq);
			return;
		}

	}

	private void ordenarFichas() {
		int ancho = getWidth(), alto = getHeight(), x, y, mitad;

		// dbg(ancho, alto);

		mitad = ancho / 2 - fichasJShow.length / 2 * (anchoFicha + 4);
		for (int i = 0; i < fichasJShow.length; i++) {

			x = mitad + i * (anchoFicha + 4);
			y = alto - altoFicha - 2;

			fichasJShow[i].setBounds(x, y, anchoFicha, altoFicha);

			fichasPcShow[i].setBounds(x, 2, anchoFicha, altoFicha);
		}
		centro.setHorizontal(true);
		int xCentr = ancho / 2 - altoFicha / 2;
		int yCentro = alto / 2 - anchoFicha / 2;
		centro.setBounds(xCentr, yCentro, altoFicha, anchoFicha);

		ordenarFJugadasArr(0, xCentr, yCentro, false, false, true);
		ordenarFJugadasAb(0, xCentr, yCentro, false, false, false);

		Dimension d = nuevoJuego.getPreferredSize();
		nuevoJuego.setBounds(mitad - d.width - 5,
				(int) (alto - Math.round(alto * .1)), d.width, d.height);
		// dbg(cantidadFichas);
	}

	public void paint(Graphics g) {
		// simply draw the buffered image
		g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
		ordenarFichas();
		for (int i = 0; i < fichasJShow.length; i++) {
			// if (fichasJShow[i].isRotado()) {
			// fichasJShow[i].setHorizontal(true);
			// }
			// if (fichasPcShow[i].isRotado()) {
			// fichasPcShow[i].setHorizontal(true);
			// }
			fichasJShow[i].repaint();
			fichasPcShow[i].repaint();
		}
		centro.repaint();
		nuevoJuego.repaint();
		if (tabAb != null)
			tabAb.repaint();
		if (tabAr != null)
			tabAr.repaint();

		for (int i = 0; i < fichasTableroAr.size(); i++) {
			fichasTableroAr.get(i).repaint();
		}
		for (int i = 0; i < fichasTableroAb.size(); i++) {
			fichasTableroAb.get(i).repaint();
		}
	}

	// overrides the method in Component class, to determine the window size
	public Dimension getPreferredSize() {
		if (img == null) {
			return new Dimension(100, 100);
		} else {
			// make sure the window is not two small to be seen
			return new Dimension(Math.max(100, img.getWidth(null)), Math.max(
					100, img.getHeight(null)));// poner que no pase de un minmo
		}
	}

	public UI(int a) {
		try {
			setLayout(null);

			String rutaTablero = "img/MesaJuego.png";
			img = ImageIO.read(new File(rutaTablero));

			fichasJShow = new FichaDomino[cantidadFichas / 2];
			fichasPcShow = new FichaDomino[cantidadFichas / 2];
			// dbg(this.getPreferredSize());
			for (int i = 0; i < fichasJShow.length; i++) {
				fichasJShow[i] = new FichaDomino();
				fichasPcShow[i] = new FichaDomino();

				fichasJShow[i].addActionListener(this);
				fichasJShow[i].setActionCommand("jugador");
				// fichasPcShow[i].setEnabled(false);
				fichasPcShow[i].addActionListener(this);
				fichasPcShow[i].setActionCommand("pc");
				this.add(fichasJShow[i]);
				this.add(fichasPcShow[i]);
			}
			tabAb = tabAr = null;

			fichasTableroAr = new ArrayList<FichaDomino>(cantidadFichas);
			fichasTableroAb = new ArrayList<FichaDomino>(cantidadFichas);
			// }//TODO: LAS AÑADO Y LES DOY SET VISIBLE EN EL EVENTO
			// for (int i = 0; i < 23; i++) {
			// fichasTableroAr.add(new FichaDomino());
			// this.add(fichasTableroAr.get(i));
			// // fichasTableroAb.add(new FichaDomino());
			// // this.add(fichasTableroAb.get(i));
			// }

			centro = new FichaDomino();
			centro.setHorizontal(true);
			centro.setVisible(false);
			centro.addActionListener(this);
			centro.setActionCommand("tablero");
			this.add(centro);

			nuevoJuego = new JButton("Nuevo Juego");
			nuevoJuego.addActionListener(this);
			nuevoJuego.setActionCommand("nuevoJuego");
			this.add(nuevoJuego);

			ordenarFichas();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new UI(1));
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setResizable(false);
		frame.setVisible(true);

	}
}