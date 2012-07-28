package jskills.factorgraphs


abstract class FactorGraphLayerBase[TValue] {

  def getUntypedFactors(): Seq[Factor[TValue]]

  def buildLayer()

  def createPriorSchedule(): Schedule[TValue] = null

  def createPosteriorSchedule(): Schedule[TValue] = null

  def getOutputVariablesGroups(): Seq[_ <: Seq[_ <: Any]]

  def setRawInputVariablesGroups(o: Any)
}
