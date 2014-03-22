package org.apache.helix.tools;

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

import java.util.List;
import java.util.Map;

import org.apache.helix.api.model.ZNRecord;

public class ZnodeValue {
  public String _singleValue;
  public List<String> _listValue;
  public Map<String, String> _mapValue;
  public ZNRecord _znodeValue;

  public ZnodeValue() {
  }

  public ZnodeValue(String value) {
    _singleValue = value;
  }

  public ZnodeValue(List<String> value) {
    _listValue = value;
  }

  public ZnodeValue(Map<String, String> value) {
    _mapValue = value;
  }

  public ZnodeValue(ZNRecord value) {
    _znodeValue = value;
  }

  @Override
  public String toString() {
    if (_singleValue != null) {
      return _singleValue;
    } else if (_listValue != null) {
      return _listValue.toString();
    } else if (_mapValue != null) {
      return _mapValue.toString();
    } else if (_znodeValue != null) {
      return _znodeValue.toString();
    }

    return "null";
  }
}
