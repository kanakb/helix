package org.apache.helix.api.query;

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
import java.util.List;
import java.util.Map;

import org.apache.helix.api.command.HelixMemberType;
import org.apache.helix.api.id.ClusterId;
import org.apache.helix.api.id.MemberId;
import org.apache.helix.api.id.ResourceId;

/**
 * Query builder factory which allows query creation for
 * members or resources
 */
public class HelixQueryBuilderFactory {

  /**
   * Creates a query builder for member queries
   * @return HelixMemberQueryBuilder
   */
  public static final HelixMemberQueryBuilder createMemberQueryBuilder() {
    return new HelixMemberQueryBuilder();
  }

  /**
   * Creates a query builder for resource queries
   * @return HelixResourceQueryBuilder
   */
  public static final HelixResourceQueryBuilder createResourceQueryBuilder() {
    return new HelixResourceQueryBuilder();
  }

  /**
   * A query builder for resource queries
   */
  public static class HelixResourceQueryBuilder {
    HelixResourceQueryBuilder() {

    }

    /**
     * Adds a cluster id to the query
     * @param id the cluster to restrict the resource search to
     * @return HelixResourceQueryBuilder
     */
    public HelixResourceQueryBuilder withClusterId(ClusterId id) {
      return null;
    }

    /**
     * Adds member ids to the query
     * @param memberId the members to restrict the resource search to
     * @return HelixResourceQueryBuilder
     */
    public HelixResourceQueryBuilder withMemberIds(List<MemberId> memberId) {
      return null;
    }

    /**
     * The resource to query for
     * @param resourceId the resource id
     * @return HelixResourceQueryBuilder
     */
    public HelixResourceQueryBuilder withResourceId(ResourceId resourceId) {
      return null;
    }

    /**
     * Specifies the properties which need to be present on the resources
     * for them to meet the query criteria
     * @param properties
     * @return HelixResourceQueryBuilder
     */
    public HelixResourceQueryBuilder withUserProperties(Map<String, Serializable> properties) {
      return null;
    }

    /**
     * Builds the query
     * @return T the query
     */
    public <T extends HelixQuery> T build() {
      return null;
    }
  }

  /**
   * A query builder for member queries
   */
  public static class HelixMemberQueryBuilder {
    HelixMemberQueryBuilder() {

    }

    /**
     * Adds a cluster id to the query
     * @param id the cluster to restrict the member search to
     * @return HelixMemberQueryBuilder
     */
    public HelixMemberQueryBuilder withClusterId(ClusterId id) {
      return null;
    }

    /**
     * Restrict the search to specific member types @see HelixMemberType
     * @param type the member type
     * @return HelixMemberQueryBuilder
     */
    public HelixMemberQueryBuilder withType(HelixMemberType type) {
      return null;
    }

    /**
     * Adds the member ids to restrict the query to
     * @param memberIds the member ids to query for
     * @return HelixMemberQueryBuilder
     */
    public HelixMemberQueryBuilder withMemberIds(List<MemberId> memberIds) {
      return null;
    }

    /**
     * Search for only enabled members or disabled members.
     * @param state <b>True</b>indicates enabled members, <b>False</b> indicates disabled members
     * @return HelixMemberQueryBuilder
     */
    public HelixMemberQueryBuilder withEnabledState(boolean state) {
      return null;
    }

    /**
     * Specifies the properties which need to be present on the resources
     * for them to meet the query criteria
     * @param properties
     * @return HelixResourceQueryBuilder
     */
    public HelixMemberQueryBuilder withUserProperties(Map<String, Serializable> properties) {
      return null;
    }

    /**
     * Searches for members with a specific defined tag
     * @param tag the tag to search for
     * @return HelixMemberQueryBuilder
     */
    public HelixMemberQueryBuilder withTag(String tag) {
      return null;
    }

    /**
     * Builds the query
     * @return T the query
     */
    public <T extends HelixQuery> T build() {
      return null;
    }
  }
}
