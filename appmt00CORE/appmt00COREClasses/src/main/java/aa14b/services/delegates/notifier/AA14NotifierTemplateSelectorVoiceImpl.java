package aa14b.services.delegates.notifier;

import aa14b.notifier.config.AA14NotifierConfigForVoice;
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
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14NotifierConfigForVoice _config;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Path createTemplatePathFor(final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									  final Language lang) {
		throw new UnsupportedOperationException("to be implemented");
	}

	@Override
	public Path updateTemplatePathFor(final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									  final Language lang) {
		throw new UnsupportedOperationException("to be implemented");
	}

	@Override
	public Path deleteTemplatePathFor(final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									  final Language lang) {
		throw new UnsupportedOperationException("to be implemented");
	}
	@Override
	public Path remindTomorrowTemplatePathFor(final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									  		  final Language lang) {
		throw new UnsupportedOperationException("to be implemented");
	}
	@Override
	public Path remindTodayTemplatePathFor(final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									  	   final Language lang) {
		throw new UnsupportedOperationException("to be implemented");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////


}
