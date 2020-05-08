package aa14b.db.entities;

import java.util.Collection;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Entity @Cacheable(false)
@Table(name="AA14LOCATIONT00") 
	@DiscriminatorValue("LOC")	// see AA14DBEntityForOrganizationalEntityBase

@NamedQueries({
	@NamedQuery(name = "AA14DBEntitiesForOrgDivisionServiceLocationsByNameSPANISH",
				query = "SELECT location " +
						  "FROM AA14DBEntityForOrgDivisionServiceLocation location " +
						 "WHERE location._nameSpanish LIKE :name "),
	@NamedQuery(name = "AA14DBEntityForOrgDivisionServiceByNameBASQUE",
				query = "SELECT location " +
						  "FROM AA14DBEntityForOrgDivisionServiceLocation location " +
						 "WHERE location._nameBasque LIKE :name ")
})
@Accessors(prefix="_")
@NoArgsConstructor
public class AA14DBEntityForOrgDivisionServiceLocation
     extends AA14DBEntityForOrganizationalEntityBase {
	
	private static final long serialVersionUID = 2768673101219364565L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  BI-DIRECTIONAL RELATIONSHIP WITH schedules
//	Beware to update BOTH SIDES of the relationship: http://en.wikibooks.org/wiki/Java_Persistence/Relationships#Object_corruption.2C_one_side_of_the_relationship_is_not_updated_after_updating_the_other_side
/////////////////////////////////////////////////////////////////////////////////////////
	@ManyToMany(targetEntity=AA14DBEntityForSchedule.class,		// not required but informative
				cascade= {CascadeType.PERSIST},
				mappedBy="_orgDivisionServiceLocations",
			    fetch=FetchType.LAZY)
	@Getter private Collection<AA14DBEntityForSchedule> _schedules;
	
	public void addSchedule(final AA14DBEntityForSchedule dbSchedule) {	
		if (_schedules == null) _schedules = Lists.newArrayList();
		_schedules.add(dbSchedule);
		if (!dbSchedule.containsLocation(this)) dbSchedule.addLocation(this);		// update the other side of the relationship
	}
	public boolean containsSchedule(final AA14DBEntityForSchedule dbSchedule) {
		return _schedules != null ? _schedules.contains(dbSchedule) : false;
	}
}
