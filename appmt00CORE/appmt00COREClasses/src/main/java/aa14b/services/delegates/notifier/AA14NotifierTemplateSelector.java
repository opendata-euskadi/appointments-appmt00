package aa14b.services.delegates.notifier;

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
	/**
	 * Returns the template used when CREATING a new appointment
	 * @param org
	 * @param div
	 * @param srvc
	 * @param loc
	 * @param lang
	 * @return
	 */
	public Path createTemplatePathFor(final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									  final Language lang);
	/**
	 * Returns the template used when UPDATING an existing appointment
	 * @param org
	 * @param div
	 * @param srvc
	 * @param loc
	 * @param lang
	 * @return
	 */
	public Path updateTemplatePathFor(final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									  final Language lang);
	/**
	 * Returns the template used when DELETING an existing appointment
	 * @param org
	 * @param div
	 * @param srvc
	 * @param loc
	 * @param lang
	 * @return
	 */
	public Path deleteTemplatePathFor(final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									  final Language lang);
	/**
	 * Returns the template used when REMINDING an existing appointment
	 * @param org
	 * @param div
	 * @param srvc
	 * @param loc
	 * @param lang
	 * @return
	 */
	public Path remindTomorrowTemplatePathFor(final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									  		  final Language lang);
	/**
	 * Returns the template used when REMINDING an existing appointment
	 * @param org
	 * @param div
	 * @param srvc
	 * @param loc
	 * @param lang
	 * @return
	 */
	public Path remindTodayTemplatePathFor(final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									  	   final Language lang);
}
