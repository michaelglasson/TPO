package au.gov.agriculture.TPO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/*
 * Qual is a basic requirement for an actual qualification. It can be used to specify
 * education,training or experience qualification requirements. For example, a postgraduate
 * university degree in Plant Epidemiology can be specified just as easily as 3 years' 
 * work experience or professional registration.
 */

@Singleton
@Path("quals")
public class QualService {
	private static final Client client = ClientBuilder.newClient();
	private static final WebTarget tgt = client.target("http://localhost:5984/quals");

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Qual> listQuals() {
		QualListWrapper l = tgt.path("_all_docs").queryParam("include_docs", "true").request(MediaType.APPLICATION_JSON)
				.get(QualListWrapper.class);
		return l.rows.stream().map(x -> x.doc).collect(Collectors.toList());
	}

	@GET
	@Path("{qualId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findQual(@PathParam("qualId") String qualId) {
		return tgt.path(qualId).request(MediaType.APPLICATION_JSON).get();
	}

	@PUT
	@Path("{qualId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response putQual(@PathParam("qualId") String qualId, Qual q) {
		q.qualId = qualId;
		return tgt.path(qualId).request(MediaType.APPLICATION_JSON).put(Entity.entity(q, MediaType.APPLICATION_JSON));
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postQual(Qual q) {
		q.qualId = UUID.randomUUID().toString();
		return tgt.path(q.qualId).request(MediaType.APPLICATION_JSON).put(Entity.entity(q, MediaType.APPLICATION_JSON));
	}

	@JsonbPropertyOrder({ "qualId", "expertise", "level", "authorityType" })
	public static class Qual {
		@JsonbProperty("_id")
		public String qualId;
		@JsonbProperty("_rev")
		public String rev;
		public String authorityType;
		public String expertise;
		public int level;
	}

	public static class QualListWrapper {
		public String total_rows;
		public int offset;
		public List<Row> rows = new ArrayList<>();
	}

	public static class Row {
		public String id;
		public String key;
		public Value value;
		public Qual doc;
	}

	public static class Value {
		public String rev;
	}
}
