/**
  * Created by artsiom.chuiko on 05/07/2017.
  */

import java.io.{File, FileInputStream, FileOutputStream}
import java.util

import org.datavec.image.loader.NativeImageLoader
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator
import org.deeplearning4j.eval.Evaluation
import org.deeplearning4j.nn.conf.inputs.InputType
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.deeplearning4j.scalnet.layers.{Dense, DenseOutput}
import org.deeplearning4j.scalnet.regularizers.L2
import org.deeplearning4j.scalnet.models.NeuralNet
import org.deeplearning4j.scalnet.optimizers.SGD
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.api.DataSet
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.ListBuffer

/**
  * Two-layer MLP for MNIST using DL4J-style NeuralNet
  * model construction pattern.
  */
object MLPMnistTwoLayerExample extends App {
  private val log: Logger = LoggerFactory.getLogger(MLPMnistTwoLayerExample.getClass)

  private val numRows: Int = 28
  private val numColumns: Int = 28
  private val outputNum: Int = 10
  private val batchSize: Int = 64
  private val rngSeed: Int = 123
  private val numEpochs: Int = 15
  private val learningRate: Double = 0.0015
  private val momentum: Double = 0.98

//  private val mnistTrain: DataSetIterator = new MnistDataSetIterator(batchSize, true, rngSeed)
//  private val mnistTest: DataSetIterator = new MnistDataSetIterator(batchSize, false, rngSeed)
//
//  log.info("Build model....")
//  private val model: NeuralNet = new NeuralNet(inputType = InputType.convolutional(28,28,1), rngSeed = rngSeed)
//  model.add(new Dense(500, nIn = numRows*numColumns, weightInit = WeightInit.XAVIER, activation = "relu",
//    regularizer = L2(learningRate * 0.005)))
//  model.add(new Dense(100, weightInit = WeightInit.XAVIER, activation = "relu", regularizer = L2(learningRate * 0.005)))
//  model.add(new DenseOutput(outputNum, weightInit = WeightInit.XAVIER, activation = "softmax",
//    lossFunction = LossFunction.NEGATIVELOGLIKELIHOOD, regularizer = L2(learningRate * 0.005)))
//  model.compile(optimizer = SGD(learningRate, momentum = momentum, nesterov = true))
//
//  log.info("Train model....")
//  model.fit(mnistTrain, nbEpoch = numEpochs, List(new ScoreIterationListener(5)))
//
//  log.info("Evaluate model....")
//  val evaluator: Evaluation = new Evaluation(outputNum)
//  while(mnistTest.hasNext){
//    val next: DataSet = mnistTest.next()
//    val output: INDArray = model.predict(next)
//    evaluator.eval(next.getLabels, output)
//  }
//  log.info(evaluator.stats())
//  log.info("****************Example finished********************")
//  println(evaluator.stats())
//
//  ModelSerializer.writeModel(model.getNetwork, new FileOutputStream(new File("model2.dat")), true)

  val pathToFile = "model2.dat"
  val model = ModelSerializer.restoreMultiLayerNetwork(new FileInputStream(pathToFile))

  val imgInput = new NativeImageLoader(numRows, numColumns, 1).asMatrix(new File("7-2.jpg"))
  new ImagePreProcessingScaler(0,1).transform(imgInput)

  val imgOutput = model.output(imgInput, false)
  model.predict(imgInput).foreach(println)

  println(s"Output: $imgOutput")

}

