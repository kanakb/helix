package org.apache.helix.api.model.ipc;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.helix.api.id.MessageId;
import org.apache.helix.api.id.PartitionId;
import org.apache.helix.api.id.ResourceId;
import org.apache.helix.api.id.SessionId;
import org.apache.helix.api.id.StateModelDefinitionId;
import org.apache.helix.api.id.StateModelFactoryId;
import org.apache.helix.api.model.HelixProperty;
import org.apache.helix.api.model.ZNRecord;
import org.apache.helix.api.model.statemachine.State;

import com.google.common.collect.Lists;

/**
 * Messages sent internally among nodes in the system to respond to changes in state.
 */
public class Message extends HelixProperty {

  /**
   * The major categories of messages that are sent
   */
  public enum MessageType {
    STATE_TRANSITION,
    SCHEDULER_MSG,
    USER_DEFINE_MSG,
    CONTROLLER_MSG,
    TASK_REPLY,
    NO_OP,
    PARTICIPANT_ERROR_REPORT
  };

  /**
   * Properties attached to Messages
   */
  public enum Attributes {
    MSG_ID,
    SRC_SESSION_ID,
    TGT_SESSION_ID,
    SRC_NAME,
    TGT_NAME,
    SRC_INSTANCE_TYPE,
    MSG_STATE,
    PARTITION_NAME,
    RESOURCE_NAME,
    FROM_STATE,
    TO_STATE,
    STATE_MODEL_DEF,
    CREATE_TIMESTAMP,
    READ_TIMESTAMP,
    EXECUTE_START_TIMESTAMP,
    MSG_TYPE,
    MSG_SUBTYPE,
    CORRELATION_ID,
    MESSAGE_RESULT,
    EXE_SESSION_ID,
    TIMEOUT,
    RETRY_COUNT,
    STATE_MODEL_FACTORY_NAME,
    BUCKET_SIZE,
    PARENT_MSG_ID, // used for group message mode
    INNER_MESSAGE
  }

  /**
   * The current processed state of the message
   */
  public enum MessageState {
    NEW,
    READ, // not used
    UNPROCESSABLE // get exception when create handler
  }

  /**
   * Compares the creation time of two Messages
   */
  public static final Comparator<Message> CREATE_TIME_COMPARATOR = new Comparator<Message>() {
    @Override
    public int compare(Message m1, Message m2) {
      // long t1 = m1.getCreateTimeStamp();
      // long t2 = m2.getCreateTimeStamp();
      return (int) (m1.getCreateTimeStamp() - m2.getCreateTimeStamp());
    }
  };

  public static final String CONTROLLER_MSG_ID = "controllerMsgId";
  private String instanceType;

  private String messageSource;

  private MessageState messageState;

  private String targetInstance;

  private String partitionName;

  private String messageId;

  private String fromState;

  private String toState;

  private String stateModelDefinition;

  private long readTimeStamp;

  private long executeStartTimeStamp;

  private long createTimeStamp;

  private String correlationId;

  private int executionTimeout;

  private int retryCount;

  private Map<String, String> resultMap;

  private String stateModelFactoryName;

  private int bucketSize;

  private String resourceName;

  /**
   * Instantiate a message
   * @param type the message category
   * @param msgId unique message identifier
   */
  public Message(MessageType type, MessageId msgId) {
    this(type.toString(), msgId);
  }

  /**
   * Instantiate a message
   * @param type the message category
   * @param msgId unique message identifier
   */
  public Message(MessageType type, String msgId) {
    this(type, MessageId.from(msgId));
  }

  /**
   * Instantiate a message
   * @param type {@link MessageType} as a string or a custom message type
   * @param msgId unique message identifier
   */
  public Message(String type, MessageId msgId) {
    super(new ZNRecord(msgId.toString()));
    _record.setSimpleField(Attributes.MSG_TYPE.toString(), type);
    setMessageId(msgId);
    setMsgState(MessageState.NEW);
    _record.setLongField(Attributes.CREATE_TIMESTAMP.toString(), new Date().getTime());
  }

  /**
   * Instantiate a message
   * @param type {@link MessageType} as a string or a custom message type
   * @param msgId unique message identifier
   */
  public Message(String type, String msgId) {
    this(type, MessageId.from(msgId));
  }

  /**
   * Instantiate a message with a new id
   * @param record a ZNRecord corresponding to a message
   * @param id unique message identifier
   * @deprecated Creating message from a ZNRecord
   *             will be removed from API, the code will be moved to SPI
   */
  @Deprecated
  public Message(ZNRecord record, MessageId id) {
    super(new ZNRecord(record, id.toString()));
    setMessageId(id);
  }

  /**
   * Instantiate a message with a new id
   * @param record a ZNRecord corresponding to a message
   * @param id unique message identifier
   */
  public Message(ZNRecord record, String id) {
    this(record, MessageId.from(id));
  }

  /**
   * Instantiate a message
   * @param record a ZNRecord corresponding to a message
   */
  public Message(ZNRecord record) {
    super(record);
    if (getMsgState() == null) {
      setMsgState(MessageState.NEW);
    }
    if (getCreateTimeStamp() == 0) {
      _record.setLongField(Attributes.CREATE_TIMESTAMP.toString(), new Date().getTime());
    }
  }

  /**
   * Convert a string map to a message
   * @param msgStrMap
   * @return message
   */
  public static Message toMessage(Map<String, String> msgStrMap) {
    String msgId = msgStrMap.get(Attributes.MSG_ID.name());
    if (msgId == null) {
      throw new IllegalArgumentException("Missing msgId in message string map: " + msgStrMap);
    }

    ZNRecord record = new ZNRecord(msgId);
    record.getSimpleFields().putAll(msgStrMap);
    return new Message(record);
  }

  /**
   * Set the time that the message was created
   * @param timestamp a UNIX timestamp
   */
  public void setCreateTimeStamp(long timestamp) {
    _record.setLongField(Attributes.CREATE_TIMESTAMP.toString(), timestamp);
  }

  /**
   * Set a subtype of the message
   * @param subType name of the subtype
   */
  public void setMsgSubType(String subType) {
    _record.setSimpleField(Attributes.MSG_SUBTYPE.toString(), subType);
  }

  /**
   * Get the subtype of the message
   * @return the subtype name, or null
   */
  public String getMsgSubType() {
    return _record.getSimpleField(Attributes.MSG_SUBTYPE.toString());
  }

  /**
   * Set the type of this message
   * @param type {@link MessageType}
   */
  void setMsgType(MessageType type) {
    _record.setSimpleField(Attributes.MSG_TYPE.toString(), type.toString());
  }

  /**
   * Get the type of this message
   * @return String {@link MessageType} or a custom message type
   */
  public String getMsgType() {
    return _record.getSimpleField(Attributes.MSG_TYPE.toString());
  }

  /**
   * Get the session identifier of the destination node
   * @return session identifier
   */
  public SessionId getTypedTgtSessionId() {
    return SessionId.from(getTgtSessionId());
  }

  /**
   * Get the session identifier of the destination node
   * @return session identifier
   */
  public String getTgtSessionId() {
    return _record.getSimpleField(Attributes.TGT_SESSION_ID.toString());
  }

  /**
   * Set the session identifier of the destination node
   * @param tgtSessionId session identifier
   */
  public void setTgtSessionId(SessionId tgtSessionId) {
    if (tgtSessionId != null) {
      setTgtSessionId(tgtSessionId.toString());
    }
  }

  /**
   * Set the session identifier of the destination node
   * @param tgtSessionId session identifier
   */
  public void setTgtSessionId(String tgtSessionId) {
    _record.setSimpleField(Attributes.TGT_SESSION_ID.toString(), tgtSessionId);
  }

  /**
   * Get the session identifier of the source node
   * @return session identifier
   */
  public SessionId getTypedSrcSessionId() {
    return SessionId.from(getSrcSessionId());
  }

  /**
   * Get the session identifier of the source node
   * @return session identifier
   */
  public String getSrcSessionId() {
    return _record.getSimpleField(Attributes.SRC_SESSION_ID.toString());
  }

  /**
   * Set the session identifier of the source node
   * @param srcSessionId session identifier
   */
  public void setSrcSessionId(SessionId srcSessionId) {
    if (srcSessionId != null) {
      setSrcSessionId(srcSessionId.toString());
    }
  }

  /**
   * Set the session identifier of the source node
   * @param srcSessionId session identifier
   */
  public void setSrcSessionId(String srcSessionId) {
    _record.setSimpleField(Attributes.SRC_SESSION_ID.toString(), srcSessionId);
  }

  /**
   * Get the session identifier of the node that executes the message
   * @return session identifier
   */
  public SessionId getTypedExecutionSessionId() {
    return SessionId.from(getExecutionSessionId());
  }

  /**
   * Get the session identifier of the node that executes the message
   * @return session identifier
   */
  public String getExecutionSessionId() {
    return _record.getSimpleField(Attributes.EXE_SESSION_ID.toString());
  }

  /**
   * Set the session identifier of the node that executes the message
   * @param exeSessionId session identifier
   */
  public void setExecuteSessionId(SessionId exeSessionId) {
    if (exeSessionId != null) {
      setExecuteSessionId(exeSessionId.toString());
    }
  }

  /**
   * Set the session identifier of the node that executes the message
   * @param exeSessionId session identifier
   */
  public void setExecuteSessionId(String exeSessionId) {
    _record.setSimpleField(Attributes.EXE_SESSION_ID.toString(), exeSessionId);
  }

  /**
   * Get the instance from which the message originated
   * @return the instance name
   */
  public String getMsgSrc() {
    return _record.getSimpleField(Attributes.SRC_NAME.toString());
  }

  /**
   * Set the type of instance that the source node is
   * @param type 
   */
  public void setSrcInstanceType(String type) {
    instanceType = type;
  }

  /**
   * Get the type of instance that the source is
   * @return String
   */
  public String getSrcInstanceType() {
    return instanceType;
  }

  /**
   * Set the name of the source instance
   * @param msgSrc instance name
   */
  public void setSrcName(String msgSrc) {
    messageSource = msgSrc;
  }

  /**
   * Get the name of the target instance
   * @return instance name
   */
  public String getTgtName() {
    return targetInstance;
  }

  /**
   * Set the current state of the message
   * @param msgState {@link MessageState}
   */
  public void setMsgState(MessageState msgState) { // HACK: The "tolowerCase()" call is to make the
                                                   // change backward compatible
    messageState = msgState;
  }

  /**
   * Get the current state of the message
   * @return {@link MessageState}
   */
  public MessageState getMsgState() {
    // HACK: The "toUpperCase()" call is to make the change backward compatible
    return messageState;
  }

  /**
   * Set the id of the partition this message concerns
   * @param partitionId
   */
  public void setPartitionId(PartitionId partitionId) {
    if (partitionId != null) {
      setPartitionName(partitionId.toString());
    }
  }

  /**
   * Set the id of the partition this message concerns
   * @param partitionId
   */
  public void setPartitionName(String partitionName) {
    this.partitionName = partitionName;
  }

  /**
   * Get the unique identifier of this message
   * @return message identifier
   */
  public MessageId getMessageId() {
    return MessageId.from(getMsgId());
  }

  /**
   * Get the unique identifier of this message
   * @return message identifier
   */
  public String getMsgId() {
    return messageId;
  }

  /**
   * Set the unique identifier of this message
   * @param msgId message identifier
   */
  public void setMessageId(MessageId msgId) {
    if (msgId != null) {
      setMsgId(msgId.toString());
    }
  }

  /**
   * Set the unique identifier of this message
   * @param msgId message identifier
   */
  public void setMsgId(String msgId) {
    messageId = msgId;
  }

  /**
   * Set the "from state" for transition-related messages
   * @param state the state
   */
  public void setFromState(State state) {
    if (state != null) {
      setFromState(state.toString());
    }
  }

  /**
   * Set the "from state" for transition-related messages
   * @param state the state
   */
  public void setFromState(String state) {
    fromState = state;
  }

  /**
   * Get the "from-state" for transition-related messages
   * @return state, or null for other message types
   */
  public State getTypedFromState() {
    return State.from(getFromState());
  }

  /**
   * Get the "from-state" for transition-related messages
   * @return state, or null for other message types
   */
  public String getFromState() {
    return fromState;
  }

  /**
   * Set the "to state" for transition-related messages
   * @param state the state
   */
  public void setToState(State state) {
    if (state != null) {
      setToState(state.toString());
    }
  }

  /**
   * Set the "to state" for transition-related messages
   * @param state the state
   */
  public void setToState(String state) {
    toState = state;
  }

  /**
   * Get the "to state" for transition-related messages
   * @return state, or null for other message types
   */
  public State getTypedToState() {
    return State.from(getToState());
  }

  /**
   * Get the "to state" for transition-related messages
   * @return state, or null for other message types
   */
  public String getToState() {
    return toState;
  }

  /**
   * Set the instance for which this message is targeted
   * @param msgTgt instance name
   */
  public void setTgtName(String msgTgt) {
    this.targetInstance = msgTgt;
  }

  /**
   * Check for debug mode
   * @return true if enabled, false if disabled
   */
  public Boolean getDebug() {
    return false;
  }

  /**
   * Get the generation that this message corresponds to
   * @return generation number
   */
  public Integer getGeneration() {
    return 1;
  }

  /**
   * Set the resource associated with this message
   * @param resourceId resource name to set
   */
  public void setResourceId(ResourceId resourceId) {
    if (resourceId != null) {
      setResourceName(resourceId.toString());
    }
  }

  /**
   * Set the resource associated with this message
   * @param resourceName resource name to set
   */
  public void setResourceName(String resourceName) {
    this.resourceName = resourceName; 
  }

  /**
   * Get the resource associated with this message
   * @return resource name
   */
  public ResourceId getResourceId() {
    return ResourceId.from(getResourceName());
  }

  /**
   * Get the resource associated with this message
   * @return resource name
   */
  public String getResourceName() {
    return resourceName;
  }

  /**
   * Get the resource partition associated with this message
   * @return partition id
   */
  public PartitionId getPartitionId() {
    return PartitionId.from(getPartitionName());
  }

  /**
   * Get the resource partition associated with this message
   * @return partition id
   */
  public String getPartitionName() {
    return this.partitionName;
  }

  /**
   * Get the state model definition name
   * @return a String reference to the state model definition, e.g. "MasterSlave"
   */
  public String getStateModelDef() {
    return stateModelDefinition;
  }

  /**
   * Get the state model definition id
   * @return a reference to the state model definition
   */
  public StateModelDefinitionId getStateModelDefId() {
    return StateModelDefinitionId.from(getStateModelDef());
  }

  /**
   * Set the state model definition
   * @param stateModelDefName a reference to the state model definition, e.g. "MasterSlave"
   */
  public void setStateModelDef(StateModelDefinitionId stateModelDefId) {
    if (stateModelDefId != null) {
      setStateModelDef(stateModelDefId.toString());
    }
  }

  /**
   * Set the state model definition
   * @param stateModelDefName a reference to the state model definition, e.g. "MasterSlave"
   */
  public void setStateModelDef(String stateModelDefId) {
    this.stateModelDefinition = stateModelDefId;
  }

  /**
   * Set the time that this message was read
   * @param time UNIX timestamp
   */
  public void setReadTimeStamp(long time) {
    readTimeStamp = time;
  }

  /**
   * Set the time that the instance executes tasks as instructed by this message
   * @param time UNIX timestamp
   */
  public void setExecuteStartTimeStamp(long time) {
    executeStartTimeStamp = time;
  }

  /**
   * Get the time that this message was read
   * @return UNIX timestamp
   */
  public long getReadTimeStamp() {
    return this.readTimeStamp;
  }

  /**
   * Get the time that execution occurred as a result of this message
   * @return UNIX timestamp
   */
  public long getExecuteStartTimeStamp() {
    return this.executeStartTimeStamp;
  }

  /**
   * Get the time that this message was created
   * @return UNIX timestamp
   */
  public long getCreateTimeStamp() {
    return this.createTimeStamp;
  }

  /**
   * Set a unique identifier that others can use to refer to this message in replies
   * @param correlationId a unique identifier, usually randomly generated
   */
  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  /**
   * Get the unique identifier attached to this message for reply matching
   * @return the correlation identifier
   */
  public String getCorrelationId() {
    return this.correlationId;
  }

  /**
   * Get the time to wait before stopping execution of this message
   * @return the timeout in ms, or -1 indicating no timeout
   */
  public int getExecutionTimeout() {
    return this.executionTimeout;
  }

  /**
   * Set the time to wait before stopping execution of this message
   * @param timeout the timeout in ms, or -1 indicating no timeout
   */
  public void setExecutionTimeout(int timeout) {
    this.executionTimeout = timeout;
  }

  /**
   * Set the number of times to retry message handling on timeouts
   * @param retryCount maximum number of retries
   */
  public void setRetryCount(int retryCount) {
    this.retryCount = retryCount;
  }

  /**
   * Get the number of times to retry message handling on timeouts
   * @return maximum number of retries
   */
  public int getRetryCount() {
    return this.retryCount;
  }

  /**
   * Get the results of message execution
   * @return map of result property and value pairs
   */
  public Map<String, String> getResultMap() {
    return resultMap;
  }

  /**
   * Set the results of message execution
   * @param resultMap map of result property and value pairs
   */
  public void setResultMap(Map<String, String> resultMap) {
    this.resultMap = resultMap;
  }

  /**
   * Get the state model factory associated with this message
   * @return the name of the factory
   */
  public String getStateModelFactoryName() {
    return stateModelFactoryName;
  }

  /**
   * Set the state model factory associated with this message
   * @param factoryName the name of the factory
   */
  public void setStateModelFactoryName(String factoryName) {
    this.stateModelFactoryName= factoryName;
  }

  /**
   * Set the state model factory associated with this message
   * @param factoryName the name of the factory
   */
  public void setStateModelFactoryId(StateModelFactoryId factoryId) {
    if (factoryId != null) {
      setStateModelFactoryName(factoryId.toString());
    } else {
      setStateModelFactoryName(StateModelFactoryId.DEFAULT_STATE_MODEL_FACTORY);
    }
  }

  // TODO: remove this. impl in HelixProperty
  @Override
  public int getBucketSize() {
    return bucketSize;
  }

  @Override
  public void setBucketSize(int bucketSize) {
    if (bucketSize > 0) {
      this.bucketSize = bucketSize;
    }
  }

  /**
   * Add or change a message attribute
   * @param attr {@link Attributes} attribute name
   * @param val attribute value
   */
  public void setAttribute(Attributes attr, String val) {
    _record.setSimpleField(attr.toString(), val);
  }

  /**
   * Get the value of an attribute
   * @param attr {@link Attribute}
   * @return attribute value
   */
  public String getAttribute(Attributes attr) {
    return _record.getSimpleField(attr.toString());
  }

  /**
   * Create a reply based on an incoming message
   * @param srcMessage the incoming message
   * @param instanceName the instance that is the source of the reply
   * @param taskResultMap the result of executing the incoming message
   * @return the reply Message
   */
  public static Message createReplyMessage(Message srcMessage, String instanceName,
      Map<String, String> taskResultMap) {
    if (srcMessage.getCorrelationId() == null) {
      throw new HelixMessagingException("Message " + srcMessage.getMessageId()
          + " does not contain correlation id");
    }
    Message replyMessage =
        new Message(MessageType.TASK_REPLY, MessageId.from(UUID.randomUUID().toString()));
    replyMessage.setCorrelationId(srcMessage.getCorrelationId());
    replyMessage.setResultMap(taskResultMap);
    replyMessage.setTgtSessionId(SessionId.from("*"));
    replyMessage.setMsgState(MessageState.NEW);
    replyMessage.setSrcName(instanceName);
    if (srcMessage.getSrcInstanceType().equals("Controller")) {
      replyMessage.setTgtName("Controller");
    } else {
      replyMessage.setTgtName(srcMessage.getMsgSrc());
    }
    return replyMessage;
  }

  /**
   * Add a partition to a collection of partitions associated with this message
   * @param partitionName the partition name to add
   */
  public void addPartitionName(String partitionName) {
    if (_record.getListField(Attributes.PARTITION_NAME.toString()) == null) {
      _record.setListField(Attributes.PARTITION_NAME.toString(), new ArrayList<String>());
    }

    List<String> partitionNames = _record.getListField(Attributes.PARTITION_NAME.toString());
    if (!partitionNames.contains(partitionName)) {
      partitionNames.add(partitionName);
    }
  }

  /**
   * Get a list of partitions associated with this message
   * @return list of partition ids
   */
  public List<PartitionId> getPartitionIds() {
    List<String> partitionNames = _record.getListField(Attributes.PARTITION_NAME.toString());
    if (partitionNames == null) {
      return Collections.emptyList();
    }
    List<PartitionId> partitionIds = Lists.newArrayList();
    for (String partitionName : partitionNames) {
      partitionIds.add(PartitionId.from(partitionName));
    }
    return partitionIds;
  }

  /**
   * Get a list of partitions associated with this message
   * @return list of partition ids
   */
  public List<String> getPartitionNames() {
    return _record.getListField(Attributes.PARTITION_NAME.toString());
  }

  /**
   * Check if this message is targetted for a controller
   * @return true if this is a controller message, false otherwise
   */
  public boolean isControlerMsg() {
    return getTgtName().equalsIgnoreCase("controller");
  }

  /**
   * Get timeout
   * @return timeout or -1 if not available
   */
  public int getTimeout() {
    String timeoutStr = _record.getSimpleField(Attributes.TIMEOUT.name());
    int timeout = -1;
    if (timeoutStr != null) {
      try {
        timeout = Integer.parseInt(timeoutStr);
      } catch (Exception e) {
        // ignore
      }
    }
    return timeout;
  }

  /**
   * Get controller message id, used for scheduler-task-queue state model only
   * @return controller message id
   */
  public String getControllerMessageId() {
    return _record.getSimpleField(CONTROLLER_MSG_ID);
  }

  /**
   * Set an inner message
   * @param inner message
   */
  public void setInnerMessage(Message message) {
    _record.setMapField(Attributes.INNER_MESSAGE.name(), message.getRecord().getSimpleFields());
  }

  /**
   * Set the cluster event name generating this message
   * @param event cluster event
   */
  public void setClusterEventName(String eventName) {
    _record.setSimpleField("ClusterEventName", eventName);
  }

  /**
   * Get the cluster event name generating this message
   * @param the cluster event event name
   */
  public String getClusterEventName() {
    return _record.getSimpleField("ClusterEventName");
  }

  private boolean isNullOrEmpty(String data) {
    return data == null || data.length() == 0 || data.trim().length() == 0;
  }

  @Override
  public boolean isValid() {
    // TODO: refactor message to state transition message and task-message and
    // implement this function separately

    if (getMsgType().equals(MessageType.STATE_TRANSITION.toString())) {
      boolean isNotValid =
          isNullOrEmpty(getTgtName()) || isNullOrEmpty(getPartitionId().toString())
              || isNullOrEmpty(getResourceId().toString()) || isNullOrEmpty(getStateModelDef())
              || isNullOrEmpty(getTypedToState().toString())
              || isNullOrEmpty(getStateModelFactoryName())
              || isNullOrEmpty(getTypedFromState().toString());

      return !isNotValid;
    }

    return true;
  }
}
