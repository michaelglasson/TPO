package au.gov.agriculture.TPO;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import au.gov.agriculture.TPO.QualService.Qual;

public class TestAPI {

	private static HttpServer server;
	private static WebTarget target;
	
	@BeforeClass
	public static void setUp() throws Exception {
		// start the server
		 server = Main.startServer();
		// create the client
		Client c = ClientBuilder.newClient();
		target = c.target(Main.BASE_URI);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		server.shutdownNow();
	}

	@Test
	public void testPutQual() {
		Qual q = new Qual();
		q.authorityType = "University";
		q.expertise = "Veterinary Science";
		q.level = 10;
		Response r = target.path("quals/identifier").request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(q, MediaType.APPLICATION_JSON));
		assertEquals(r.getStatus(), Response.Status.CREATED.getStatusCode());
	}
	
	@Test
	public void updateQual() {
		Response r = target.path("quals/identifier").request(MediaType.APPLICATION_JSON)
				.get();
		assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
		Qual q = r.readEntity(Qual.class);
		
		q.authorityType = "Not a University any more";
		
		r = target.path("quals/identifier").request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(q, MediaType.APPLICATION_JSON));
		assertEquals(Response.Status.CREATED.getStatusCode(), r.getStatus());
	}
	
	@Test
	public void testGetQualSuccess() {
		Response r = target.path("quals/identifier").request(MediaType.APPLICATION_JSON)
				.get();
		assertEquals(r.getStatus(), Response.Status.OK.getStatusCode());
	}
	
	@Test
	public void testGetQualFailure() {
		Response s = target.path("quals/WRONGidentifier").request(MediaType.APPLICATION_JSON)
				.get();
		assertEquals(s.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
	}
}
