package au.gov.agriculture.TPO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
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

import org.apache.log4j.Logger;

import au.gov.agriculture.TPO.QualService.Qual;
import au.gov.agriculture.TPO.RoleService.Role;

@Singleton
@Path("TPOs")
public class TPOService {
	private List<TPO> tPOs = new ArrayList<>();
	
	final static Logger event = Logger.getLogger("EVENT");

	@Inject
	private QualService quals;

	@Inject
	private RoleService roles;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<TPO> listTPOs() {
		return tPOs;
	}

	@PUT
	@Path("{tPOId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TPO putTPO(@PathParam("tPOId") String tPOId, TPO tPO) {
		event.info("This is my first log4j's statement");
		tPO.tPOId = tPOId;
		tPOs.add(tPO);
		return tPO;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TPO postTPO(TPO t) {
		t.tPOId = UUID.randomUUID().toString();
		tPOs.add(t);
		return t;
	}

	@GET
	@Path("{tPOid}")
	@Produces(MediaType.APPLICATION_JSON)
	public TPO findTPO(@PathParam("TPOid") String tPOid) {
		return tPOs.stream().filter(x -> tPOid.equals(x.tPOId)).findAny().orElse(null);
	}

	@PUT
	@Path("{tPOId}/requestedRoles/{roleId}")
	@Produces(MediaType.APPLICATION_JSON)
	public TPO requestRole(@PathParam("tPOId") String tPOId, @PathParam("roleId") String roleId) {
		TPO t = tPOs.stream().filter(x -> tPOId.equals(x.tPOId)).findAny().orElse(null);
		Role r = roles.listRoles().stream().filter(x -> roleId.equals(x.roleId)).findAny().orElse(null);
		TPORole tr = new TPORole();
		tr.role = r;
		t.tPORoles.add(tr);
		return t;
	}

	@PUT
	@Path("{tPOId}/approvedRoles/{tPORoleId}")
	@Produces(MediaType.APPLICATION_JSON)
	public TPO approveRole(@PathParam("tPOId") String tPOId, @PathParam("tPORoleId") String tPORoleId) {
		TPO t = tPOs.stream().filter(x -> tPOId.equals(x.tPOId)).findAny().orElse(null);
		TPORole tr = t.tPORoles.stream().filter(x -> tPORoleId.equals(x.tPORoleId)).findAny().orElse(null);
		tr.approved = true;
		return t;
	}

	/*
	 * This method represents a claim made by a TPO, a claim to have gained a
	 * particular qualification. The claimed qualification is in two parts. The
	 * first part is a pre-existing Qual from the Quals repository. The second part
	 * is the specific qualification claimed by the TPO. For example, if the Qual is
	 * a university degree in veterinary science, then the TPOQual could be a degree
	 * in veterinary science awarded by the Uni of WA in 1998.
	 */

	@PUT
	@Path("{tPOId}/claimedQuals/{qualId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TPO claimQual(@PathParam("tPOId") String tPOId, @PathParam("qualId") String qualId, TPOQual tPOQual) {
		Qual q = quals.listQuals().stream().filter(x -> qualId.equals(x.qualId)).findAny().orElse(null);
		tPOQual.tPOQualId = UUID.randomUUID().toString();
		tPOQual.qual = q;
		TPO t = tPOs.stream().filter(x -> tPOId.equals(x.tPOId)).findAny().orElse(null);
		t.tPOQuals.add(tPOQual);
		return t;
	}

	@PUT
	@Path("{tPOId}/verifiedQuals/{tPOQualId}")
	@Produces(MediaType.APPLICATION_JSON)
	public TPO verifyQual(@PathParam("tPOId") String tPOId, @PathParam("tPOQualId") String tPOQualId) {
		TPO t = tPOs.stream().filter(x -> tPOId.equals(x.tPOId)).findAny().orElse(null);
		TPOQual tq = t.tPOQuals.stream().filter(x -> tPOQualId.equals(tPOQualId)).findAny().orElse(null);
		tq.verified = true;
		return t;
	}

	@GET
	@Path("{tPOId}/TPORoles")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TPORole> retrieveRoles(@PathParam("tPOId") String tPOId) {
		TPO t = tPOs.stream().filter(x -> tPOId.equals(x.tPOId)).findAny().orElse(null);
		return t.tPORoles;
	}

	@GET
	@Path("{tPOId}/TPOQuals")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TPOQual> retrieveQuals(@PathParam("tPOId") String tPOId) {
		TPO t = tPOs.stream().filter(x -> tPOId.equals(x.tPOId)).findAny().orElse(null);
		return t.tPOQuals;
	}

	@JsonbPropertyOrder({ "TPOId", "tPOName", "TPOroles", "TPOquals" })
	public static class TPO {
		public String tPOId;
		public String tPOName;
		public List<TPORole> tPORoles = new ArrayList<>();
		public List<TPOQual> tPOQuals = new ArrayList<>();
	}

	@JsonbPropertyOrder({ "tPOQualId", "qual", "issuingAuthority", "tPOQualName", "yearAwarded", "verified" })
	public static class TPOQual {
		public String tPOQualId;
		public QualService.Qual qual;
		public String issuingAuthority;
		public String tPOQualName;
		public int yearAwarded;
		public boolean verified;
	}

	@JsonbPropertyOrder({ "tPORoleId", "role", "approved" })
	public static class TPORole {
		public String tPORoleId = UUID.randomUUID().toString();
		public RoleService.Role role;
		public boolean approved;
	}
}
