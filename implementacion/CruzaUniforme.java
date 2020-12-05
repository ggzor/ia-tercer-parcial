package implementacion;

import java.util.BitSet;
import java.util.Random;

import base.*;

/**
 * Representa una operador de cruza que selecciona aleatoriamente
 * bits de cada uno de los padres
 */
public class CruzaUniforme extends OperadorCruza {

  private Random random;

  public CruzaUniforme(Random random) {
    this.random = random;
	}

  @Override
  public BitSet[] cruzar(int longitud, BitSet p1, BitSet p2) {
    BitSet[] padres = new BitSet[] { p1, p2 };
    BitSet[] hijos = new BitSet[2];

    for (int i = 0; i < hijos.length; i++) {
      hijos[i] = new BitSet();
      for (int j = 0; j < longitud; j++) {
        BitSet padreAleatorio = padres[random.nextInt(padres.length)];
        hijos[i].set(j, padreAleatorio.get(j));
      }
    }
    
    return hijos;
  }
}
