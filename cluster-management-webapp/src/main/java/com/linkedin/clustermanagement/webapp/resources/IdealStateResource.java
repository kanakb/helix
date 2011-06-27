package com.linkedin.clustermanagement.webapp.resources;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import com.linkedin.clustermanager.core.ClusterDataAccessor;
import com.linkedin.clustermanager.core.ClusterDataAccessor.ClusterPropertyType;
import com.linkedin.clustermanager.impl.zk.ZKDataAccessor;
import com.linkedin.clustermanager.impl.zk.ZNRecordSerializer;
import com.linkedin.clustermanager.model.ZNRecord;
import com.linkedin.clustermanager.tools.ClusterSetup;

public class IdealStateResource extends Resource
{
  public IdealStateResource(Context context,
      Request request,
      Response response) 
  {
    super(context, request, response);
    getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));
  }

  public boolean allowGet()
  {
    return true;
  }
  
  public boolean allowPost()
  {
    return true;
  }
  
  public boolean allowPut()
  {
    return false;
  }
  
  public boolean allowDelete()
  {
    return false;
  }
  
  public Representation represent(Variant variant)
  {
    StringRepresentation presentation = null;
    try
    {
      String zkServer = (String)getContext().getAttributes().get("zkServer");
      String clusterName = (String)getRequest().getAttributes().get("clusterName");
      String entityId = (String)getRequest().getAttributes().get("entityId");
      presentation = getIdealStateRepresentation(zkServer, clusterName, entityId);
    }
    
    catch(Exception e)
    {
      getResponse().setEntity("ERROR " + e.getMessage(),
          MediaType.TEXT_PLAIN);
      getResponse().setStatus(Status.SUCCESS_OK);
    }  
    return presentation;
  }
  
  StringRepresentation getIdealStateRepresentation(String zkServerAddress, String clusterName, String entityId) throws JsonGenerationException, JsonMappingException, IOException
  {
    String message = "Ideal state for entity " + entityId + " in cluster "+ clusterName + "\n";
    message += ClusterRepresentationUtil.getClusterPropertyAsString(zkServerAddress, clusterName, ClusterPropertyType.IDEALSTATES, entityId, MediaType.APPLICATION_JSON);
    
    StringRepresentation representation = new StringRepresentation(message, MediaType.APPLICATION_JSON);
    
    return representation;
  }
  
  public void acceptRepresentation(Representation entity)
  {
    try
    {
      String zkServer = (String)getContext().getAttributes().get("zkServer");
      String clusterName = (String)getRequest().getAttributes().get("clusterName");
      String entityId = (String)getRequest().getAttributes().get("entityId");
      
      Form form = new Form(entity);
     
      int replicas = Integer.parseInt(form.getFirstValue("replicas"));
      ClusterSetup setupTool = new ClusterSetup(zkServer);
      setupTool.rebalanceStorageCluster(clusterName, entityId, replicas);
            // add cluster
      getResponse().setEntity(getIdealStateRepresentation(zkServer, clusterName, entityId));
      getResponse().setStatus(Status.SUCCESS_OK);
    }

    catch(Exception e)
    {
      getResponse().setEntity("ERROR " + e.getMessage(),
          MediaType.TEXT_PLAIN);
      getResponse().setStatus(Status.SUCCESS_OK);
    }  
  }
}