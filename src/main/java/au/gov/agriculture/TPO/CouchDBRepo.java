package au.gov.agriculture.TPO;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class CouchDBRepo {
	private Client c = ClientBuilder.newClient();
	private WebTarget target = c.target("http://localhost:5984/tpo");


	

}
