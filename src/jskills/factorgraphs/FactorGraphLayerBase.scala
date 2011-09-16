package jskills.factorgraphs

import java.util.Collection
import java.util.List

abstract class FactorGraphLayerBase[TValue] {

  def getUntypedFactors(): Collection[Factor[TValue]]

  def BuildLayer()

  def createPriorSchedule(): Schedule[TValue] = null

  def createPosteriorSchedule(): Schedule[TValue] = null

  def getOutputVariablesGroups(): List[_ <: List[_ <: Any]]

  def SetRawInputVariablesGroups(o: Any)
}
