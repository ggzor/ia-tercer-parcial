package implementacion;

import base.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Representa una forma de evaluar múltiples individuos simultaneamente
 * utilizando la terminal como intermediario
 */
public class EvaluadorParaleloTerminal extends Evaluador<Individuo> {
  private ForkJoinPool pool;
  private Path archivo;
  private int semilla;

  public EvaluadorParaleloTerminal(int nivelParalelismo, Path archivo, int semilla) {
    this.semilla = semilla;
    this.pool = new ForkJoinPool(nivelParalelismo);
    this.archivo = archivo;
  }

  @Override
  public void evaluar(List<Individuo> poblacion) {
    // Ejecutar todas las tareas en el pool
    pool.invokeAll(poblacion.stream().map(i -> new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        i.setFitness(obtenerFitness(i)); 
        return 0;
      }
    }).collect(Collectors.toList()));
  }

  private static String formatoComando = "java -classpath /usr/share/weka/weka.jar "
                                       + "weka.classifiers.functions.MultilayerPerceptron -t %s "
                                       + "-N %d -H %s -L %f -M %f -S %d";

  public double obtenerFitness(Individuo individuo) throws Exception {
    Runtime rt = Runtime.getRuntime();
    String comando = String.format(formatoComando, 
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
