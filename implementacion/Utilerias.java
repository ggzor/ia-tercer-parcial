package implementacion;

import java.util.BitSet;

/**
 * Clase que implementa diversas funciones de utilería
 */
public class Utilerias {

  // Invierte un bit en un índice dado que puede expandirse a través de múltiples bitsets
	public static void invertirBit(BitSet[] campos, int longitudes[], int indice) {
    int i, tamañoActual;
    i = tamañoActual = 0;
    while (tamañoActual + longitudes[i] < indice + 1) {
        tamañoActual += longitudes[i];
        i++;
        if (i == longitudes.length) break;
    }
    campos[i].flip((longitudes[i] - 1) - (indice - tamañoActual));
	}
}
