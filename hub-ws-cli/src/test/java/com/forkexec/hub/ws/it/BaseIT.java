package com.forkexec.hub.ws.it;

import java.io.IOException;
import java.util.Properties;

import java.util.List;
import java.util.ArrayList;
import com.forkexec.hub.ws.cli.HubClient;
import com.forkexec.pts.ws.cli.PointsClient;

import org.junit.AfterClass;
import org.junit.BeforeClass;


/**
 * Base class for testing a Park Load properties from test.properties
 */
public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static HubClient hubClient;
	protected static PointsClient pointsClient;

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		final String uddiEnabled = testProps.getProperty("uddi.enabled");
		final String verboseEnabled = testProps.getProperty("verbose.enabled");

		final String uddiURL = testProps.getProperty("uddi.url");
		final String wsName = testProps.getProperty("ws.name");
		final String wsURL = testProps.getProperty("ws.url");

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			hubClient = new HubClient(uddiURL, wsName);
		} else {
			hubClient = new HubClient(wsURL);
		}
		pointsClient = new PointsClient(uddiURL, "A41_Points1");

		hubClient.setVerbose("true".equalsIgnoreCase(verboseEnabled));
	}

	@AfterClass
	public static void cleanup() {
	}

}
