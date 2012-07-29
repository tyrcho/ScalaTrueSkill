package jskills.ui
import vaadin.scala.HorizontalLayout
import vaadin.scala.TextField

class PlayerEditor extends HorizontalLayout {
  import PlayerEditor._
  val name = addField("player", "player" + nextPlayer())
  val team = addField("team", "team 1")
  val mu = addField("µ", "25.0")
  val sigma = addField("sigma", "8.3")

  def readOnly() {
    for (f <- Seq(name, team, mu, sigma)) f.setEnabled(false)
  }

  def values = Player(
    name.getValue.toString,
    team.getValue.toString,
    mu.getValue.toString.toDouble,
    mu.getValue.toString.toDouble)

  def values_=(p: Player) {
    name.setValue(p.name)
    team.setValue(p.team)
    mu.setValue(p.mu)
    sigma.setValue(p.sigma)
  }

  private def addField(c: String, initialValue: String): TextField =
    add(new TextField(caption = c, value = initialValue))
}

object PlayerEditor {
  def apply(readOnly: Boolean = false) = {
    val p = new PlayerEditor
    if (readOnly) p.readOnly()
    p
  }

  var playerCount = 0
  def nextPlayer() = {
    playerCount += 1
    playerCount
  }
}