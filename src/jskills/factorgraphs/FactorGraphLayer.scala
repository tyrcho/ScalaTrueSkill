package jskills.factorgraphs


import scala.collection.mutable.ListBuffer


abstract class FactorGraphLayer[TParentFactorGraph <: FactorGraph[TParentFactorGraph], TValue, TBaseVariable <: Variable[TValue], TInputVariable <: Variable[TValue], TFactor <: Factor[TValue], TOutputVariable <: Variable[TValue]](parentGraph: TParentFactorGraph)
  extends FactorGraphLayerBase[TValue] {

  protected val localFactors = ListBuffer.empty[TFactor]
  protected val outputVariablesGroups = ListBuffer.empty[Seq[TOutputVariable]]
  protected val inputVariablesGroups = ListBuffer.empty[Seq[TInputVariable]]

  def getLocalFactors() = localFactors

  def getOutputVariablesGroups() = outputVariablesGroups

  //TODO: fix this hack
  def setRawInputVariablesGroups(o: Any) {
    inputVariablesGroups.clear
    for (l <- o.asInstanceOf[Seq[Seq[TInputVariable]]]) {
      inputVariablesGroups += l
    }
  }

  def getUntypedFactors() = localFactors.asInstanceOf[Seq[Factor[TValue]]]

  def addOutputVariableGroup(group: Seq[TOutputVariable]) {
    outputVariablesGroups += group.toList
  }

  def addOutputVariable(v: TOutputVariable) {
    addOutputVariableGroup(List(v))
  }

  protected def scheduleSequence(
    itemsToSequence: Seq[Schedule[TValue]],
    nameFormat: String,
    args: Any*): Schedule[TValue] = {
    val formattedName = format(nameFormat, args)
    return new ScheduleSequence[TValue](formattedName, itemsToSequence)
  }

  protected def addLayerFactor(factor: TFactor) {
    localFactors += factor
  }
}