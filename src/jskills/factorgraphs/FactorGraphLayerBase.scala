package jskills.factorgraphs;

import java.util.Collection;

abstract class FactorGraphLayerBase[TValue] {

  def getUntypedFactors(): Collection[Factor[TValue]]

  def BuildLayer()

  def createPriorSchedule(): Schedule[TValue] = null

  def createPosteriorSchedule(): Schedule[TValue] = null

  // HACK

  def SetRawInputVariablesGroups(value: Any);
  def GetRawOutputVariablesGroups(): Object
}
