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
@Table(name="AA14DIVISIONT00")
	@DiscriminatorValue("DIV")	// see AA14DBEntityForOrganizationalEntityBase
	
@NamedQueries({
	@NamedQuery(name = "AA14DBEntitiesForOrgDivisionsByNameSPANISH",
				query = "SELECT division " +
						  "FROM AA14DBEntityForOrgDivision division " +
						 "WHERE division._nameSpanish LIKE :name "),
	@NamedQuery(name = "AA14DBEntitiesForOrgDivisionsByNameBASQUE",
				query = "SELECT division " +
						  "FROM AA14DBEntityForOrgDivision division " +
						 "WHERE division._nameBasque LIKE :name ")
})
@Accessors(prefix="_")
@NoArgsConstructor
public class AA14DBEntityForOrgDivision
     extends AA14DBEntityForOrganizationalEntityBase {

	private static final long serialVersionUID = -8544023719877260333L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  BI-DIRECTIONAL RELATIONSHIP division -> service (oneToMany)
//	Beware to update BOTH SIDES of the relationship: http://en.wikibooks.org/wiki/Java_Persistence/Relationships#Object_corruption.2C_one_side_of_the_relationship_is_not_updated_after_updating_the_other_side
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * Child services
//	 */
//	@OneToMany(targetEntity=AA14DBEntityForOrgDivisionService.class,			// not required but informative
//			   mappedBy="_orgDivision",										   	// relationship owner
//			   cascade={CascadeType.REMOVE},
//			   orphanRemoval=true,
//			   fetch=FetchType.LAZY)
//	@Getter private Collection<AA14DBEntityForOrgDivisionService> _orgDivisionServices;
//	
//	public void addService(final AA14DBEntityForOrgDivisionService dbService) {	
//		if (_orgDivisionServices == null) _orgDivisionServices = Lists.newArrayList();
//		_orgDivisionServices.add(dbService);
//		if (dbService.getOrgDivision() != this) dbService.setOrgDivision(this);		// update the other side of the relationship
//	}
//	public boolean containsService(final AA14DBEntityForOrgDivisionService dbService) {
//		return _orgDivisionServices != null ? _orgDivisionServices.contains(dbService) : false;
//	}
}
