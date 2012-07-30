package jskills.ui
import vaadin.scala.VerticalLayout
import vaadin.scala.Button
import scala.collection.mutable.ListBuffer

class OutputPanel extends VerticalLayout {
  def addPlayers(players: Iterable[Player]) {
    removeAllComponents()
    for (p <- players) {
      val editor = add(PlayerEditor())
      editor.values = p
    }
  }
}