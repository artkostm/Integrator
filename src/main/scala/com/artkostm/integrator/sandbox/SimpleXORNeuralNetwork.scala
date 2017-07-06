import java.io.File

import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.{DenseLayer, OutputLayer}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.factory.Nd4j

/**
  * Created by artsiom.chuiko on 06/07/2017.
  */
object XOR extends App {

  val model = new File("model1.dat")
  val inputLayer = new DenseLayer.Builder().nIn(2).nOut(3).name("Input").build()
  val hiddenLayer = new DenseLayer.Builder().nIn(3).nOut(2).name("Hidden").build()
  val outputLayer = new OutputLayer.Builder().nIn(2).nOut(2).name("Output").activation(Activation.SOFTMAX).build()

  val nncBuilder = new NeuralNetConfiguration.Builder()
  nncBuilder.iterations(60000).learningRate(0.1)

  val listBuilder = nncBuilder.list()
  listBuilder.layer(0, inputLayer)
  listBuilder.layer(1, hiddenLayer)
  listBuilder.layer(2, outputLayer)

  listBuilder.backprop(true)

  val network = new MultiLayerNetwork(listBuilder.build())
  network.init()

  //Training
  val trainingInputs = Nd4j.zeros(4, inputLayer.getNIn)
  val trainingOutputs = Nd4j.zeros(4, outputLayer.getNOut)

  trainingInputs.putScalar(Array(0, 0), 0)
  trainingInputs.putScalar(Array(0, 1), 0)
  trainingOutputs.putScalar(Array(0, 0), 0)
  trainingOutputs.putScalar(Array(0, 1), 1)

  trainingInputs.putScalar(Array(1, 0), 0)
  trainingInputs.putScalar(Array(1, 1), 1)
  trainingOutputs.putScalar(Array(1, 0), 1)
  trainingOutputs.putScalar(Array(1, 1), 0)

  trainingInputs.putScalar(Array(2, 0), 1)
  trainingInputs.putScalar(Array(2, 1), 0)
  trainingOutputs.putScalar(Array(2, 0), 1)
  trainingOutputs.putScalar(Array(2, 1), 0)

  trainingInputs.putScalar(Array(3, 0), 1)
  trainingInputs.putScalar(Array(3, 1), 1)
  trainingOutputs.putScalar(Array(3, 0), 0)
  trainingOutputs.putScalar(Array(3, 1), 1)

  val dataSet = new DataSet(trainingInputs, trainingOutputs)

  network.fit(dataSet)

  //Create input
  val actualInput = Nd4j.zeros(1, 2)
  actualInput.putScalar(Array(0, 0), 1)
  actualInput.putScalar(Array(0, 1), 1)

  //Generate output
  val actualOutput = network.output(actualInput)
  println(s"Output:\n$actualOutput")
}
