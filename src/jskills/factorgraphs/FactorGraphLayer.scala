package jskills.factorgraphs

import java.util.ArrayList

import java.util.List
import collection.JavaConversions._

abstract class FactorGraphLayer[TParentFactorGraph <: FactorGraph[TParentFactorGraph], TValue, TBaseVariable <: Variable[TValue], TInputVariable <: Variable[TValue], TFactor <: Factor[TValue], TOutputVariable <: Variable[TValue]](parentGraph: TParentFactorGraph)
  extends FactorGraphLayerBase[TValue] {

  protected val localFactors = new ArrayList[TFactor]()
  protected val outputVariablesGroups = new ArrayList[List[TOutputVariable]]()
  protected val inputVariablesGroups = new ArrayList[List[TInputVariable]]()

  def getLocalFactors() = localFactors

  def getOutputVariablesGroups() = outputVariablesGroups

  def setRawInputVariablesGroups(o: Any) {
    inputVariablesGroups.clear
    for (l <- o.asInstanceOf[List[List[TInputVariable]]]) {
      inputVariablesGroups.add(l)
    }
  }

  def getUntypedFactors() = localFactors.asInstanceOf[Seq[Factor[TValue]]]

  def addOutputVariableGroup(group: List[TOutputVariable]) {
    outputVariablesGroups.add(group)
  }

  def addOutputVariable(v: TOutputVariable) {
    val g = new ArrayList[TOutputVariable](1)
    g.add(v)
    addOutputVariableGroup(g)
  }

  protected def scheduleSequence(
    itemsToSequence: Seq[Schedule[TValue]],
    nameFormat: String,
    args: Any*): Schedule[TValue] = {
    val formattedName = format(nameFormat, args)
    return new ScheduleSequence[TValue](formattedName, itemsToSequence)
  }

  protected def addLayerFactor(factor: TFactor) {
    localFactors.add(factor)
  }
}