using System;
using System.Collections.Generic;

namespace Moserware.Skills.FactorGraphs
{
    public class VariableFactory<TValue>
    {
        // using a Func<TValue> to encourage fresh copies in case it's overwritten
        private final List<Variable<TValue>> _CreatedVariables = new List<Variable<TValue>>();
        private final Func<TValue> _VariablePriorInitializer;

        public VariableFactory(Func<TValue> variablePriorInitializer)
        {
            _VariablePriorInitializer = variablePriorInitializer;
        }

        public Variable<TValue> CreateBasicVariable(String nameFormat, params Object[] args)
        {
            newVar = new Variable<TValue>(
                String.Format(nameFormat, args),
                this,
                _CreatedVariables.Count,
                _VariablePriorInitializer());

            _CreatedVariables.Add(newVar);
            return newVar;
        }

        public KeyedVariable<TKey, TValue> CreateKeyedVariable<TKey>(TKey key, String nameFormat, params Object[] args)
        {
            newVar = new KeyedVariable<TKey, TValue>(
                key,
                String.Format(nameFormat, args),
                this,
                _CreatedVariables.Count,
                _VariablePriorInitializer());

            _CreatedVariables.Add(newVar);
            return newVar;
        }
    }
}