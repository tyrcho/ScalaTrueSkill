// Generated by delombok at Mon Sep 05 01:05:25 CEST 2011
package jskills.factorgraphs

class KeyedVariable[K, V](val key: K, prior: V, name: String)
  extends Variable[V](prior, name)

object KeyedVariable {
  def apply[K, V](key: K, prior: V, name: String, args: Any*) = {
    new KeyedVariable[K, V](key, prior, format(name, args))
  }
}