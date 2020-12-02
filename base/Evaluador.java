package base;

import java.util.List;

// Representa la forma en la que se evalúan los individuos de la población
public abstract class Evaluador<T> {
  // Asigna un fitness a todos los individuos de la población dada
	public abstract void evaluar(List<T> poblacion);
}
