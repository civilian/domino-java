import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public class NodoMiniMaxDomino implements Identificadores {

	// Todos los nodos tienen un tipo para ver si la jugada es MIN = del jugador
	// contrario o MAX= del pc
	private int tipo = MAX; // max=0 min=1 aleatorio=2;

	// Para calcular que tan buena o que tan mala es una opcion de los
	// ecsenarios posibles
	private double productividad = 0.0;

	// Los nodos hijos tienen referencia de su padre esto es solamente para
	// debugear el codigo ya que en la logica no se necesita
	private NodoMiniMaxDomino padre = null;

	// Todo nodo minimax tiene un conjunto de hijos de su mismo tipo
	private ArrayList<NodoMiniMaxDomino> hijos = new ArrayList<NodoMiniMaxDomino>();

	// Estos son los arreglos de las fichas restantes para el jugador humano y
	// para la maquina
	public ArrayList<Ficha> fichasPc, fichasJ;

	// Esta es una ficha inicial que representa los dos lados por los cuales se
	// puede jugar el juego que esta en la mesa,
	// Si las fichas estuvieran en la mesa así:
	// |1 | 2|..| 2| 3|..|3 | 5| esta ficha sería |1 | 5| ya que son los numeros
	// en los cuales puedo poner mi jugada
	private Ficha F;

	// Esta es la ficha que se utiliza para pasar de el esenario que representa
	// el padre a el el escenario que representa el hijo por ejemplo si el
	// escenario que representa el padre es:
	// El el tablero estan: |1 | 2|..| 2| 3|..|3 | 5|
	// pc tiene |1 | 1|..|2 | 2|..|3 | 3|
	// j tienen |5 | 5|..|0 | 0|..|6 | 6|
	// Y el hijo es:
	// El el tablero estan: |1 | 1|..|1 | 2|..| 2| 3|..|3 | 5|
	// pc tiene |2 | 2|..|3 | 3|
	// j tienen |5 | 5|..|0 | 0|..|6 | 6|
	// Este sera |1 | 1|
	private Ficha usadaLLegar;

	// Esta es la posicion en la que se puso la ultima ficha para llegar a este
	// esenario en el caso de arriba si:
	// con : |1 | 2|..| 2| 3|..|3 | 5|
	// 1 fuera arriba y 5 fuera abajo en el hijo del ejemplo este sería true;
	private boolean arriba;

	// constructor normal solo le entran los parametros necesarios
	public NodoMiniMaxDomino(NodoMiniMaxDomino padrein, ArrayList<Ficha> pcIn,
			ArrayList<Ficha> jIn, Ficha FIn, int tipoin, Ficha usad,
			boolean arri) {
		F = FIn;
		fichasPc = pcIn;
		fichasJ = jIn;
		padre = padrein;
		tipo = tipoin;
		usadaLLegar = usad;
		arriba = arri;
	}

	// Voy a crear todas las posibles situaciones de juego a partir de la
	// situacion inicial que es este mismo nodo todo esto hasta la profundidad
	// maxProf
	public boolean expandir(int prof, int maxProf) {
		Ficha tmp;

		if (getTipo() == MAX && (prof >= maxProf)) {// Si el tipo de este
			tipo = HOJA;// si es el max es hoja
			return true;
		}

		// Si el tipo del nodo es max el proximo es min y vicebersa
		int tipoN = (tipo == MAX) ? MIN : MAX;

		ArrayList<Ficha> fichasActual;
		// Con que fichas voy a jugar, si es MAX con fichasPc
		fichasActual = (tipo == MAX) ? fichasPc : fichasJ;

		// Saco cada una de las fichas
		for (int i = 0; i < fichasActual.size(); i++) {
			tmp = fichasActual.get(i);
			for (int direcAct = 0; direcAct < 2; direcAct++) {
				// Miro si la ficha actual con la que se puede jugar en su parte
				// de arriba es igual a
				// la parte de arriba o de abajo de cada una de las fichas
				if (F.getArr() == tmp.get(direcAct)) {
					Ficha nueva = new Ficha();
					nueva.setArr(tmp.get((direcAct + 1) % 2));
					nueva.setAb(F.getAb());

					// En caso de que sea igual creo un nuevo nodo modificando
					// la ficha actual
					NodoMiniMaxDomino a = crearNodo(fichasPc, fichasJ, i,
							nueva, tipoN, tmp, true);
					hijos.add(a);
				}
				// Lo mismo que arriba pero con la parte de abajo de la ficha
				// actual
				if (F.getAb() == tmp.get(direcAct)) {
					Ficha nueva = new Ficha();
					nueva.setArr(F.getArr());
					nueva.setAb(tmp.get((direcAct + 1) % 2));// abajo lo mismo
					NodoMiniMaxDomino a = crearNodo(fichasPc, fichasJ, i,
							nueva, tipoN, tmp, false);
					hijos.add(a);
				}
			}
		}

		if (hijos.size() == 0) {// No puedo generar mas jugadas
			// Si el tipo es max osea la jugada del pc es porque ya ha perdido
			// el pc
			if (getTipo() == MAX) {
				tipo = HOJA;
				productividad = -10000.0;
			}

			// Si el tipo es min osea la jugada es del jugador humano y no tiene
			// jugadas ya gane
			if (getTipo() == MIN) {
				tipo = HOJA;
				productividad = 10000.0;// casi inf porque el jugador se quedo
										// sin
										// juego
			}
		}
		// Expando recursivamente
		for (Iterator<NodoMiniMaxDomino> it = hijos.iterator(); it.hasNext();) {
			NodoMiniMaxDomino ob = it.next();
			ob.expandir(prof + 1, maxProf);
		}
		return true;
	}

	// Funcion auxiliar para simplificar el echo de crear un nodo para una nueva
	// situación
	public NodoMiniMaxDomino crearNodo(ArrayList<Ficha> pc, ArrayList<Ficha> j,
			int idxIgual, Ficha nueva, int tipoN, Ficha usad, boolean arr) {

		// Saco una copia de las dos listas de fichas
		ArrayList<Ficha> pcCop = (ArrayList<Ficha>) pc.clone();
		ArrayList<Ficha> jCop = (ArrayList<Ficha>) j.clone();

		// Dependiendo del tipo de este nodo remuevo ya sea de las fichas del
		// MAX que es el pc o del jugador
		if (tipo == MAX) {
			pcCop.remove(idxIgual);
		} else {
			jCop.remove(idxIgual);
		}

		return new NodoMiniMaxDomino(this, pcCop, jCop, nueva, tipoN, usad, arr);
	}

	// Aceso variables
	public NodoMiniMaxDomino getPadre() {
		return padre;
	}

	// Aceso variables
	public int getTipo() {
		return tipo;
	}

	// Aceso variables
	public ArrayList<NodoMiniMaxDomino> getHijos() {
		return hijos;
	}

	// Voy a calcular la mejor opcion que puedo tomar
	public Decision calcularMiniMax(NodoMiniMaxDomino arbol) {// calculo
																// la
																// productividad
																// de
																// los
																// hijos
																// me
																// quedo
																// con
																// el
																// que
																// genere
																// la
																// mas
																// grande
		int idxMax = -1;
		double maxVal = -10000000;// voy a encontrar la maxima productividad
									// posible
		double tmp;

		if (hijos.size() == 0) {// Si no tiene hijos ya perdi
			return null;
		}
		for (int i = 0; i < hijos.size(); i++) {
			dbg("productivadad", i, calcularProductividad(hijos.get(i)));

			// Calcula cada productividad
			tmp = calcularProductividad(hijos.get(i));
			// miro si es mayor que la anterior y entonces me quedo con la mas
			// grande y su indice
			if (maxVal < tmp) {
				maxVal = tmp;
				idxMax = i;
			}
		}

		// El actual depende de si izquierda o derecha asi lo cojo
		//
		// System.out.println(productiv[0]+"pro1"+productiv[1]);

		Ficha fTmp;
		// con el indice saco la ficha usada
		Ficha fUsada = hijos.get(idxMax).usadaLLegar;

		// Necisto arriba o abajo
		dbg("fUsada", fUsada, "fichas hijo", hijos.get(idxMax).fichasPc);
		dbg("fichas yo", fichasPc);
		dbg("F", F, "hijos.F", hijos.get(idxMax).F);
		dbg("productivadad", maxVal);
		System.out.println(fUsada.toString());

		// Y como la puse
		boolean puestoArriba = hijos.get(idxMax).arriba;

		// Y eso lo devuelvo como desicion
		Decision ans = new Decision(fUsada, puestoArriba);
		return ans;
	}

	// Busco un numero en un arreglo de fichas
	private boolean estaNumeroEn(byte n, ArrayList<Ficha> fichas) {
		for (int i = 0; i < fichas.size(); i++) {
			// Si esta arriba o esta abajo
			if (n == fichas.get(i).getArr()) {
				return true;
			}
			if (n == fichas.get(i).getAb()) {
				return true;
			}
		}
		return false;
	}

	private boolean esVictoria(byte n, byte n1, ArrayList<Ficha> fichas) {
		return !(estaNumeroEn(n, fichas) || estaNumeroEn(n1, fichas));
	}

	// Cuento cuantas veces esta un nuero en un arreglo de fichas
	private double cuantasAnulan(byte f, ArrayList<Ficha> fichas) {
		double ans = 0;
		for (int i = 0; i < fichas.size(); i++) {
			Ficha act = fichas.get(i);
			// Si es igual al de arribo o de abajo cuento ++
			if (act.getArr() == f) {
				ans++;
			}
			if (act.getAb() == f) {
				ans++;
			}
		}
		return ans;
	}

	// Productividad de cada Nodo que tan bueno o que tan malo es cada nodo
	public double calcularProductividad(NodoMiniMaxDomino nodo) {
		// Si es hoja osea la profundidad del arbol es maxima las hojas aqui son
		// MAX,
		if (nodo.getTipo() == HOJA) {
			// dbg("estoy en hoja");
			// dbg(productividad);

			// Estos casos especiales se trataron por simpleza en expandir se
			// trata de los casos en donde no se pueden generar mas hijos
			if (productividad == 10000.0) {
				dbg(" JAJAJAJAJAJ  BIEN");
				return productividad;
			} else if (productividad == -10000.0) {
				dbg(" BUUU  MAL");
				return productividad;
			}

			// if ((cuantasAnulan(F.getArr(), fichasJ) == 0)
			// && (cuantasAnulan(F.getArr(), fichasJ) == 0)) {
			// productividad = -10000.0;
			// dbg(productividad);
			// return productividad;// por si ya perdi
			// }

			// Veo cuantas posibilidades tengo de jugar la actual
			double ans = cuantasAnulan(F.getArr(), fichasPc);
			// Y la sumo a la productividad
			ans += cuantasAnulan(F.getArr(), fichasPc);
			ans *= 10.0;
			double c;
			// Ademas por cada lado de cada ficha i=ficha j=lado
			for (int i = 0; i < fichasPc.size(); i++) {
				for (int j = 0; j < 2; j++) {
					// Veo con cuantas fichas el jugador humano me puede(al
					// computador) responder la jugada
					c = cuantasAnulan(fichasPc.get(i).get(j), fichasJ);

					if (c == 0) { // si no me puede responder la jugada cueta
									// mas +4
						ans += 40.0;
					} else {
						// Y si me la puede responder utilizo
						// 10/cantVecesResponder para ver que tan util es la
						// ficha
						ans += (10.0 / c); // veo
											// que
											// tan
											// utiles
											// son
											// mis
											// fichas
											// con
											// respecto
											// a las
											// del
											// enemigo
					}
				}

			}
			productividad = ans;
			return productividad;
		} else if (nodo.getTipo() == MIN) {// Cada nodo min busca el peor
											// esenario para el MAX buscando la
											// menor productividad osea el peor
											// caso
			double min = 1000000;
			for (Iterator<NodoMiniMaxDomino> it = nodo.getHijos().iterator(); it
					.hasNext();) {
				NodoMiniMaxDomino ob = it.next();
				min = Math.min(calcularProductividad(ob), min);
			}
			productividad = min;
			// dbg("tipo Min", productividad);
			return productividad;
		} else if (nodo.getTipo() == MAX) {// Y cada nodo max hace lo contrario
											// a el MIN busca la mejor
			double max = -1000000;
			for (Iterator<NodoMiniMaxDomino> it = nodo.getHijos().iterator(); it
					.hasNext();) {
				NodoMiniMaxDomino ob = it.next();
				max = Math.max(calcularProductividad(ob), max);
			}
			productividad = max;
			// dbg("tipo Max", productividad);
			return productividad;// y algo mas
		}

		return -1;

	}

	// Para debugear
	private void dbg(Object... o) {
		System.out.println(Arrays.deepToString(o));
	}

	// Para debugear
	public void imprimir() {

		System.out.println(imprimirNodo());

		for (Iterator<NodoMiniMaxDomino> it = hijos.iterator(); it.hasNext();) {
			NodoMiniMaxDomino ob = it.next();
			ob.imprimir();
		}
	}

	// Para debugear, imprimo en la misma linea que me imprimo yo a mi padre
	// para identificarlo mejor
	public String imprimirPapa() {
		if (padre.getPadre() == null)
			return new String(" raiz tipo: " + SON[padre.tipo] + " ficha "
					+ padre.F.toString() + " fJ: " + hToString(padre.fichasJ)
					+ " fPc: " + hToString(padre.fichasPc) + " productividad "
					+ padre.productividad);
		else
			return new String("/tipo:" + SON[padre.getTipo()] + " ficha "
					+ padre.F.toString() + " fJ: " + hToString(padre.fichasJ)
					+ " fPc: " + hToString(padre.fichasPc) + " productividad "
					+ padre.productividad);
	}

	// Para debugear
	public String imprimirNodo() {
		if (padre == null)
			return new String(" raiz tipo: " + SON[tipo] + " ficha "
					+ F.toString() + " fJ: " + hToString(fichasJ) + " fPc: "
					+ hToString(fichasPc) + " productividad " + productividad);
		else
			return new String(" padre/ " + imprimirPapa() + " /tipo: "
					+ SON[tipo] + " ficha " + F.toString() + " fJ: "
					+ hToString(fichasJ) + " fPc: " + hToString(fichasPc)
					+ " productividad " + productividad);
	}

	// Para debugear
	public String hToString(Object... o) {
		return Arrays.deepToString(o);
	}

	// public static void main(String[] args) throws Exception {
	//
	// // para hacer pruebas? saco de la otra clase
	//
	// ArrayList<Ficha> fichas = new ArrayList<Ficha>();
	// // generar las fichas
	// for (byte i = 0; i < 7; i++) {
	// for (byte j = i; j < 7; j++) {
	// fichas.add(new Ficha(i, j));
	// // creo los strings
	// }
	// }
	// // repartir las fichas.
	// int ind, size = fichas.size() / 2;
	// Ficha p;
	// ArrayList<Ficha> fichasJ = new ArrayList<Ficha>();
	// for (int i = 0; i < size; i++) {
	// ind = (int) Math.round(Math.random() * (fichas.size() - 1));
	// p = fichas.remove(ind);
	// fichasJ.add(p);
	// // fichasJShow[i].cambiaIcono(p.getArr() + "-" + p.getAb() +
	// // ".png");
	// }
	// ArrayList<Ficha> fichasPc = fichas;
	// for (int i = 0; i < fichasPc.size(); i++) {
	// p = fichasPc.get(i);
	// // fichasPcShow[i].cambiaIcono(p.getArr() + "-" + p.getAb() +
	// // ".png");
	// }
	// Ficha a = fichasJ.remove(0);
	// NodoMiniMaxDomino n = new NodoMiniMaxDomino(null, fichasPc, fichasJ, a,
	// MAX, null, false);
	// n.expandir(0, 6);
	// // n.imprimir();
	// // n.calcularProductividad(n);
	// // n.imprimir();
	// n.calcularMiniMax(n);
	//
	// fichasJ.remove(0);
	// n = new NodoMiniMaxDomino(null, fichasPc, fichasJ, a, MAX, null, false);
	// n.expandir(0, 5);
	// n.calcularMiniMax(null);
	// // n.imprimir();
	// }
}