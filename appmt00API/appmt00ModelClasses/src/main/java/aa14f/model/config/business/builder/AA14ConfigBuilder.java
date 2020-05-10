package aa14f.model.config.business.builder;

import java.util.Collection;

import aa14f.model.config.AA14NotifierFromConfig;
import aa14f.model.config.AA14NotifierMessageComposingConfig;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14BusinessID;

public interface AA14ConfigBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14Organization createOrganizationConfig(final AA14BusinessID businessId);
	
	public Collection<AA14OrgDivision> createOrgDivisionConfigs(final AA14Organization org);
	
	public Collection<AA14OrgDivisionService> createOrgDivisionServicesConfigs(final AA14Organization org,
									  							      		   final Collection<AA14OrgDivision> divisions);
	
	public Collection<AA14OrgDivisionServiceLocation> createOrgDivisionServiceLocationsConfigs(final AA14Organization org,
																  							   final Collection<AA14OrgDivision> divisions,
																  							   final Collection<AA14OrgDivisionService> services);
	public Collection<AA14Schedule> createSchedulesConfigs(final AA14Organization org,
														   final Collection<AA14OrgDivision> divisions,
														   final Collection<AA14OrgDivisionService> services,
														   final Collection<AA14OrgDivisionServiceLocation> locs);
/////////////////////////////////////////////////////////////////////////////////////////
//	NOTIFIER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14NotifierFromConfig createNotifierFromConfig();
	
	public AA14NotifierMessageComposingConfig createNotifierMessageComposingConfig();
}
