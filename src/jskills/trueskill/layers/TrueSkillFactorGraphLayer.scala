package jskills.trueskill.layers;

import jskills.factorgraphs.Factor;
import jskills.factorgraphs.FactorGraphLayer;
import jskills.factorgraphs.Variable;
import jskills.numerics.GaussianDistribution;
import jskills.trueskill.TrueSkillFactorGraph;

abstract class TrueSkillFactorGraphLayer[TInputVariable <: Variable[GaussianDistribution], TFactor <: Factor[GaussianDistribution], TOutputVariable <: Variable[GaussianDistribution]](val parentGraph: TrueSkillFactorGraph)
  extends FactorGraphLayer[TrueSkillFactorGraph, GaussianDistribution, Variable[GaussianDistribution], TInputVariable, TFactor, TOutputVariable](parentGraph) {}
