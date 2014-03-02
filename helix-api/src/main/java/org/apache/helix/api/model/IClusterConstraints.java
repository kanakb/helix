package org.apache.helix.api.model;

import java.util.Map;
import java.util.Set;

import org.apache.helix.api.id.ConstraintId;

/**
 * Attributes on which constraints operate
 */
public interface IClusterConstraints {

  public enum ConstraintAttribute {
    STATE,
    STATE_MODEL,
    MESSAGE_TYPE,
    TRANSITION,
    RESOURCE,
    PARTITION,
    INSTANCE,
    CONSTRAINT_VALUE
  }

  /**
   * Possible special values that constraint attributes can take
   */
  public enum ConstraintValue {
    ANY,
    N,
    R
  }

  /**
   * What is being constrained
   */
  public enum ConstraintType {
    STATE_CONSTRAINT,
    MESSAGE_CONSTRAINT
  }

  /**
   * Get the type of constraint this object represents
   * @return constraint type
   */
  public abstract ConstraintType getType();

  /**
   * add the constraint, overwrite existing one if constraint with same constraint-id already exists
   * @param constraintId unique constraint identifier
   * @param item the constraint as a {@link ConstraintItem}
   */
  public abstract <T extends IConstraintItem> void addConstraintItem(ConstraintId constraintId, T item);

  /**
   * add the constraint, overwrite existing one if constraint with same constraint-id already exists
   * @param constraintId unique constraint identifier
   * @param item the constraint as a {@link ConstraintItem}
   */
  public abstract <T extends IConstraintItem> void addConstraintItem(String constraintId, T item);

  /**
   * Add multiple constraint items.
   * @param items (constraint identifier, {@link ConstrantItem}) pairs
   */
  public abstract <T extends IConstraintItem> void addConstraintItems(Map<String, T> items);

  /**
   * remove a constraint-item
   * @param constraintId unique constraint identifier
   */
  public abstract void removeConstraintItem(ConstraintId constraintId);

  /**
   * remove a constraint-item
   * @param constraintId unique constraint identifier
   */
  public abstract void removeConstraintItem(String constraintId);

  /**
   * get a constraint-item
   * @param constraintId unique constraint identifier
   * @return {@link ConstraintItem} or null if not present
   */
  public abstract <T extends IConstraintItem> T getConstraintItem(ConstraintId constraintId);

  /**
   * get a constraint-item
   * @param constraintId unique constraint identifier
   * @return {@link ConstraintItem} or null if not present
   */
  public abstract <T extends IConstraintItem> T getConstraintItem(String constraintId);

  /**
   * return a set of constraints that match the attribute pairs
   * @param attributes (constraint scope, constraint string) pairs
   * @return a set of {@link ConstraintItem}s with matching attributes
   */
  public abstract <T extends IConstraintItem> Set<T> match(
      Map<ConstraintAttribute, String> attributes);

  /**
   * Get all constraint items in this collection of constraints
   * @return map of constraint id to constraint item
   */
  public abstract <T extends IConstraintItem> Map<ConstraintId, T> getConstraintItems();

  public abstract boolean isValid();

}
