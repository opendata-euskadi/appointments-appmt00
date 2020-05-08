package aa14b.services.delegates.notifier;

import aa14f.model.config.business.AA14BusinessConfigForBizilagun;
import aa14f.model.config.business.AA14BusinessConfigForBloodDonation;
import aa14f.model.config.business.AA14BusinessConfigForJustizia;
import aa14f.model.config.business.AA14BusinessConfigForMedicalService;
import aa14f.model.config.business.AA14BusinessConfigForTrafikoa;
import aa14f.model.config.business.AA14BusinessConfigForZuzenean;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import r01f.locale.Language;
import r01f.util.types.Strings;

/**
 * Selects the {@link AA14NotifierTemplateModelDataSupplier} using some model data
 */
public class AA14NotifierDataSupplierSelector {
	/**
	 * Returns the {@link AA14NotifierTemplateModelDataSupplier} to be used
	 * @param org	organization
	 * @param div	division
	 * @param srvc	service
	 * @param loc	location
	 * @param lang	language
	 * @return	notifer data supplier
	 */
	public AA14NotifierTemplateModelDataSupplier dataSupplierFor(final AA14OrganizationID org,
																 final AA14OrgDivisionID div,
																 final AA14OrgDivisionServiceID srvc,
																 final AA14OrgDivisionServiceLocationID loc,
									  							 final Language lang) {
		AA14NotifierTemplateModelDataSupplier outSupplier = null;
		if (srvc.is(AA14BusinessConfigForTrafikoa.SERVICE_ID)) {
			outSupplier = new AA14NotifierTemplateModelDataSupplierForTrafikoa();
		}
		else if (srvc.isContainedIn(AA14BusinessConfigForBizilagun.SERVICE_COMUNIDADES_ALQUILERES_ID,
						   			AA14BusinessConfigForBizilagun.SERVICE_FIANZAS_DEPOSITO_CONTRATOS_ID)) {
			outSupplier = new AA14NotifierTemplateModelDataSupplierForBizilagun();
		}
		else if (srvc.is(AA14BusinessConfigForBloodDonation.SERVICE_ID)) {
			outSupplier = new AA14NotifierTemplateModelDataSupplierForDonations();
		}
		else if (srvc.isContainedIn(AA14BusinessConfigForMedicalService.SERVICE_DOCTOR_VISIT_ID,
						   			AA14BusinessConfigForMedicalService.SERVICE_NURSE_VISIT_ID)) {
			outSupplier = new AA14NotifierTemplateModelDataSupplierForMedicalService();
		}
		else if (srvc.is(AA14BusinessConfigForZuzenean.SERVICE_ID)) {
			outSupplier = new AA14NotifierTemplateModelDataSupplierForZuzenean();
		}
		else if (srvc.is(AA14BusinessConfigForJustizia.SERVICE_ID)) {
			outSupplier = new AA14NotifierTemplateModelDataSupplierForJustizia();
		}
		else {
			throw new IllegalArgumentException(Strings.customized("The given service {} is NOT one of the supported ones: {}, {}, {}, {}, {}, {}, {}", 
										       srvc,
											   AA14BusinessConfigForTrafikoa.SERVICE_ID,
										       AA14BusinessConfigForBizilagun.SERVICE_COMUNIDADES_ALQUILERES_ID,
										       AA14BusinessConfigForBizilagun.SERVICE_FIANZAS_DEPOSITO_CONTRATOS_ID, 
										       AA14BusinessConfigForBloodDonation.SERVICE_ID ,
										       AA14BusinessConfigForMedicalService.SERVICE_DOCTOR_VISIT_ID, 
										       AA14BusinessConfigForMedicalService.SERVICE_NURSE_VISIT_ID,
										       AA14BusinessConfigForZuzenean.SERVICE_ID,
										       AA14BusinessConfigForJustizia.SERVICE_ID));
		}
		return outSupplier;
	}
}
