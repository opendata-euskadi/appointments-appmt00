package aa14a.ui.portal;

import javax.servlet.http.HttpServlet;

import aa14a.ui.servlet.AA14CalendarPrintServlet;
import aa14a.ui.servlet.AA14ControllerServlet;
import aa14a.ui.servlet.AA14ModelObjectsLoadServlet;
import r01f.locale.Language;
import r01f.util.types.Strings;
import r01p.portal.appembed.R01PPortalPageAppEmbedContextDefaults;
import r01p.portal.appembed.R01PPortalPageAppEmbedServletFilter;
import r01p.portal.appembed.config.R01PPortalPageAppEmbedServletFilterConfig;
import r01p.portal.common.R01PPortalOIDs.R01PPortalID;
import r01p.portal.common.R01PPortalOIDs.R01PPortalPageID;

public class AA14AppEmbedServletFilter
     extends R01PPortalPageAppEmbedServletFilter {
/////////////////////////////////////////////////////////////////////////////////////////
//    CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public AA14AppEmbedServletFilter() {
        super(_createAppEmbedConfig());
        // optionally set the portal page loader 
        // ..by default the filesystem page loader is used
        //   where:
        //		- pages working copy root path: /datos/r01hp/file/aplic/
        //		- pages live copy path root path: /datos/r01hp/file/aplic/
        //		- container pages rel path: /html/pages/portal
    }
/////////////////////////////////////////////////////////////////////////////////////////
//    
/////////////////////////////////////////////////////////////////////////////////////////
    private static R01PPortalPageAppEmbedServletFilterConfig _createAppEmbedConfig() {
    	R01PPortalID defPortal = R01PPortalID.forId("web01");
    	R01PPortalPageID defPage = R01PPortalPageID.forId("ejeduki");
    	Language defLang = Language.DEFAULT;
    	R01PPortalPageAppEmbedContextDefaults defs = new R01PPortalPageAppEmbedContextDefaults(defPortal,defPage,defLang,
                                                                                               "r01hpPortalCookie");
        return new R01PPortalPageAppEmbedServletFilterConfig(defs)
                                // not portal-page embedded urls
                                .withNotPortalEmbeddedUrlPatterns(_notPortalEmbeddedUrlPatternFor(AA14ControllerServlet.class),
                                								  _notPortalEmbeddedUrlPatternFor(AA14CalendarPrintServlet.class),
                                                                  _notPortalEmbeddedUrlPatternFor(AA14ModelObjectsLoadServlet.class));
    }
    private static <S extends HttpServlet> String _notPortalEmbeddedUrlPatternFor(final Class<S> servletType) {
    	return Strings.customized("^/aa14aUIWar/{}/?.*",
    							  servletType.getSimpleName());
    }
}
