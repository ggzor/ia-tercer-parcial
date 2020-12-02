import base.*;
import implementacion.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class Main {
  // El archivo en el que se irán respaldando las últimas generaciones
  private static Path checkpoint = Paths.get("checkpoint.bin");

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    int semilla = 0;
    Random random = new Random(semilla);

    Path dataset = Paths.get("/usr/share/weka/data/diabetes.arff");
    int atributos = 9;
    int clases = 2;

    int cantidadIndividuos = 50;
    int cantidadGeneraciones = 50;
    double porcentajeMutaciones = 0.03;
    double porcentajeMejoresCruzar = 0.30;


    // Inicializar configuración de parámetros de neuronas
    Individuo.inicializar(atributos, clases);

    // Seleccionar las implementaciones para las clases abstractas necesarias
    Evaluador<Individuo> evaluador = new EvaluadorParaleloTerminal(6, dataset, semilla);
    OperadorCruza operadorCruza = new OperadorCruzaMultiple(random, new OperadorCruza[] {
      new CruzaEnCruz(),
      new CruzaUnPunto(random)
    });
    EstrategiaCruza<Individuo> cruzador = new CruzadorMejores(porcentajeMejoresCruzar, operadorCruza);

    int generacionInicial = 0;
    List<Individuo> poblacion = null;

    // Revisar si existe alguna ejecucion anterior para restaurar
    if (Files.exists(checkpoint)) {
      System.out.println("Se encontró el archivo con la última generación evaluada satisfactoriamente y se procederá a cargar.");
      System.out.println("Si deseas comenzar la búsqueda desde el inicio, borra el archivo:");
      System.out.println(checkpoint.toAbsolutePath().toString());

      try (ObjectInputStream is = new ObjectInputStream(Files.newInputStream(checkpoint))) {
        Object valores[] = (Object[]) is.readObject();
        poblacion = (List<Individuo>) valores[0];
        generacionInicial = (Integer) valores[1];
        System.out.println("La última generación encontrada en el archivo es: " + (generacionInicial + 1));
      }
    }

    // Algoritmo genético principal
    // Crear la nueva generacion si es la primera vez
    if (poblacion == null) {
      System.out.println("Generando y evaluando población inicial...");

      // Población inicial
      poblacion = IntStream.range(0, cantidadIndividuos)
                           .mapToObj(i -> Individuo.aleatorio(random))
                           .collect(Collectors.toList());

      // Asignar fitness 
      evaluador.evaluar(poblacion);
    }

    // Mostrar estadísticas de la población inicial
    procesarNuevaGeneracion(generacionInicial, poblacion);

    // Iterar las generaciones especificadas
    for (int i = generacionInicial + 1; i < cantidadGeneraciones + 1; i++) {
      // Realizar la cruza
      List<Individuo> nuevosIndividuos = cruzador.cruzar(poblacion);
      
      // Mutar el porcentaje de individuos específicado
      Collections.shuffle(nuevosIndividuos, random);
      nuevosIndividuos.stream()
                      .limit((long)(nuevosIndividuos.size() * porcentajeMutaciones))
                      .forEach(individuo -> individuo.mutar(random));
      
      // Calcular fitness de nuevos
      evaluador.evaluar(nuevosIndividuos);

      // Preservar los mejores individuos
      poblacion = Stream.concat(poblacion.stream(), nuevosIndividuos.stream())
                        .sorted((i1, i2) -> Double.compare(i2.getFitness(), i1.getFitness()))
                        .limit(cantidadIndividuos)
                        .collect(Collectors.toList());
      
      // Imprimir estadísticas y hacer respaldo
      procesarNuevaGeneracion(i, poblacion);
    } 

    // Imprimir el mejor
    Individuo mejor = poblacion.get(0);
    mejor.imprimir();
  }

  private static void procesarNuevaGeneracion(int generacion, List<Individuo> poblacion) throws IOException {
    // Guardar los mejores individuos en el archivo
    try (ObjectOutputStream os = new ObjectOutputStream(Files.newOutputStream(checkpoint))) {
      os.writeObject(new Object[] { poblacion, (Integer)generacion }); 
    } 

    // Imprimir estadísticas
    System.out.println("Generacion " + (generacion + 1));
    System.out.printf("Mejor:    %2.3f\n", poblacion.stream()
                                                    .mapToDouble(Individuo::getFitness)
                                                    .max()
                                                    .getAsDouble());
    System.out.printf("Promedio: %2.3f\n", poblacion.stream()
                                                    .mapToDouble(Individuo::getFitness)
                                                    .average()
                                                    .getAsDouble());
    System.out.println();
  }
}
