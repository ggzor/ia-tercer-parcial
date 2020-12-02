package implementacion;

import base.*;
import java.util.*;

/**
 * Representa un parámetro decimal acotado del que se puede ajustar su granularidad
 * es decir, se puede elegir cuántos bits exactamente va a ocupar el parámetro
 */
public class ParametroGranular extends Parametro {
  private int valores;
  private int longitud;
  private double minimo;
  private double paso;

  public ParametroGranular(double minimo, double maximo, int bitsGranularidad) {
    this.minimo = minimo;
    this.valores = (int) Math.pow(2, bitsGranularidad);
    this.paso = (maximo - minimo) / (double)(this.valores - 1);
    this.longitud = bitsGranularidad;
	}

  @Override
  public BitSet aleatorio(Random r) {
    return BitSet.valueOf(new long[] { r.nextInt(valores) });
  }

  @Override
  public double decodificar(BitSet bitSet) {
    long arreglo[] = bitSet.toLongArray();
    long valor = arreglo.length == 0 ? 0 : arreglo[0];
    return minimo + paso * valor;
  }

  @Override
  public int obtenerCantidadBits() {
    return longitud;
  }

  @Override
  public BitSet normalizar(BitSet bitSet) {
    // Limpiar bits extra para evitar problemas de decodificación
    if (bitSet.size() >= longitud)
      bitSet.set(longitud, bitSet.size(), false);
    return bitSet;
  }
}
