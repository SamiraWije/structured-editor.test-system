// Copyright 2002, SLAC, Stanford, U.S.A.
package org.freehep.util;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Stores a hashtable of hashtables, which can be indexed by a key and a subkey.
 * Keys and Values can be null.
 * 
 * @author Mark Donszelmann
 * @version $Id: DoubleHashtable.java,v 1.4 2009/06/22 02:18:20 hohenwarter Exp
 *          $
 */

public class DoubleHashtable extends AbstractCollection implements Serializable {

  private static final long serialVersionUID = -545653328241864972L;
  private final Hashtable<Object, Hashtable<Object, Object>> table;

  /**
   * creates a hashtable of hashtables
   */
  public DoubleHashtable() {
    table = new Hashtable<Object, Hashtable<Object, Object>>();
  }

  /**
   * removes all entries and sub-tables
   */
  @Override
  public void clear() {
    table.clear();
  }

  /**
   * removes all entries from a subtable
   */
  public void clear(Object key) {
    Hashtable<Object, Object> subtable = get(key);
    if (subtable != null)
      subtable.clear();
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException(
        "DoubleHashtable.clone() is not (yet) supported.");
  }

  /**
   * @return true if value exists in some sub-table
   */
  @Override
  public boolean contains(Object value) {
    if (value == null)
      value = this;

    for (Enumeration<Object> e = table.keys(); e.hasMoreElements();) {
      Hashtable<Object, Object> subtable = get(e.nextElement());
      if (subtable.contains(value))
        return true;
    }
    return false;
  }

  /**
   * @return true if sub-table exists for key
   */
  public boolean containsKey(Object key) {
    if (key == null)
      key = this;
    return table.containsKey(key);
  }

  /**
   * @return true if value exists for key and subkey
   */
  public boolean containsKey(Object key, Object subKey) {
    if (subKey == null)
      subKey = this;
    Hashtable<Object, Object> subtable = get(key);
    return subtable != null ? subtable.containsKey(subKey) : false;
  }

  /**
   * @return enumeration over all values in all sub-tables
   */
  public Enumeration elements() {
    return new Enumeration() {
      private final Enumeration<Hashtable<Object, Object>> subtableEnumeration = table.elements();
      private Enumeration valueEnumeration;
      private final Object nullValue = DoubleHashtable.this;

      public boolean hasMoreElements() {
        if (valueEnumeration == null || !valueEnumeration.hasMoreElements()) {
          if (!subtableEnumeration.hasMoreElements())
            return false;
          valueEnumeration = subtableEnumeration.nextElement()
              .elements();
        }
        return true;
      }

      public Object nextElement() {
        hasMoreElements();
        Object value = valueEnumeration.nextElement();
        return value == nullValue ? null : value;
      }
    };
  }

  /**
   * @return sub-table for key
   */
  public Hashtable<Object, Object> get(Object key) {
    if (key == null)
      key = this;
    return table.get(key);
  }

  /**
   * @return value for key and subkey, null in non-existent or null value was
   *         stored
   */
  public Object get(Object key, Object subKey) {
    if (subKey == null)
      subKey = this;
    Hashtable<Object, Object> table = get(key);
    Object value = table == null ? null : table.get(subKey);
    return value == this ? null : value;
  }

  /**
   * @return true if table is empty
   */
  @Override
  public boolean isEmpty() {
    return table.isEmpty();
  }

  /**
   * @return iterator over all values in all sub-tables
   */
  @Override
  public Iterator iterator() {
    return new Iterator() {
      private final Iterator subtableIterator = table.entrySet().iterator();
      private Map subtable;
      private Iterator valueIterator;
      private final Object nullValue = DoubleHashtable.this;

      public boolean hasNext() {
        if (valueIterator == null || !valueIterator.hasNext()) {
          if (!subtableIterator.hasNext())
            return false;
          Map.Entry entry = (Map.Entry) subtableIterator.next();
          subtable = (Map) entry.getValue();
          valueIterator = subtable.entrySet().iterator();
        }
        return true;
      }

      public Object next() {
        hasNext();
        Map.Entry entry = (Map.Entry) valueIterator.next();
        Object value = entry.getValue();
        return value == nullValue ? null : value;
      }

      public void remove() {
        valueIterator.remove();

        if (subtable.isEmpty())
          subtableIterator.remove();
      }
    };
  }

  /**
   * @return enumeration of keys in table
   */
  public Enumeration<Object> keys() {
    return table.keys();
  }

  /**
   * @return enumeration in subkeys of sub-table pointed by key, and empty if
   *         sub-table does not exist
   */
  public Enumeration keys(Object key) {
    final Hashtable<Object, Object> subtable = get(key);
    return new Enumeration() {
      private final Enumeration subkeys = subtable == null ? null : subtable
          .keys();
      private final Object nullKey = DoubleHashtable.this;

      public boolean hasMoreElements() {
        return subkeys == null ? false : subkeys.hasMoreElements();
      }
      public Object nextElement() {
        if (subkeys == null)
          throw new NoSuchElementException();
        Object subkey = subkeys.nextElement();
        return subkey == nullKey ? null : subkey;
      }
    };
  }

  /**
   * puts a value in sub-table specified by key and subkey.
   * 
   * @return previous value
   */
  public Object put(Object key, Object subKey, Object value) {
    // Make sure there exists a subtable
    Hashtable<Object, Object> subtable = get(key);
    if (subtable == null) {
      subtable = new Hashtable<Object, Object>();
      if (key == null)
        key = this;
      table.put(key, subtable);
    }

    // add entry and handle nulls
    if (subKey == null)
      subKey = this;
    if (value == null)
      value = this;
    Object old = subtable.get(subKey);
    subtable.put(subKey, value);

    // return previous entry
    return old == this ? null : old;
  }

  /**
   * removes value from sub-table specified by key and subkey.
   * 
   * @return previous value
   */
  public Object remove(Object key, Object subKey) {
    // look for subtable
    Hashtable<Object, Object> subtable = get(key);
    if (subtable == null)
      return null;

    // remove from subtable
    if (subKey == null)
      subKey = this;
    Object old = subtable.remove(subKey);

    // remove subtable if needed
    if (subtable.isEmpty()) {
      if (key == null)
        key = this;
      table.remove(key);
    }

    // return old value
    return old == this ? null : old;
  }

  /**
   * @return size of all tables
   */
  @Override
  public int size() {
    int size = 0;
    for (Enumeration<Object> e = table.keys(); e.hasMoreElements();) {
      Object key = e.nextElement();
      Hashtable<Object, Object> subtable = get(key);
      size += subtable.size();
    }
    return size;
  }

  /**
   * @return a string representation of the table
   */
  @Override
  public String toString() {
    return "DoubleHashtable@" + hashCode();
  }
}
