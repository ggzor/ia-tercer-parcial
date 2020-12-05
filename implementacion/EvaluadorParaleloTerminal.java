package implementacion;

import base.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Representa una forma de evaluar múltiples individuos simultaneamente
 * utilizando la terminal como intermediario
 */
public class EvaluadorParaleloTerminal extends Evaluador<Individuo> {
  private Path archivo;
  private int semilla;
  private int nivelParalelismo;
  private Path jarWeka;

  public EvaluadorParaleloTerminal(int nivelParalelismo, Path archivo, Path jarWeka, int semilla) {
    this.nivelParalelismo = nivelParalelismo;
    this.jarWeka = jarWeka;
    this.semilla = semilla;
    this.archivo = archivo;
  }

  private AtomicInteger contador = new AtomicInteger(0);

  @Override
  public void evaluar(List<Individuo> poblacion) {
    contador.set(0);

    System.out.printf("Comenzando evaluación de %d individuos...\n", poblacion.size());
    System.out.println("Nota: Los individuos no aparecen en orden ya que la evaluación se realiza en paralelo");
    System.out.println("Individuos evaluados: ");

    // Generar la cantidad de hilos que se especifique
    List<Thread> threads =
      IntStream.range(0, nivelParalelismo)
               .mapToObj(i -> new Thread(() -> {
                 int indice;
                  while ((indice = contador.getAndIncrement()) < poblacion.size()) {
                    Individuo individuo = poblacion.get(indice);
                    try {
                      double fitness = obtenerFitness(individuo);
                      System.out.printf("Individuo: %d\n", indice);
                      System.out.printf("Fitness: %2.3f\n", fitness);
                      individuo.imprimir();
                      System.out.println();

                      individuo.setFitness(fitness);
                    } catch (Exception ex) {
                      System.out.println("Error mientras se evaluaba un individuo");
                      System.exit(-1);
                    }
                  }
               }))
               .collect(Collectors.toList());

    threads.forEach(Thread::start);

    try {
      // Esperar cada hilo
      for (Thread thread : threads) {
        thread.join();
      }
    } catch (Exception ex) {
      // Terminar inmediatamente el proceso
      // Error inesperado al esperar hilos
      ex.printStackTrace();
      System.exit(-1);
    }

    System.out.println();
  }

  private static String formatoComando = "java -classpath %s "
                                       + "weka.classifiers.functions.MultilayerPerceptron -t %s -x 5 "
                                       + "-N %d -H %s -L %f -M %f -S %d";

  public double obtenerFitness(Individuo individuo) throws Exception {
    Runtime rt = Runtime.getRuntime();
    String comando = String.format(formatoComando,
                                   this.jarWeka.toAbsolutePath().toString(),
                                   this.archivo.toAbsolutePath().toString(),
                                   individuo.getEpocas(),
                                   IntStream.range(0, individuo.getCapas())
                                            .mapToObj(i -> String.valueOf(individuo.getNeuronas()))
                                            .collect(Collectors.joining(",")),
                                   individuo.getLearningRate(),
                                   individuo.getMomentum(),
                                   semilla);

    String[] commands = comando.split(" ");
    Process proc = rt.exec(commands);

    BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(proc.getInputStream()));

    // Encontrar el fitness en la salida del proceso
    String s = null;
    double fitness = 0;
    String fitnessStr = "";
    while ((s = stdInput.readLine()) != null) {
      if (s.contains("Correctly Classified Instances")) {
        String aux = "";
        int indice = s.length() - 2;
        fitnessStr = "";
        while (s.charAt(indice) == ' ') indice--;
        while (s.charAt(indice) != ' ') {
          aux = aux + s.charAt(indice);
          indice--;
        }
        for (int i = aux.length() - 1;i >= 0;i--) fitnessStr = fitnessStr + aux.charAt(i);
        fitness = Double.parseDouble(fitnessStr);
      }
    }

    /*
    // Imprimir errores
    while ((s = stdErr.readLine()) != null)
      System.err.println(s);
    */

    return fitness;
  }
}
