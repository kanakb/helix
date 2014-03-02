package org.apache.helix.manager.zk;

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

import org.I0Itec.zkclient.DataUpdater;
import org.apache.helix.api.ZNRecord;
import org.apache.helix.api.config.State;
import org.apache.helix.api.id.PartitionId;
import org.apache.helix.api.id.SessionId;
import org.apache.helix.model.CurrentState;

/**
 * updater for carrying over last current states
 * @see HELIX-30: ZkHelixManager.carryOverPreviousCurrentState() should use a special merge logic
 *      because carryOver() is performed after addLiveInstance(). it's possible that carryOver()
 *      overwrites current-state updates performed by current session. so carryOver() should be
 *      performed only when current-state is empty for the partition
 */
class CurStateCarryOverUpdater implements DataUpdater<ZNRecord> {
  final String _curSessionId;
  final String _initState;
  final CurrentState _lastCurState;

  public CurStateCarryOverUpdater(String curSessionId, String initState, CurrentState lastCurState) {
    if (curSessionId == null || initState == null || lastCurState == null) {
      throw new IllegalArgumentException(
          "missing curSessionId|initState|lastCurState for carry-over");
    }
    _curSessionId = curSessionId;
    _initState = initState;
    _lastCurState = lastCurState;
  }

  @Override
  public ZNRecord update(ZNRecord currentData) {
    CurrentState curState = null;
    if (currentData == null) {
      curState = new CurrentState(_lastCurState.getId());
      // copy all simple fields settings and overwrite session-id to current session
      curState.getRecord().setSimpleFields(_lastCurState.getRecord().getSimpleFields());
      curState.setSessionId(SessionId.from(_curSessionId));
    } else {
      curState = new CurrentState(currentData);
    }

    for (PartitionId partitionId : _lastCurState.getTypedPartitionStateMap().keySet()) {
      // carry-over only when current-state not exist
      if (curState.getState(partitionId) == null) {
        curState.setState(partitionId, State.from(_initState));
      }
    }
    return curState.getRecord();
  }

}
