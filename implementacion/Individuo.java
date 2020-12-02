package implementacion;

import base.*;

import java.io.Serializable;
import java.util.*;
import java.util.stream.*;

/**
 * La clase principal que representa un individuo de la población
 */
public class Individuo implements Serializable {
  private static final long serialVersionUID = -3627076624459389937L;

  public static final Parametro EPOCAS = 
    new ParametroAcotado(100, 1000);
  public static final Parametro CAPAS = 
    new ParametroAcotado(1, 4);
  // Requiere inicialización
  public static Parametro NEURONAS;
  public static final Parametro LEARNING_RATE =
    new ParametroGranular(0.0, 0.3, 8);
  public static final Parametro MOMENTUM =
    new ParametroGranular(0.0, 0.3, 8);

  private static Parametro[] todosParametros;
  private static int cantidadBits;
  private static int longitudesCampos[];
  public static void inicializar(int atributos, int clases) {
    NEURONAS = new ParametroAcotado(clases, clases + atributos);
    todosParametros = new Parametro[] {
      EPOCAS,
      CAPAS,
      NEURONAS,
      LEARNING_RATE,
      MOMENTUM
    };
    cantidadBits = Arrays.stream(todosParametros)
                         .mapToInt(Parametro::obtenerCantidadBits)
                         .sum();

    longitudesCampos = Arrays.stream(todosParametros)
                             .mapToInt(Parametro::obtenerCantidadBits)
                             .toArray();
  }

  public BitSet campos[];
  public Double fitness = null;

  public Individuo(BitSet[] bitSets) {
    campos = bitSets;
  }

  public static Individuo aleatorio(Random r) {
    return new Individuo(
      Arrays.stream(todosParametros)
            .map(parametro -> parametro.aleatorio(r))
            .toArray(BitSet[]::new)
    );
  }

  public void setFitness(double d) {
    fitness = d;
  }

  public double getFitness() {
    if (fitness == null)
      throw new IllegalStateException("Aún no se ha establecido el fitness");
    return fitness;
  }

  public void mutar(Random random) {
    int indice = random.nextInt(cantidadBits);
    Utilerias.invertirBit(campos, longitudesCampos, indice);
  }

  public static final String nombres[] = new String[] {
    "Epocas", "Capas", "Neuronas", "Learning Rate", "Momentum"
  };

  public void imprimir() {
    System.out.println(IntStream.range(0, nombres.length)
              .mapToObj(i -> 
                String.format("  %s: %4.3f",
                               nombres[i],
                               todosParametros[i].decodificar(campos[i])))
              .collect(Collectors.joining(",\n", "{\n", "\n}")));
  }

  public Stream<Individuo> cruzar(Individuo otro, OperadorCruza operadorCruza) {
    ArrayList<Individuo> descendencia = new ArrayList<>();

    ArrayList<BitSet[]> campos = new ArrayList<>();
    for (int i = 0; i < Individuo.todosParametros.length; i++) {
      campos.add(operadorCruza.cruzar(todosParametros[i].obtenerCantidadBits(), 
                 this.campos[i], 
                 otro.campos[i]));
    }

    for (int i = 0; i < campos.get(0).length; i++) {
      BitSet parametros[] = new BitSet[Individuo.todosParametros.length];
      for (int j = 0; j < Individuo.todosParametros.length; j++) {
        parametros[j] = todosParametros[j].normalizar(campos.get(j)[i]);
      }
      descendencia.add(new Individuo(parametros));
    }

    return descendencia.stream();
  }

  public int getEpocas() {
    return (int)EPOCAS.decodificar(campos[0]);
  }

  public int getCapas() {
    return (int)CAPAS.decodificar(campos[1]);
  }

  public int getNeuronas() {
    return (int)NEURONAS.decodificar(campos[2]);
  }

  public double getLearningRate() {
    return LEARNING_RATE.decodificar(campos[3]);
  }

  public double getMomentum() {
    return MOMENTUM.decodificar(campos[4]);
  }
}
