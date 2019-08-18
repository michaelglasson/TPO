package au.gov.agriculture.TPO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Singleton;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/*
 * Qual is a basic requirement for an actual qualification. It can be used to specify
 * education,training or experience qualification requirements. For example, a postgraduate
 * university degree in Plant Epidemiology can be specified just as easily as 3 years' 
 * work experience or professional registration.
 */

@Singleton
@Path("quals")
public class QualService {
	private List<Qual> quals = new ArrayList<>();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Qual> listQuals() {
		return quals;
	}

	@GET
	@Path("{qualId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Qual findQual(@PathParam("qualId") String qualId) {
		return quals.stream().filter(x -> qualId.equals(x.qualId)).findAny().orElse(null);
	}

	@PUT
	@Path("{qualId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Qual putQual(@PathParam("qualId") String qualId, Qual q) {
		q.qualId = qualId;
		quals.add(q);
		return q;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Qual postQual(Qual q) {
		q.qualId = UUID.randomUUID().toString(); // Use as key
		quals.add(q);
		return q;
	}

	@JsonbPropertyOrder({ "qualId", "expertise", "level", "authorityType" })
	public static class Qual {
		public String qualId;
		public String authorityType;
		public String expertise;
		public int level;
	}
}
