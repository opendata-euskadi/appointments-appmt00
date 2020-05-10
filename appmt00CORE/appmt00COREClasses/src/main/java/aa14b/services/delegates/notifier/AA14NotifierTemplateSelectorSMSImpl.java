package aa14b.services.delegates.notifier;

import aa14b.services.internal.AA14CORESideBusinessConfigServices;
import aa14f.model.AA14NotificationOperation;
import aa14f.model.config.AA14NotifierMessageComposingConfig;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.types.Path;

@RequiredArgsConstructor
     class AA14NotifierTemplateSelectorSMSImpl 
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
		return _templatePathFor(AA14NotificationOperation.CREATE,
								businessId,
								org,div,srvc,loc,
						 		lang);
	}
	@Override
	public Path updateTemplateFor(final AA14BusinessID businessId,
								  final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
								  final Language lang) {
		return _templatePathFor(AA14NotificationOperation.UPDATE,
								businessId,
								org,div,srvc,loc,
						 		lang);
	}
	@Override
	public Path deleteTemplateFor(final AA14BusinessID businessId,
								  final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
								  final Language lang) {
		return _templatePathFor(AA14NotificationOperation.DELETE,
								businessId,
								org,div,srvc,loc,
						 		lang);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	REMIND
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Path remindTomorrowTemplateFor(final AA14BusinessID businessId,
										  final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
								  		  final Language lang) {
		return _templatePathFor(AA14NotificationOperation.REMIND_TOMORROW,
								businessId,
								org,div,srvc,loc,
						 		lang);
	}
	@Override
	public Path remindTodayTemplateFor(final AA14BusinessID businessId,
									   final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
								  	   final Language lang) {
		return _templatePathFor(AA14NotificationOperation.REMIND_TODAY,
							    businessId,
								org,div,srvc,loc,
						 		lang);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PERSON LOCATOR
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Path remindPersonIdLocatorTemplateFor(final AA14BusinessID businessId,
									   			 final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									   			 final Language lang) {
		return _templatePathFor(AA14NotificationOperation.REMIND_PERSON_LOCATOR,
							    businessId,
								org,div,srvc,loc,
						 		lang);		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	private Path _templatePathFor(final AA14NotificationOperation op,
								  final AA14BusinessID businessId,
								  final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
								  final Language lang) {
		AA14NotifierMessageComposingConfig notifMessageComposingConfig = _businessConfigServices.getCORESideCachedBusinessConfigs()
																								.getFor(businessId)
																								.getNotifierMessageComposingConfigFor(org,div,srvc);
		Path outPath = notifMessageComposingConfig.getSMSTemplateFor(op);
		return outPath;
	}
}
