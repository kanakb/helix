package org.apache.helix.api.config;

import java.util.HashSet;
import java.util.Set;

import org.apache.helix.api.id.ParticipantId;
import org.apache.helix.api.id.PartitionId;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Configuration properties of a Helix participant
 */
public class ParticipantConfig {
  private final ParticipantId _id;
  private final String _hostName;
  private final int _port;
  private final boolean _isEnabled;
  private final Set<PartitionId> _disabledPartitions;
  private final Set<String> _tags;
  private final UserConfig _userConfig;

  /**
   * Initialize a participant configuration. Also see ParticipantConfig.Builder
   * @param id participant id
   * @param hostName host where participant can be reached
   * @param port port to use to contact participant
   * @param isEnabled true if enabled, false if disabled
   * @param disabledPartitions set of partitions, if any to disable on this participant
   * @param tags tags to set for the participant
   */
  public ParticipantConfig(ParticipantId id, String hostName, int port, boolean isEnabled,
      Set<PartitionId> disabledPartitions, Set<String> tags, UserConfig userConfig) {
    _id = id;
    _hostName = hostName;
    _port = port;
    _isEnabled = isEnabled;
    _disabledPartitions = ImmutableSet.copyOf(disabledPartitions);
    _tags = ImmutableSet.copyOf(tags);
    _userConfig = userConfig;
  }

  /**
   * Get the host name of the participant
   * @return host name, or null if not applicable
   */
  public String getHostName() {
    return _hostName;
  }

  /**
   * Get the port of the participant
   * @return port number, or -1 if not applicable
   */
  public int getPort() {
    return _port;
  }

  /**
   * Get if the participant is enabled
   * @return true if enabled or false otherwise
   */
  public boolean isEnabled() {
    return _isEnabled;
  }

  /**
   * Get disabled partition id's
   * @return set of disabled partition id's, or empty set if none
   */
  public Set<PartitionId> getDisabledPartitions() {
    return _disabledPartitions;
  }

  /**
   * Get tags
   * @return set of tags
   */
  public Set<String> getTags() {
    return _tags;
  }

  /**
   * Check if participant has a tag
   * @param tag tag to check
   * @return true if tagged, false otherwise
   */
  public boolean hasTag(String tag) {
    return _tags.contains(tag);
  }

  /**
   * Get user-specified configuration properties of this participant
   * @return UserConfig properties
   */
  public UserConfig getUserConfig() {
    return _userConfig;
  }

  /**
   * Get the participant id
   * @return ParticipantId
   */
  public ParticipantId getId() {
    return _id;
  }

  /**
   * Update context for a ParticipantConfig
   */
  public static class Delta {
    private enum Fields {
      HOST_NAME,
      PORT,
      ENABLED,
      USER_CONFIG
    }

    private Set<Fields> _updateFields;
    private Set<String> _removedTags;
    private Set<PartitionId> _removedDisabledPartitions;
    private Builder _builder;

    /**
     * Instantiate the delta for a participant config
     * @param participantId the participant to update
     */
    public Delta(ParticipantId participantId) {
      _updateFields = Sets.newHashSet();
      _removedTags = Sets.newHashSet();
      _removedDisabledPartitions = Sets.newHashSet();
      _builder = new Builder(participantId);
    }

    /**
     * Set the participant host name
     * @param hostName reachable host when live
     * @return Delta
     */
    public Delta setHostName(String hostName) {
      _builder.hostName(hostName);
      _updateFields.add(Fields.HOST_NAME);
      return this;
    }

    /**
     * Set the participant port
     * @param port port number
     * @return Delta
     */
    public Delta setPort(int port) {
      _builder.port(port);
      _updateFields.add(Fields.PORT);
      return this;
    }

    /**
     * Set the enabled status of the participant
     * @param isEnabled true if enabled, false if disabled
     * @return Delta
     */
    public Delta setEnabled(boolean isEnabled) {
      _builder.enabled(isEnabled);
      _updateFields.add(Fields.ENABLED);
      return this;
    }

    /**
     * Set the user configuration
     * @param userConfig user-specified properties
     * @return Delta
     */
    public Delta setUserConfig(UserConfig userConfig) {
      _builder.userConfig(userConfig);
      _updateFields.add(Fields.USER_CONFIG);
      return this;
    }

    /**
     * Add an new tag for this participant
     * @param tag the tag to add
     * @return Delta
     */
    public Delta addTag(String tag) {
      _builder.addTag(tag);
      return this;
    }

    /**
     * Remove a tag for this participant
     * @param tag the tag to remove
     * @return Delta
     */
    public Delta removeTag(String tag) {
      _removedTags.add(tag);
      return this;
    }

    /**
     * Add a partition to disable for this participant
     * @param partitionId the partition to disable
     * @return Delta
     */
    public Delta addDisabledPartition(PartitionId partitionId) {
      _builder.addDisabledPartition(partitionId);
      return this;
    }

    /**
     * Remove a partition from the disabled set for this participant
     * @param partitionId the partition to enable
     * @return Delta
     */
    public Delta removeDisabledPartition(PartitionId partitionId) {
      _removedDisabledPartitions.add(partitionId);
      return this;
    }

    /**
     * Create a ParticipantConfig that is the combination of an existing ParticipantConfig and this
     * delta
     * @param orig the original ParticipantConfig
     * @return updated ParticipantConfig
     */
    public ParticipantConfig mergeInto(ParticipantConfig orig) {
      ParticipantConfig deltaConfig = _builder.build();
      Builder builder =
          new Builder(orig.getId()).hostName(orig.getHostName()).port(orig.getPort())
              .enabled(orig.isEnabled()).userConfig(orig.getUserConfig());
      for (Fields field : _updateFields) {
        switch (field) {
        case HOST_NAME:
          builder.hostName(deltaConfig.getHostName());
          break;
        case PORT:
          builder.port(deltaConfig.getPort());
          break;
        case ENABLED:
          builder.enabled(deltaConfig.isEnabled());
          break;
        case USER_CONFIG:
          builder.userConfig(deltaConfig.getUserConfig());
          break;
        }
      }
      Set<String> tags = Sets.newHashSet(orig.getTags());
      tags.addAll(deltaConfig.getTags());
      tags.removeAll(_removedTags);
      for (String tag : tags) {
        builder.addTag(tag);
      }
      Set<PartitionId> disabledPartitions = Sets.newHashSet(orig.getDisabledPartitions());
      disabledPartitions.addAll(deltaConfig.getDisabledPartitions());
      disabledPartitions.removeAll(_removedDisabledPartitions);
      for (PartitionId partitionId : disabledPartitions) {
        builder.addDisabledPartition(partitionId);
      }
      return builder.build();
    }
  }

  /**
   * Assemble a participant
   */
  public static class Builder {
    private final ParticipantId _id;
    private String _hostName;
    private int _port;
    private boolean _isEnabled;
    private final Set<PartitionId> _disabledPartitions;
    private final Set<String> _tags;
    private UserConfig _userConfig;

    /**
     * Build a participant with a given id
     * @param id participant id
     */
    public Builder(ParticipantId id) {
      _id = id;
      _disabledPartitions = new HashSet<PartitionId>();
      _tags = new HashSet<String>();
      _isEnabled = true;
      _userConfig = new UserConfig(Scope.participant(id));
    }

    /**
     * Set the participant host name
     * @param hostName reachable host when live
     * @return Builder
     */
    public Builder hostName(String hostName) {
      _hostName = hostName;
      return this;
    }

    /**
     * Set the participant port
     * @param port port number
     * @return Builder
     */
    public Builder port(int port) {
      _port = port;
      return this;
    }

    /**
     * Set whether or not the participant is enabled
     * @param isEnabled true if enabled, false otherwise
     * @return Builder
     */
    public Builder enabled(boolean isEnabled) {
      _isEnabled = isEnabled;
      return this;
    }

    /**
     * Add a partition to disable for this participant
     * @param partitionId the partition to disable
     * @return Builder
     */
    public Builder addDisabledPartition(PartitionId partitionId) {
      _disabledPartitions.add(partitionId);
      return this;
    }

    /**
     * Add an arbitrary tag for this participant
     * @param tag the tag to add
     * @return Builder
     */
    public Builder addTag(String tag) {
      _tags.add(tag);
      return this;
    }

    /**
     * Set the user configuration
     * @param userConfig user-specified properties
     * @return Builder
     */
    public Builder userConfig(UserConfig userConfig) {
      _userConfig = userConfig;
      return this;
    }

    /**
     * Assemble the participant
     * @return instantiated Participant
     */
    public ParticipantConfig build() {
      return new ParticipantConfig(_id, _hostName, _port, _isEnabled, _disabledPartitions, _tags,
          _userConfig);
    }
  }
}
