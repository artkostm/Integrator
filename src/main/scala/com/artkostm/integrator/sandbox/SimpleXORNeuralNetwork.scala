import java.io.{File, FileInputStream, FileOutputStream}

import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.{DenseLayer, OutputLayer}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.factory.Nd4j

/**
  * Created by artsiom.chuiko on 06/07/2017.
  */
object XOR extends App {

  val model = new File("model1.dat")

  val network = model.exists() match {
    case true => NNUtils.restore(model)
    case false => NNUtils.createAndTrain(model)
  }

  //Create input
  val actualInput = Nd4j.zeros(1, 2)
  actualInput.putScalar(Array(0, 0), 0)
  actualInput.putScalar(Array(0, 1), 0)

  //Generate output
  val actualOutput = network.output(actualInput)
  println(s"Output:\n$actualOutput")
}

object NNUtils {
  def restore(fileToRestore: File): MultiLayerNetwork = {
    ModelSerializer.restoreMultiLayerNetwork(new FileInputStream(fileToRestore))
  }

  def createAndTrain(fileToSaveIfNotExist: File): MultiLayerNetwork = {
    val inputLayer = new DenseLayer.Builder().nIn(2).nOut(3).name("Input").build()
    val hiddenLayer = new DenseLayer.Builder().nIn(3).nOut(2).name("Hidden").build()
    val outputLayer = new OutputLayer.Builder().nIn(2).nOut(2).name("Output").activation(Activation.SOFTMAX).build()

    val nncBuilder = new NeuralNetConfiguration.Builder()
    nncBuilder.iterations(1000000).learningRate(0.01)

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

    ModelSerializer.writeModel(network, new FileOutputStream(fileToSaveIfNotExist), true)

    network
  }
}
