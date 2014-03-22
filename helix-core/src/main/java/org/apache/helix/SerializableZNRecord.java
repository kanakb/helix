package org.apache.helix;

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
import org.apache.helix.api.model.ZNRecord;
import org.apache.helix.manager.zk.serializer.PayloadSerializer;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * @author osgigeek
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = Inclusion.NON_NULL)
public class SerializableZNRecord extends ZNRecord {
  private PayloadSerializer _serializer;
  
  /**
   * Initialize with an identifier
   * @param id
   */
  @JsonCreator
  public SerializableZNRecord(@JsonProperty("id") String id) {
    super(id);
  }

  /**
   * Initialize with a pre-populated ZNRecord
   * @param record
   */
  public SerializableZNRecord(ZNRecord record) {
    this(record, record.getId());
  }

  /**
   * Initialize with a pre-populated ZNRecord, overwriting the identifier
   * @param record
   * @param id
   */
  public SerializableZNRecord(ZNRecord record, String id) {
    this(id);
  }
  
  /**
   * Set a custom {@link PayloadSerializer} to allow including arbitrary data
   * @param serializer
   */
  @JsonIgnore(true)
  public void setPayloadSerializer(PayloadSerializer serializer) {
    _serializer = serializer;
  }

  /**
   * Get the {@link PayloadSerializer} that will serialize/deserialize the payload
   * @return serializer
   */
  @JsonIgnore(true)
  public PayloadSerializer getPayloadSerializer() {
    return _serializer;
  }
  
  /**
   * Set a typed payload that will be serialized and persisted.
   * @param payload
   */
  @JsonIgnore(true)
  public <T> void setPayload(T payload) {
    if (_serializer != null && payload != null) {
      setRawPayload(_serializer.serialize(payload));
    } else {
      setRawPayload(null);
    }
  }

  /**
   * Get a typed deserialized payload
   * @param clazz
   * @return
   */
  @JsonIgnore(true)
  public <T> T getPayload(Class<T> clazz) {
    if (_serializer != null && getRawPayload() != null) {
      return _serializer.deserialize(clazz, getRawPayload());
    } else {
      return null;
    }
  }
}
