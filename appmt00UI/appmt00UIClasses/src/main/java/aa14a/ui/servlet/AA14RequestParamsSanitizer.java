package aa14a.ui.servlet;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.util.types.StringConverter.StringConverterFilter;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class AA14RequestParamsSanitizer {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	protected static PolicyFactory policy =   (Sanitizers.FORMATTING)
										  .and(Sanitizers.BLOCKS);
//										  .and(Sanitizers.LINKS);		// do NOT escape @ character 
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static StringConverterFilter FILTER = new StringConverterFilter() {
															@Override
															public String filter(final String untrustedHtml) {
																String safeHtml = policy.sanitize(untrustedHtml);																					
																return safeHtml.replace("&#64;","@");		// mega-�apa for emails
//																					return safeHtml;
															}
											   		};
}
