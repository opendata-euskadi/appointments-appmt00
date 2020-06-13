package aa14b.db.entities;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Entity @Cacheable(false)
@Table(name="AA14ORGANIZATIONT00")
	@DiscriminatorValue("ORG")	// see AA14DBEntityForOrganizationalEntityBase

@NamedQueries({
	@NamedQuery(name = "AA14DBEntitiesForOrganizationsByNameSPANISH",
				query = "SELECT org " +
						  "FROM AA14DBEntityForOrganization org " +
						 "WHERE org._nameSpanish LIKE :name "),
	@NamedQuery(name = "AA14DBEntitiesForOrganizationsByNameBASQUE",
				query = "SELECT org " +
						  "FROM AA14DBEntityForOrganization org " +
						 "WHERE org._nameBasque LIKE :name ")
})
@Accessors(prefix="_")
@NoArgsConstructor
public class AA14DBEntityForOrganization
     extends AA14DBEntityForOrganizationalEntityBase {

	private static final long serialVersionUID = -5447136725316833669L;
/////////////////////////////////////////////////////////////////////////////////////////
//  BI-DIRECTIONAL RELATIONSHIP Organization -> Division (oneToMany)
//	Beware to update BOTH SIDES of the relationship: http://en.wikibooks.org/wiki/Java_Persistence/Relationships#Object_corruption.2C_one_side_of_the_relationship_is_not_updated_after_updating_the_other_side
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * Child divisions
//	 */
//	@OneToMany(targetEntity=AA14DBEntityForOrgDivision.class,		// not required but informative
//			   mappedBy="_organization",							// relationship owner
//			   cascade={CascadeType.REMOVE},
//			   orphanRemoval=true,
//			   fetch=FetchType.LAZY)
//	@Getter private Collection<AA14DBEntityForOrgDivision> _orgDivisions;
//	
//	public void addOrgDivision(final AA14DBEntityForOrgDivision orgDivision) {	
//		if (_orgDivisions == null) _orgDivisions = Lists.newArrayList();
//		_orgDivisions.add(orgDivision);
//		if (orgDivision.getOrganization() != this) orgDivision.setOrganization(this);		// update the other side of the relationship
//	}
//	public boolean containsOrgDivision(final AA14DBEntityForOrgDivision orgDivision) {
//		return _orgDivisions != null ? _orgDivisions.contains(orgDivision) : false;
//	}
}
