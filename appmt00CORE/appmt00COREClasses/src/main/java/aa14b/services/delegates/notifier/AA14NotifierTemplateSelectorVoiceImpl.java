package aa14b.services.delegates.notifier;

import aa14b.services.internal.AA14CORESideBusinessConfigServices;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.types.Path;

@RequiredArgsConstructor
public class AA14NotifierTemplateSelectorVoiceImpl 
  implements AA14NotifierTemplateSelector {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14CORESideBusinessConfigServices _businessConfigServices;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CREATE / UPDATE DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Path createTemplateFor(final AA14BusinessID businessId,
								  final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
								  final Language lang) {
		throw new UnsupportedOperationException("to be implemented");
	}
	@Override
	public Path updateTemplateFor(final AA14BusinessID businessId,
								  final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
								  final Language lang) {
		throw new UnsupportedOperationException("to be implemented");
	}
	@Override
	public Path deleteTemplateFor(final AA14BusinessID businessId,
								  final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
								  final Language lang) {
		throw new UnsupportedOperationException("to be implemented");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	REMIND
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Path remindTomorrowTemplateFor(final AA14BusinessID businessId,
										  final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
								  		  final Language lang) {
		throw new UnsupportedOperationException("to be implemented");
	}
	@Override
	public Path remindTodayTemplateFor(final AA14BusinessID businessId,
									   final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
								  	   final Language lang) {
		throw new UnsupportedOperationException("to be implemented");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PERSON LOCATOR
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Path remindPersonIdLocatorTemplateFor(final AA14BusinessID businessId,
									   			 final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									   			 final Language lang) {
		throw new UnsupportedOperationException("to be implemented");	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////


}
