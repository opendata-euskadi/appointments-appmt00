package aa14b.services.delegates.notifier;

import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import r01f.locale.Language;
import r01f.types.Path;

/**
 * Selects the template using some model data
 */
public interface AA14NotifierTemplateSelector {
/////////////////////////////////////////////////////////////////////////////////////////
//	CREATE / UPDATE / DELETE
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the template used when CREATING a new appointment
	 * @param businessId
	 * @param org
	 * @param div
	 * @param srvc
	 * @param loc
	 * @param lang
	 * @return
	 */
	public Path createTemplateFor(final AA14BusinessID businessId,
								  final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
								  final Language lang);
	/**
	 * Returns the template used when UPDATING an existing appointment
	 * @param businessId
	 * @param org
	 * @param div
	 * @param srvc
	 * @param loc
	 * @param lang
	 * @return
	 */
	public Path updateTemplateFor(final AA14BusinessID businessId,
								  final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
								  final Language lang);
	/**
	 * Returns the template used when DELETING an existing appointment
	 * @param businessId
	 * @param org
	 * @param div
	 * @param srvc
	 * @param loc
	 * @param lang
	 * @return
	 */
	public Path deleteTemplateFor(final AA14BusinessID businessId,
								  final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
								  final Language lang);
/////////////////////////////////////////////////////////////////////////////////////////
//	REMIND
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the template used when REMINDING an existing appointment
	 * @param businessId
	 * @param org
	 * @param div
	 * @param srvc
	 * @param loc
	 * @param lang
	 * @return
	 */
	public Path remindTomorrowTemplateFor(final AA14BusinessID businessId,
										  final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
										  final Language lang);
	/**
	 * Returns the template used when REMINDING an existing appointment
	 * @param businessId
	 * @param org
	 * @param div
	 * @param srvc
	 * @param loc
	 * @param lang
	 * @return
	 */
	public Path remindTodayTemplateFor(final AA14BusinessID businessId,
									   final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
								  	   final Language lang);
/////////////////////////////////////////////////////////////////////////////////////////
//	PERSON ID LOCATOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the template used when REMINDING the personId locator
	 * @param businessId
	 * @param org
	 * @param div
	 * @param srvc
	 * @param loc
	 * @param lang
	 * @return
	 */
	public Path remindPersonIdLocatorTemplateFor(final AA14BusinessID businessId,
									   			 final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									   			 final Language lang);
}
