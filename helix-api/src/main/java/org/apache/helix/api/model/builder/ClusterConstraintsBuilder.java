package org.apache.helix.api.model.builder;

import org.apache.commons.discovery.tools.DiscoverClass;
import org.apache.helix.api.model.IClusterConstraints;
import org.apache.helix.api.model.IClusterConstraints.ConstraintType;

public abstract class ClusterConstraintsBuilder {

  public abstract ClusterConstraintsBuilder withType(ConstraintType type);
  
  public abstract IClusterConstraints build();
  
  public static ClusterConstraintsBuilder newInstance(){
    try{
      return new DiscoverClass().newInstance(ClusterConstraintsBuilder.class);
    }catch(Throwable e){
      throw new IllegalStateException(e);
    }
  }
}
