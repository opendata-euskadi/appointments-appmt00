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
@Table(name="AA14SERVICET00") 
	@DiscriminatorValue("SRV")	// see AA14DBEntityForOrganizationalEntityBase

@NamedQueries({
	@NamedQuery(name = "AA14DBEntitiesForOrgDivisionServicesByNameSPANISH",
				query = "SELECT service " +
						  "FROM AA14DBEntityForOrgDivisionService service " +
						 "WHERE service._nameSpanish LIKE :name "),
	@NamedQuery(name = "AA14DBEntitiesForOrgDivisionServicesByNameBASQUE",
				query = "SELECT service " +
						  "FROM AA14DBEntityForOrgDivisionService service " +
						 "WHERE service._nameBasque LIKE :name ")
})
@Accessors(prefix="_")
@NoArgsConstructor
public class AA14DBEntityForOrgDivisionService
     extends AA14DBEntityForOrganizationalEntityBase {

	private static final long serialVersionUID = 698294185545489256L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  BI-DIRECTIONAL RELATIONSHIP service -> location (oneToMany)
//	Beware to update BOTH SIDES of the relationship: http://en.wikibooks.org/wiki/Java_Persistence/Relationships#Object_corruption.2C_one_side_of_the_relationship_is_not_updated_after_updating_the_other_side
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * Child services
//	 */
//	@OneToMany(targetEntity=AA14DBEntityForOrgDivisionServiceLocation.class,	// not required but informative
//			   mappedBy="_orgDivisionService",									// relationship owner
//			   cascade={CascadeType.REMOVE},
//			   orphanRemoval=true,
//			   fetch=FetchType.LAZY)
//	@Getter private Collection<AA14DBEntityForOrgDivisionServiceLocation> _orgDivisionServiceLocations;
//	
//	public void addLocation(final AA14DBEntityForOrgDivisionServiceLocation dbLocation) {	
//		if (_orgDivisionServiceLocations == null) _orgDivisionServiceLocations = Lists.newArrayList();
//		_orgDivisionServiceLocations.add(dbLocation);
//		if (dbLocation.getOrgDivisionService() != this) dbLocation.setOrgDivisionService(this);		// update the other side of the relationship
//	}
//	public boolean containsLocation(final AA14DBEntityForOrgDivisionServiceLocation dbLocation) {
//		return _orgDivisionServiceLocations != null ? _orgDivisionServiceLocations.contains(dbLocation) : false;
//	}
}
