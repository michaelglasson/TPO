package au.gov.agriculture.TPO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
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

import au.gov.agriculture.TPO.QualService.Qual;
import au.gov.agriculture.TPO.QualService.QualListWrapper;
import au.gov.agriculture.TPO.QualService.Row;
import au.gov.agriculture.TPO.QualService.Value;

/*
 * A given business process involves participants who play certain roles. A role is specific to
 * a business process, and a jurisdiction. A role requires a set of qualifications, which
 * are specified as required qualifications. Roles are generic. Particular TPOs may request
 * particular roles. Approval of a TPOs ability to play that role is part of the TPORole
 * domain, not this general or canonical role domain.
 */

@Singleton
@Path("roles")
public class RoleService {
	private List<Role> roles = new ArrayList<>();
	private static final Client client = ClientBuilder.newClient();
	private static final WebTarget tgt = client.target("http://localhost:5984/roles");


	@Inject
	private QualService qualService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Role> listRoles() {
		RoleListWrapper l = tgt.path("_all_docs").queryParam("include_docs", "true").request(MediaType.APPLICATION_JSON)
				.get(RoleListWrapper.class);
		return l.rows.stream().map(x -> x.doc).collect(Collectors.toList());
	}

	@GET
	@Path("{roleId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findRole(@PathParam("roleId") String roleId) {
		return tgt.path(roleId).request(MediaType.APPLICATION_JSON).get();
	}

	@PUT
	@Path("{roleId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response putRole(@PathParam("roleId") String roleId, Role r) {
		r.roleId = roleId;
		return tgt.path(roleId).request(MediaType.APPLICATION_JSON).put(Entity.entity(r, MediaType.APPLICATION_JSON));
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postRole(Role r) {
		r.roleId = UUID.randomUUID().toString();
		return tgt.path(r.roleId).request(MediaType.APPLICATION_JSON).put(Entity.entity(r, MediaType.APPLICATION_JSON));
	}

	@GET
	@Path("{roleId}/requiredQuals")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Qual> listRequiredQuals(@PathParam("roleId") String roleId) {
		Role r = roles.stream().filter(x -> roleId.equals(x.roleId)).findAny().orElse(null);
		return r.requiredQuals;
	}

	@PUT
	@Path("{roleId}/requiredQuals/{qualId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Role addRequiredQual(@PathParam("roleId") String roleId, @PathParam("qualId") String qualId) {
		Role r = roles.stream().filter(x -> roleId.equals(x.roleId)).findAny().orElse(null);
		Qual q = qualService.listQuals().stream().filter(x -> qualId.equals(x.qualId)).findAny().orElse(null);
		r.requiredQuals.add(q);
		return r;
	}

	@JsonbPropertyOrder({ "roleId", "name", "processName", "jurisdiction" })
	public static class Role {
		@JsonbProperty("_id")
		public String roleId;
		@JsonbProperty("_rev")
		public String rev;
		public String name; // Role performed in process, e.g. Inspector
		public String processName; // Name of process in jurisdiction, e.g. Inspection
		public String jurisdiction; // Government or regulatory environment
		public List<QualService.Qual> requiredQuals = new ArrayList<>();
	}
	
	public static class RoleListWrapper {
		public String total_rows;
		public int offset;
		public List<Row> rows = new ArrayList<>();
	}

	public static class Row {
		public String id;
		public String key;
		public Value value;
		public Role doc;
	}

	public static class Value {
		public String rev;
	}
	
	
}