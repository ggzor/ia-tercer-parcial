package implementacion;
/**
 * CruzaEnCruz
    001 010
    110 011

    001 011
    110 010
    011 110
    010 001

    1010 110
    1111 000

    1010 000
    1111 110
    0000 101
    1110 111
 */
import base.*;
import java.util.BitSet;

/**
 * Representa una operación de cruza utilizando el patrón de cruz
 */
public class CruzaEnCruz extends OperadorCruza {
  @Override
  public BitSet[] cruzar(int longitud, BitSet p1, BitSet p2) {
    int puntoMedio, auxPunto;
    BitSet hijo1 = new BitSet(longitud);
    BitSet hijo2 = new BitSet(longitud);
    BitSet hijo3 = new BitSet(longitud);
    BitSet hijo4 = new BitSet(longitud);
    puntoMedio = (int) Math.floor(longitud / 2);
    if (longitud % 2 == 0)
      auxPunto = puntoMedio - 1;
    else
      auxPunto = puntoMedio;

    for (int i = longitud - 1; i >= puntoMedio; i--) {
      hijo1.set(i, p1.get(i));
      hijo2.set(i, p2.get(i));
      hijo3.set(i, p1.get(auxPunto));
      hijo4.set(i, p2.get(auxPunto));
      auxPunto--;
    }

    auxPunto = longitud - 1;
    for (int i = puntoMedio - 1; i >= 0; i--) {
      hijo1.set(i, p2.get(i));
      hijo2.set(i, p1.get(i));
      hijo3.set(i, p1.get(auxPunto));
      hijo4.set(i, p2.get(auxPunto));
      auxPunto--;
    }

    return new BitSet[] { hijo1, hijo2, hijo3, hijo4 };
  }
}