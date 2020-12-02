package base;

import java.util.List;

/**
 * Representa la estrategia que se va a tomar para seleccionar los 
 * individuos de los que se va a generar descendencia
 */ 
public abstract class EstrategiaCruza<T> {
  // Es el método principal para generar la descendencia de una población dada
	public abstract List<T> cruzar(List<T> poblacion);
}
