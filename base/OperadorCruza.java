package base;

import java.util.BitSet;

/**
 * Representa el operador de cruza, con lo que debe generar 0 o más descendientes
 * a partir de dos padres
 */
public abstract class OperadorCruza {
  // El método principal para cruzar dos padres de una longitud dada
  public abstract BitSet[] cruzar(int longitud, BitSet p1, BitSet p2);
}
