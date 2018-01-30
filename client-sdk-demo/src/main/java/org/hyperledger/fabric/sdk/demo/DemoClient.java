package org.hyperledger.fabric.sdk.demo;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

public class DemoClient {
	private HFClient client;
	private Channel channel;
	private long timeoutMillis;
	private ChaincodeID ccId;

	public DemoClient timeoutMillis(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
		return this;
	}

	public DemoClient ccId(String ccName, String ccVersion) {
		this.ccId = ChaincodeID.newBuilder().setName(ccName).setVersion(ccVersion).build();
		return this;
	}

	public DemoClient initClient(String msp) throws Exception {
		HFCAClient caClient = HFCAClient.createNewInstance("http://localhost:7054", null);
		caClient.setCryptoSuite(CryptoSuiteFactory.getDefault().getCryptoSuite());
		Enrollment enrollment = caClient.enroll("admin", "adminpw");
		this.client = HFClient.createNewInstance();
		this.client.setCryptoSuite(CryptoSuiteFactory.getDefault().getCryptoSuite());
		this.client.setUserContext(new DemoUser("admin", msp, enrollment));
		return this;
	}

	public DemoClient createChannel() throws Exception {
		InputStream configFile = getClass().getClassLoader().getResourceAsStream("network-config.yaml");
		NetworkConfig config = NetworkConfig.fromYamlStream(configFile);
		channel = client.loadChannelFromConfig("tutorialchannel", config);
		channel.initialize();
		return this;
	}


	public DemoClient callChaincode(Consumer<String> resultConsumer, boolean query, String function, String... args)
			throws Exception {
		TransactionProposalRequest request = client.newTransactionProposalRequest();
		request.setChaincodeID(ccId);
		request.setFcn(function);
		request.setArgs(args);
		request.setProposalWaitTime(timeoutMillis);
		// endorse
		Collection<ProposalResponse> responses = channel.sendTransactionProposal(request);
		String result = responses.stream().findFirst().map((response) -> {
			if (!response.isVerified() || response.getStatus() != ChaincodeResponse.Status.SUCCESS) {
				throw new RuntimeException(response.getMessage());
			}
			try {
				return new String(response.getChaincodeActionResponsePayload());
			} catch (InvalidArgumentException e) {
				throw new RuntimeException(e);
			}
		}).orElse("");
		if (!query) {
			// commit
			channel.sendTransaction(responses).get(timeoutMillis, TimeUnit.MILLISECONDS);
		}
		if (resultConsumer != null) {
			resultConsumer.accept(result);
		}
		return this;
	}

	public void shutdown() throws Exception {
		if (channel != null) {
			channel.shutdown(true);
		}
		if (client != null) {
			final Field executorService = HFClient.class.getDeclaredField("executorService");
			executorService.setAccessible(true);
			((ExecutorService) executorService.get(client)).shutdownNow();
			System.exit(0);
		}
	}

	public static void main(String[] args) throws Exception {
		new DemoClient().timeoutMillis(30 * 1000)
				.ccId("tutorial-demo", "v1")
				.initClient("TutorialMSP")
				.createChannel()
				.callChaincode(null, false, "invoke", "{\"a\": 1000, \"b\": 2200, \"k\": \"ijk\"}")
				.callChaincode(System.out::println, true, "query", "ijk")
				.shutdown();
	}

}
