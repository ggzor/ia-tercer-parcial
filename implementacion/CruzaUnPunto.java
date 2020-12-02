package implementacion;

import base.*;
import java.util.*;

public class CruzaUnPunto extends OperadorCruza {
  
  private Random random;

  public CruzaUnPunto(Random random) {
    this.random = random;
  }

  @Override
  public BitSet[] cruzar(int longitud, BitSet p1, BitSet p2) {
    BitSet bitshijo1 = new BitSet(longitud);
    BitSet bitshijo2 = new BitSet(longitud);
    int n = 1 + random.nextInt(longitud - 2);
    for (int i = longitud; i >= 0; i--) {
      if (i >= 0 && i <= n) {
        bitshijo1.set(i, p1.get(i));
        bitshijo2.set(i, p2.get(i));
      } else {
        bitshijo1.set(i, p2.get(i));
        bitshijo2.set(i, p1.get(i));
      }
    }
    return new BitSet[] { bitshijo1, bitshijo2 };
  }
}
