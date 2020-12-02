package base;

import java.util.BitSet;
import java.util.Random;

/**
 * Representa un parámetro ajustable de un individuo de una población
 */
public abstract class Parametro {
  // Obtiene un valor aleatorio válido para este parámetro basado en el generador r
  public abstract BitSet aleatorio(Random r);

  // Obtiene el valor de este parámetro como un decimal a partir de su 
  // representación binaria
  public abstract double decodificar(BitSet bitSet);

  // Normaliza el parametro para que sea un valor válido
  public abstract BitSet normalizar(BitSet bitSet);

  // Obtiene la cantidad de bits que son necesarios para representar este campo en binario
  public abstract int obtenerCantidadBits();
}
