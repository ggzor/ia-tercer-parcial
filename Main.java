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
    // NOTA: En caso de que el JAR de Weka se encuentre en otra localización
    //       se debe de cambiar la dirección que se encuentra a continuación
    Path jarWeka = Paths.get("C:\\Program Files\\Weka-3-8\\weka.jar");
    // La dirección del conjunto de datos
    Path dataset = Paths.get("./datos/dataset.arff");
    // Características del conjunto de datos
    int atributos = 15;
    int clases = 2;

    // Cantidad de hilos a utilizar
    int cantidadHilos = 7;

    // Configuración del algoritmo genético
    int cantidadIndividuos = 30;
    int cantidadGeneraciones = 36;
    double porcentajeMutaciones = 0.07;
    double porcentajeMejoresCruzar = 0.15;

    // La semilla para los números pseudoaleatorios
    int semilla = 0;
    // El generador de números pseudoaleatorios
    Random random = new Random(semilla);

    // Inicializar configuración de parámetros de neuronas
    Individuo.inicializar(atributos, clases);

    // Seleccionar las implementaciones para las clases abstractas necesarias
    // Realizar evaluación en paralelo utilizando la línea de comandos para conectar con Weka
    Evaluador<Individuo> evaluador = new EvaluadorParaleloTerminal(cantidadHilos, dataset, jarWeka, semilla);
    // Utilizar 4 operadores distintos de cruza y aplicarlos secuencialmente
    OperadorCruza operadorCruza = new OperadorCruzaMultiple(new OperadorCruza[] {
      new CruzaEnCruz(),
      new CruzaUnPunto(random),
      new CruzaUniforme(random),
      new CruzaCombinada(random)
    });
    // Cruzar tomando un porcentaje de la población y cruzándola con todos los individuos
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

    // ALGORITMO GENÉTICO PRINCIPAL
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
                        .distinct()
                        .limit(cantidadIndividuos)
                        .collect(Collectors.toList());

      // Imprimir estadísticas y hacer respaldo
      procesarNuevaGeneracion(i, poblacion);
    }

    // Imprimir el mejor
    System.out.println("El mejor individuo encontrado es:");
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
