package org.apache.helix.api.model.statemachine;

import org.apache.helix.api.id.PartitionId;

/**
 * The state model factory which creates a state model
 */
public interface StateModelFactory {

  void newStateModel(PartitionId partitionId);
}
