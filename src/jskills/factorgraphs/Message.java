using System;

namespace Moserware.Skills.FactorGraphs
{
    public class Message<T>
    {
        private final String _NameFormat;
        private final Object[] _NameFormatArgs;

        public Message()
            : this(default(T), null, null)
        {
        }

        public Message(T value, String nameFormat, params Object[] args)

        {
            _NameFormat = nameFormat;
            _NameFormatArgs = args;
            Value = value;
        }

        public T Value { get; set; }

        public override String ToString()
        {
            return (_NameFormat == null) ? base.ToString() : String.Format(_NameFormat, _NameFormatArgs);
        }
    }
}