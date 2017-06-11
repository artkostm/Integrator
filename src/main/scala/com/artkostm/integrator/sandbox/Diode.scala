import diode.ActionResult.ModelUpdate
import diode.{Action, Circuit}

/**
  * Created by artsiom.chuiko on 11/06/2017.
  */
case class BasicModel(name: String, interactions: Int)
case class Interact(name: String) extends Action
case object Reset extends Action

object BasicCircuit extends Circuit[BasicModel] {
  override protected def initialModel: BasicModel = BasicModel("root", 0)

  override protected def actionHandler: BasicCircuit.HandlerFunction = (model, action) => action match {
    case Interact(name) => Some(ModelUpdate(model.copy(s"${model.name}:$name", model.interactions + 1)))
    case Reset => Some(ModelUpdate(model.copy(name = "root", interactions = 0)))
    case _ => None
  }
}

object DiodeApp extends App {

}
