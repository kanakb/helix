package org.apache.helix.api.command;
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
import java.io.Serializable;
import java.util.Map;

import org.apache.helix.api.id.MemberId;

import com.google.common.collect.Maps;
/**
 * A member command which allows creation of cluster members
 */
abstract class HelixMemberCommand {
  private String hostName;
  private boolean instanceEnabled;
  private final MemberType type;
  private final MemberId memberId;
  private int port;
  private Map<String, Serializable> properties;
  
  /**
   * Member types which can be created
   *
   */
  protected enum MemberType{

     PARTICIPANT,
     
     CONTROLLER,
     
     SPECTATOR,
     
     ADMINISTRATOR
  }
  
  protected HelixMemberCommand(MemberId memberId, MemberType type){
    this.type = type;
    this.memberId = memberId;
  }
  
  /**
   * Sets the host name for the member
   * @param hostName the host name
   */
  public void setHostName(String hostName){
    this.hostName = hostName;
  }
  
  /**
   * Retrieves the host name for the member
   * @return the host name
   */
  public String getHostName(){
    return this.hostName;
  }
  
  /**
   * Not all members can have configurable ports, currently only participants project this property
   * @param port
   */
  protected void setPort(int port) {
    this.port = port;
  }
  
  /**
   * Port number for the member
   * @return port number
   */
  protected int getPort() {
    return this.port;
  }

  
  /**
   * Enables the instance
   * @param enabled <b>True</b>to enable, <b>False</b>to disable
   */
  public void setEnabled(boolean enabled){
    instanceEnabled = enabled;
  }
  
  /**
   * Checks if the instance is enabled
   * @return <b>True</b>to enable, <b>False</b>to disable
   */
  public boolean isEnabled(){
    return instanceEnabled;
  }
  
  /**
   * Retrieves the member type
   * @return @link org.apache.helix.api.command.HelixMemberCommand.MemberType
   */
  public MemberType getType(){
    return this.type;
  }
  
  public void addProperties(Map<String, Serializable> properties){
    this.properties = Maps.newHashMap(properties);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((hostName == null) ? 0 : hostName.hashCode());
    result = prime * result + ((memberId == null) ? 0 : memberId.hashCode());
    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    HelixMemberCommand other = (HelixMemberCommand) obj;
    if (hostName == null) {
      if (other.hostName != null)
        return false;
    } else if (!hostName.equals(other.hostName))
      return false;
    if (memberId == null) {
      if (other.memberId != null)
        return false;
    } else if (!memberId.equals(other.memberId))
      return false;
    if (properties == null) {
      if (other.properties != null)
        return false;
    } else if (!properties.equals(other.properties))
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  
}
