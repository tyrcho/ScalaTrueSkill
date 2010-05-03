package jskills.factorgraphs;

import static jskills.Guard.argumentIsValidIndex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Factor<TValue> {

    private final List<Message<TValue>> messages = new ArrayList<Message<TValue>>();

    private final Map<Message<TValue>, Variable<TValue>> messageToVariableBinding =
        new HashMap<Message<TValue>, Variable<TValue>>();

    private final String name;
    private final List<Variable<TValue>> variables = new ArrayList<Variable<TValue>>();

    protected Factor(String name) { this.name = "Factor[" + name + "]"; }

    /** Returns the log-normalization constant of that factor **/
    public abstract double getLogNormalization();

    /** Returns the number of messages that the factor has **/
    public int getNumberOfMessages() { return messages.size(); }

    protected Collection<Variable<TValue>> getVariables() {
        return Collections.unmodifiableCollection(variables); 
    }

    protected Collection<Message<TValue>> getMessages() {
        return Collections.unmodifiableCollection(messages);
    }

    /** Update the message and marginal of the i-th variable that the factor is connected to **/
    public double updateMessage(int messageIndex) {
        argumentIsValidIndex(messageIndex, messages.size(), "messageIndex");
        return UpdateMessage(messages.get(messageIndex), messageToVariableBinding.get(messages.get(messageIndex)));
    }

    protected abstract double UpdateMessage(Message<TValue> message, Variable<TValue> variable);

    /** Resets the marginal of the variables a factor is connected to **/
    public void ResetMarginals() {
        for(Variable<TValue> variable : messageToVariableBinding.values())
            variable.resetToPrior();
    }

    /**
     * Sends the ith message to the marginal and returns the log-normalization
     * constant
     **/
    public double SendMessage(int messageIndex) {
        argumentIsValidIndex(messageIndex, messages.size(), "messageIndex");

        Message<TValue> message = messages.get(messageIndex);
        Variable<TValue> variable = messageToVariableBinding.get(message);
        return SendMessage(message, variable);
    }

    protected abstract double SendMessage(Message<TValue> message, Variable<TValue> variable);

    public abstract Message<TValue> CreateVariableToMessageBinding(Variable<TValue> variable);

    protected Message<TValue> CreateVariableToMessageBinding(Variable<TValue> variable, Message<TValue> message) {
        messages.add(message);
        messageToVariableBinding.put(message, variable);
        variables.add(variable);

        return message;
    }

    @Override
    public String toString() { return name != null ? name : super.toString(); }
}