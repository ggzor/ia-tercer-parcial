package implementacion;

import base.*;
import java.util.BitSet;
import java.util.Random;

/**
 * Representa una operación de cruza realizada en zig-zag
 */
public class CruzaCombinada extends OperadorCruza {
    private Random random;
    
    public CruzaCombinada(Random random) {
        this.random = random;
    }

    @Override
    public BitSet[] cruzar(int longitud, BitSet p1, BitSet p2) {
        int punto;
        BitSet hijo1 = new BitSet(longitud);
        BitSet hijo2 = new BitSet(longitud);
        punto = longitud - 1 - random.nextInt(longitud);
        for (int i = 0; i < punto; i++ ) {
            if (i % 2 == 0) {
                hijo1.set(i,p2.get(i));
                hijo2.set(i,p1.get(i));             
            }
            else {
                hijo1.set(i,p1.get(i));
                hijo2.set(i,p2.get(i));
            }
        }
        for (int i = punto; i < longitud ; i++ ) {
            hijo1.set(i,p1.get(i));
            hijo2.set(i,p2.get(i));
        }
        return new BitSet[] { hijo1, hijo2 };
    }
}