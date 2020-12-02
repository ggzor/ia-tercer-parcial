package implementacion;

import base.*;
import java.util.*;

/**
 * Representa un parámetro entero acotado por un valor mínimo y uno máximo
 */
public class ParametroAcotado extends Parametro {
  private int minimo;
  private int valores;
  private int longitud;
  private int maximo;

  public ParametroAcotado(int minimo, int maximo) {
    this.minimo = minimo;
    this.maximo = maximo;
    this.valores = maximo - minimo + 1;
    this.longitud = (int)Math.ceil(Math.log(this.valores) / Math.log(2));
  }
  
  @Override
  public BitSet aleatorio(Random r) {
    return BitSet.valueOf(new long[] { r.nextInt(valores) });
  }

  @Override
  public double decodificar(BitSet bitSet) {
    long arreglo[] = bitSet.toLongArray();
    long valor = arreglo.length == 0 ? 0 : arreglo[0];

    return minimo + valor;
  }

  @Override
  public int obtenerCantidadBits() {
    return longitud;
  }

  @Override
  public BitSet normalizar(BitSet bitSet) {
    if (decodificar(bitSet) > this.maximo)
      return BitSet.valueOf(new long[] { maximo - minimo });
      
    return bitSet;
  }
}
