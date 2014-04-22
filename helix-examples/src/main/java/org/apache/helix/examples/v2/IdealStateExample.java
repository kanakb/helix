package org.apache.helix.examples.v2;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.helix.api.client.HelixAdministratorClient;
import org.apache.helix.api.client.HelixClientFactory;
import org.apache.helix.api.command.HelixClusterCommand;
import org.apache.helix.api.command.HelixCommandBuilderFactory;
import org.apache.helix.api.command.HelixParticipantCommand;
import org.apache.helix.api.command.HelixResourceCommand;
import org.apache.helix.api.command.HelixStateModelDefinitionCommand;
import org.apache.helix.api.id.ClusterId;
import org.apache.helix.api.id.ParticipantId;
import org.apache.helix.api.id.RebalancerId;
import org.apache.helix.api.id.ResourceId;
import org.apache.helix.api.id.StateModelDefinitionId;
import org.apache.helix.api.rebalancer.RebalancerConfiguration.RebalanceMode;
import org.apache.helix.spi.store.HelixStoreProviderProperties;

public class IdealStateExample {
  public static void main(String[] args) throws Exception {
    if (args.length < 3) {
      System.err
          .println("USAGE: IdealStateExample connectString clusterName idealStateMode (FULL_AUTO, SEMI_AUTO, or CUSTOMIZED) idealStateJsonFile (required for CUSTOMIZED mode)");
      System.exit(1);
    }

    final String connectString = args[0];
    final String clusterName = args[1];
    final String idealStateRebalancerModeStr = args[2].toUpperCase();
    String idealStateJsonFile = null;
    RebalanceMode idealStateRebalancerMode = RebalanceMode.valueOf(idealStateRebalancerModeStr);
    if (idealStateRebalancerMode == RebalanceMode.CUSTOMIZED) {
      if (args.length < 4) {
        System.err.println("Missng idealStateJsonFile for CUSTOMIZED ideal state mode");
        System.exit(1);
      }
      idealStateJsonFile = args[3];
    }
    Properties properties = new Properties();
    properties.put(HelixStoreProviderProperties.PROVIDER_NAME, "Zookeeper");
    properties.put(HelixStoreProviderProperties.PROVIDER_CONNECT_STRING, connectString);
    HelixAdministratorClient client = HelixClientFactory.instance().createAdminClient(properties);

    // Create the state model definition command to add to the cluster
    HelixStateModelDefinitionCommand stateModelCommand =
        HelixCommandBuilderFactory.createStateModelDefinitionBuilder(
            StateModelDefinitionId.from("MasterSlave")).build();

    // Create the participants to add to the cluster
    Set<HelixParticipantCommand> participants = new HashSet<HelixParticipantCommand>();
    for (int i = 0; i < 3; i++) {
      int port = 12918 + i;
      HelixParticipantCommand command =
          (HelixParticipantCommand) HelixCommandBuilderFactory
              .createParticipantMemberBuilder(ParticipantId.from("localhost_" + port))
              .forHost("localhost").forPort(port).enable().build();
      participants.add(command);
    }

    RebalancerId rebalancerId = null;

    // Create the resources to add to the cluster
    HelixResourceCommand resourceCommand =
        HelixCommandBuilderFactory.createResourceBuilder(ResourceId.from("TestDB"))
            .withPartitions(4).withRebalancerId(rebalancerId)
            .withStateModelDefinitionId(stateModelCommand.getId()).build();

    Set<HelixResourceCommand> resources = new HashSet<HelixResourceCommand>();
    resources.add(resourceCommand);

    // Create the cluster command
    HelixClusterCommand clusterCommand =
        HelixCommandBuilderFactory.createClusterBuilder(ClusterId.from(clusterName))
            .recreateIfExists(true).withStateModelDefinition(stateModelCommand)
            .withParticipants(participants).withResources(resources).build();
    client.addCluster(clusterCommand);
  }
}
