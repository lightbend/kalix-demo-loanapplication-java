package com.example.loanapp;

import com.example.loanapp.domain.LoanAppDomain;
import com.google.protobuf.Empty;
import kalix.javasdk.testkit.junit.KalixTestKitResource;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.*;

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

// Example of an integration test calling our service via the Kalix proxy
// Run all test classes ending with "IntegrationTest" using `mvn verify -Pit`
public class LoanAppEntityIntegrationTest {

  /**
   * The test kit starts both the service container and the Kalix proxy.
   */
  @ClassRule
  public static final KalixTestKitResource testKit =
    new KalixTestKitResource(Main.createKalix());

  /**
   * Use the generated gRPC client to call the service through the Kalix proxy.
   */
  private final LoanAppService client;

  public LoanAppEntityIntegrationTest() {
    client = testKit.getGrpcClient(LoanAppService.class);
  }

  @Test
  public void happyPath() throws Exception {
      String loanAppId = UUID.randomUUID().toString();
      client.submit(create(loanAppId)).toCompletableFuture().get(5, SECONDS);
      assertGet(loanAppId, LoanAppApi.LoanAppStatus.STATUS_IN_REVIEW);
      client.approve(LoanAppApi.ApproveCommand.newBuilder().setLoanAppId(loanAppId).build()).toCompletableFuture().get(5, SECONDS);
      assertGet(loanAppId, LoanAppApi.LoanAppStatus.STATUS_APPROVED);
  }

  private LoanAppApi.SubmitCommand create(String loanAppId, long monthlyIncomeCents, long loanAmountCents, int loanDurationMonths){
      return LoanAppApi.SubmitCommand.newBuilder()
              .setLoanAppId(loanAppId)
              .setClientId(UUID.randomUUID().toString())
              .setClientMonthlyIncomeCents(monthlyIncomeCents)
              .setLoanAmountCents(loanAmountCents)
              .setLoanDurationMonths(loanDurationMonths)
              .build();
  }
  private LoanAppApi.SubmitCommand create(String loanAppId){
      return create(loanAppId,1000,500,24);
  }

  private void assertGet(String loanAppId, LoanAppApi.LoanAppStatus status) throws Exception{
      LoanAppApi.LoanAppState loanApp = client.get(LoanAppApi.GetCommand.newBuilder().setLoanAppId(loanAppId).build()).toCompletableFuture().get(5,SECONDS);
      Assert.assertNotNull(loanApp);
      Assert.assertEquals(status,loanApp.getStatus());
  }
}
