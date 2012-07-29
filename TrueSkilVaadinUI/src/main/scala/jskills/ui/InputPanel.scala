package jskills.ui
import vaadin.scala.VerticalLayout
import vaadin.scala.Button
import scala.collection.mutable.ListBuffer
import scala.collection.immutable.ListMap

class InputPanel extends VerticalLayout {
  var playerInputs = List.empty[PlayerEditor]

  add(new Button("Add player", _ => addPlayerInput()))

  for (i <- 1 to 4) addPlayerInput()

  def addPlayerInput() {
    playerInputs :+= add(PlayerEditor())
  }

  def teams(): List[Player] = playerInputs map (_.values)
}