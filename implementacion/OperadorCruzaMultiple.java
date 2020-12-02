package implementacion;

import base.*;
import java.util.*;

/**
 * Representa un operador de cruza que selecciona un operador distinto de forma
 * consecutiva
 */
public class OperadorCruzaMultiple extends OperadorCruza {
  private OperadorCruza[] operadoresCruza;

  public OperadorCruzaMultiple(OperadorCruza[] operadoresCruza) {
    this.operadoresCruza = operadoresCruza;
	}

  private int contador = 0;
  private int indice = 0;

  @Override
  public BitSet[] cruzar(int longitud, BitSet p1, BitSet p2) {
    if (contador == 5) {
      indice = (indice + 1) % operadoresCruza.length;
      contador = 0;
    }

    contador++;
    return operadoresCruza[indice].cruzar(longitud, p1, p2);
  }
}
