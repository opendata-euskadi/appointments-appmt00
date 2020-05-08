package aa14b.bootstrap.core;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.inject.Singleton;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;

import aa14b.calendar.AA14CalendarService;
import aa14b.calendar.AA14CalendarServiceOrchestraImpl;
import aa14b.calendar.AA14QMaticOrchestraMediator;
import aa14f.common.internal.AA14AppCodes;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.types.Path;
import r01f.types.url.Host;
import r01f.types.url.Url;
import r01f.types.url.UrlPath;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLProperties;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import r01f.xmlproperties.annotations.XMLPropertiesComponent;

@Slf4j
@EqualsAndHashCode				// This is important for guice modules
public class AA14CalendarGuiceModule 
  implements Module {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14CalendarGuiceModule() {
		// default no-args constructor
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MODULE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void configure(final Binder binder) {
		// Calendar service
		binder.bind(AA14CalendarService.class)
			  .to(AA14CalendarServiceOrchestraImpl.class)
//			  .to(AA14CalendarServiceMockImpl.class)	
			  .in(Singleton.class);
		
		// Orchestra mediator & properties are provided via @Provides annotated method
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  AA14QMaticOrchestraMediator Provider
/////////////////////////////////////////////////////////////////////////////////////////
	@Provides @SuppressWarnings("static-method") 
	private AA14QMaticOrchestraMediator _provideOrchestraMediator(@XMLPropertiesComponent("calendar") final XMLPropertiesForAppComponent props) {
		// Read the properties
		Host host = props.propertyAt("calendar/service/host").asHost(Host.of("txandak.jakina.ejgvdns"));
		int port = props.propertyAt("calendar/service/post").asInteger(80);
		Charset charset = props.propertyAt("calendar/service/charset").asCharset(Charset.forName("UTF-8"));
		UserCode userCode = props.propertyAt("calendar/service/user").asUserCode(UserCode.forId("superadmin"));
		Password password = props.propertyAt("calendar/service/password").asPassword(Password.forId("ulan"));
		
		Path endpointBaseUrl = props.propertyAt("calendar/service/endpointBaseUrl").asPath();
		
//		Path appointmentBookJsonMessageTemplatePath = PROPS.propertyAt("calendar/messageTemplates/appointmentBookJson")
//														   .asPath("aa14/appointment/AA14AppointmentBookJsonMessageTemplate.vm");
		
		log.debug("QMatic Orchestra REST endpoint: {}",Url.from(host,port,
								   							    endpointBaseUrl != null ? new UrlPath(endpointBaseUrl) : null));
		log.debug("                  Service User: {}",userCode);
		log.debug("              Service Password: {}",password);
		
		// Create a json mapper
		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.registerModule(new JodaDateTimeModule());
		
		// Create the qmatic mediator
		AA14QMaticOrchestraMediator qMaticMediator = new AA14QMaticOrchestraMediator(host,port,charset,
																					 userCode,password,
																					 endpointBaseUrl,
																				 	 jsonMapper);
		return qMaticMediator;
	}
	@Provides @XMLPropertiesComponent("calendar") @SuppressWarnings("static-method")
 	XMLPropertiesForAppComponent provideXMLPropertiesForCalendar(final XMLProperties props) {
 		XMLPropertiesForAppComponent outPropsForComponent = new XMLPropertiesForAppComponent(props.forApp(AA14AppCodes.CORE_APPCODE),
 																							 AppComponent.forId("calendar"));
 		return outPropsForComponent;
 	}
/////////////////////////////////////////////////////////////////////////////////////////
//  JACKSON MODULE USED TO SERIALIZE / DE-SERIALIZE JodaTime's LocalTime OBJECTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static class JodaDateTimeModule 
		         extends SimpleModule {
		private static final long serialVersionUID = -7197077215638345070L;

		public JodaDateTimeModule() {
	        super("aa14JodaDateTimeModule",Version.unknownVersion());
	        this.addSerializer(LocalTime.class,
	        				   new JsonSerializer<LocalTime>() {
										@Override
										public void serialize(final LocalTime time,
															  final JsonGenerator jgen,
															  final SerializerProvider provider) throws IOException,
															  											JsonProcessingException {
											jgen.writeString(Strings.customized("{}:{}",
																				String.format("%02d",time.getHourOfDay()),String.format("%02d",time.getMinuteOfHour())));
										}
	        				   });
	        this.addDeserializer(LocalTime.class,
	        					 new JsonDeserializer<LocalTime>() {
										@Override
										public LocalTime deserialize(final JsonParser jsonParser,
																	 final DeserializationContext ctxt) throws IOException,
																	 										   JsonProcessingException {
											LocalTime outLocalTime = null;
								            JsonToken currentToken = jsonParser.getCurrentToken();
								            if (currentToken == JsonToken.VALUE_STRING) {
								                String dateTimeAsString = jsonParser.getText().trim();
								                DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
								                outLocalTime = fmt.parseLocalTime(dateTimeAsString);
								            }
									        if (outLocalTime == null) throw new IllegalStateException();
									        return outLocalTime;
										}
	        					
	        					 });
	    }
	}
}
