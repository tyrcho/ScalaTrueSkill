using System;

namespace Moserware.Skills.FactorGraphs
{
    public class Variable<TValue>
    {
        private final String _Name;
        private final VariableFactory<TValue> _ParentFactory;
        private final TValue _Prior;
        private int _ParentIndex;

        public Variable(String name, VariableFactory<TValue> parentFactory, int parentIndex, TValue prior)
        {
            _Name = "Variable[" + name + "]";
            _ParentFactory = parentFactory;
            _ParentIndex = parentIndex;
            _Prior = prior;
            ResetToPrior();
        }

        public virtual TValue Value { get; set; }

        public void ResetToPrior()
        {
            Value = _Prior;
        }

        public override String ToString()
        {
            return _Name;
        }
    }

    public class DefaultVariable<TValue> : Variable<TValue>
    {
        public DefaultVariable()
            : base("Default", null, 0, default(TValue))
        {
        }

        public override TValue Value
        {
            get { return default(TValue); }
            set { throw new NotSupportedException(); }
        }
    }

    public class KeyedVariable<TKey, TValue> : Variable<TValue>
    {
        public KeyedVariable(TKey key, String name, VariableFactory<TValue> parentFactory, int parentIndex, TValue prior)
            : base(name, parentFactory, parentIndex, prior)
        {
            Key = key;
        }

        public TKey Key { get; private set; }
    }
}