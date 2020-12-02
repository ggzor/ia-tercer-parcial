package implementacion;

import base.*;

import java.util.List;
import java.util.stream.Collectors;

/** 
 * Representa una estrategia de cruza en la que se selecciona un porcentaje de la
 * población que se cruzará con todos los demás individuos de la población
 * */ 
public class CruzadorMejores extends EstrategiaCruza<Individuo> {
  private double porcentajeMejoresCruzar;
  private OperadorCruza operadorCruza;

  public CruzadorMejores(double porcentajeMejoresCruzar, OperadorCruza operadorCruza) {
    this.porcentajeMejoresCruzar = porcentajeMejoresCruzar;
    this.operadorCruza = operadorCruza;
	}

  @Override
  public List<Individuo> cruzar(List<Individuo> poblacion) {
    return poblacion.stream()
                    .limit((long)(porcentajeMejoresCruzar * (double)poblacion.size()))
                    .flatMap(i1 -> poblacion.stream()
                                            .flatMap(i2 -> i1.cruzar(i2, operadorCruza)))
                    .collect(Collectors.toList());
  }
}
