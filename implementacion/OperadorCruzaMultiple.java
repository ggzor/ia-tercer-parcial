package implementacion;

import base.*;
import java.util.*;

/**
 * Representa un operador de cruza que selecciona un operador distinto de forma
 * aleatoria
 */
public class OperadorCruzaMultiple extends OperadorCruza {
  private Random random;
  private OperadorCruza[] operadoresCruza;

  public OperadorCruzaMultiple(Random random, OperadorCruza[] operadoresCruza) {
    this.random = random;
    this.operadoresCruza = operadoresCruza;
	}

@Override
  public BitSet[] cruzar(int longitud, BitSet p1, BitSet p2) {
    return operadoresCruza[random.nextInt(operadoresCruza.length)].cruzar(longitud, p1, p2);
  }
}
