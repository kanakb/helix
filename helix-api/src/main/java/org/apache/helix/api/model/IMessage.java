package org.apache.helix.api.model;

public interface IMessage {
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

  public abstract int getTimeout();
}
