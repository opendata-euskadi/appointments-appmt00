package aa14b.db.find;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.jpa.JpaQuery;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import aa14b.db.entities.AA14DBEntityForAppointment;
import aa14b.db.entities.AA14DBEntityForBookedSlotBase;
import aa14b.db.entities.AA14DBEntityForNonBookableSlot;
import aa14b.services.internal.AA14BookedSlotSummarizerService;
import aa14f.api.interfaces.AA14FindServicesForBookedSlot;
import aa14f.model.AA14Appointment;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14BookedSlotType;
import aa14f.model.AA14NonBookableSlot;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14PeriodicSlotSerieOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import aa14f.model.search.AA14AppointmentFilter;
import aa14f.model.search.AA14BookedSlotFilter;
import aa14f.model.summaries.AA14SummarizedAppointment;
import aa14f.model.summaries.AA14SummarizedBookedSlot;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.model.persistence.FindOIDsResult;
import r01f.model.persistence.FindOIDsResultBuilder;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindResultBuilder;
import r01f.model.persistence.FindSummariesResult;
import r01f.model.persistence.FindSummariesResultBuilder;
import r01f.objectstreamer.Marshaller;
import r01f.patterns.FactoryFrom;
import r01f.persistence.db.TransformsDBEntityIntoModelObject;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.securitycontext.SecurityContext;
import r01f.types.Range;
import r01f.util.types.collections.CollectionUtils;

/**
 * Persistence layer
 */
@Slf4j
public class AA14DBFindForBookedSlot
	 extends AA14DBFindBase<AA14SlotOID,AA14SlotID,AA14BookedSlot,
	 						AA14DBEntityForBookedSlotBase>
  implements AA14FindServicesForBookedSlot {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14BookedSlotSummarizerService _slotSummarizerService;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBFindForBookedSlot(final DBModuleConfig dbCfg,
								   final EntityManager entityManager,
								   final Marshaller marshaller) {
		this(dbCfg,
			 entityManager,
			 marshaller,
			 null);
	}
	public AA14DBFindForBookedSlot(final DBModuleConfig dbCfg,
								   final EntityManager entityManager,
								   final Marshaller marshaller,
								   final AA14BookedSlotSummarizerService slotSummarizerService) {
		super(dbCfg,
			  AA14BookedSlot.class,AA14DBEntityForBookedSlotBase.class,
			  entityManager,
			  marshaller);
		_slotSummarizerService = slotSummarizerService;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BY SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14BookedSlot> findRangeBookedSlotsFor(final SecurityContext securityContext,
										   					  final AA14ScheduleOID schOid,
										   					  final Range<Date> range) {
		return _findRangeBookedSlotsFor(securityContext,
										null,schOid,
										range);
	}
	@Override
	public FindSummariesResult<AA14BookedSlot> findRangeBookedSlotsSummarizedFor(final SecurityContext securityContext,
																				 final Language lang,
																  				 final AA14ScheduleOID schOid,
																  				 final Range<Date> range) {
		return _findRangeBookedSlotsSummarizedFor(securityContext,
												  lang,
												  null,schOid,
												  range);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BY LOCATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindOIDsResult<AA14SlotOID> findRangeBookedSlotsFor(final SecurityContext securityContext,
															   final AA14OrgDivisionServiceLocationOID locationOid,
										   					   final Range<Date> dateRange,
										   					   final AA14BookedSlotType slotType) {
		return _findRangeBookedSlotsOidsFor(securityContext,
											locationOid,null,
											dateRange,
											slotType);
	}
	@Override
	public FindResult<AA14BookedSlot> findRangeBookedSlotsFor(final SecurityContext securityContext,
										   					  final AA14OrgDivisionServiceLocationOID locationOid,
										   					  final Range<Date> range) {
		return _findRangeBookedSlotsFor(securityContext,
										locationOid,null,
										range);
	}
	@Override
	public FindSummariesResult<AA14BookedSlot> findRangeBookedSlotsSummarizedFor(final SecurityContext securityContext,
																				 final Language lang,
																  				 final AA14OrgDivisionServiceLocationOID locationOid,
																  				 final Range<Date> range) {
		return _findRangeBookedSlotsSummarizedFor( securityContext,
												  lang,
												  locationOid,null,
												  range);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BY LOCATION & SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14BookedSlot> findRangeBookedSlotsFor(final SecurityContext securityContext,
										   					  final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
										   					  final Range<Date> range) {
		return _findRangeBookedSlotsFor(securityContext,
										locationOid,schOid,
										range);
	}
	@Override
	public FindSummariesResult<AA14BookedSlot> findRangeBookedSlotsSummarizedFor(final SecurityContext securityContext,
																				 final Language lang,
																  				 final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
																  				 final Range<Date> range) {
		return _findRangeBookedSlotsSummarizedFor(securityContext,
												  lang,
												  locationOid,schOid,
												  range);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SLOTS OVERLAPPING RANGE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14BookedSlot> findBookedSlotsOverlappingRange(final SecurityContext securityContext,
																	  final AA14ScheduleOID schOid,
																	  final Range<Date> range) {
		if (schOid == null) throw new IllegalArgumentException("The schedule is needed in order to find the overlapping slots!");
		
		// Get an interval to span the day range
		Range<Date> effDateRange = _dateRange(range);
		log.debug("> finding overlapping slots with range {} - {} at SCHEDULE={}",
				  effDateRange.getLowerBound(),effDateRange.getUpperBound(),
				  schOid);
		TypedQuery<AA14DBEntityForBookedSlotBase> query = this.getEntityManager()
															        .createNamedQuery("AA14DBEntitiesForSlotOverlappingRange",
																	  		          AA14DBEntityForBookedSlotBase.class)
																	.setParameter("start",effDateRange.getLowerBound())
																	.setParameter("end",effDateRange.getUpperBound());
		query.setParameter("sch",schOid.asString());

		query.setHint(QueryHints.READ_ONLY,HintValues.TRUE);		
		Collection<AA14DBEntityForBookedSlotBase> dbEntities = query.getResultList();
		
		return FindResultBuilder.using(securityContext)
				          	  	.on(_modelObjectType)
				          	  	.foundDBEntities(dbEntities)
				          	  	.transformedToModelObjectsUsing(this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COMMON
/////////////////////////////////////////////////////////////////////////////////////////
	private FindResult<AA14BookedSlot> _findRangeBookedSlotsFor(final SecurityContext securityContext,
										   					    final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
										   					    final Range<Date> range) {
		// [1] - Get the DB entities
		Collection<AA14DBEntityForBookedSlotBase> dbEntities = _findRangeDBEntitiesForBookedSlotsFor(securityContext,
																									 locationOid,schOid,
																									 range);
		// [2] - Transform
		FindResult<AA14BookedSlot> outEntities = FindResultBuilder.using(securityContext)
													          	  .on(_modelObjectType)
													          	  .foundDBEntities(dbEntities)
													          	  .transformedToModelObjectsUsing(this);
		// [3] - Return
		return outEntities;
	}
	private FindSummariesResult<AA14BookedSlot> _findRangeBookedSlotsSummarizedFor(final SecurityContext securityContext,
																				   final Language lang,
																  				   final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
																  				   final Range<Date> range) {
		// [1] - Get the DB entities
		Collection<AA14DBEntityForBookedSlotBase> dbEntities = _findRangeDBEntitiesForBookedSlotsFor(securityContext,
																									 locationOid,schOid,
																									 range);
		// [2] - Transform to summarized model objects
		FindSummariesResult<AA14BookedSlot> outSummaries = null; 
		outSummaries = FindSummariesResultBuilder.using(securityContext)
												 .on(_modelObjectType)
												 .foundDBEntities(dbEntities)
												 .transformedToSummarizedModelObjectUsing(_slotToSummarizedFunction(securityContext,
														 															lang));
		// [3] - Return
		return outSummaries;
	}
	private Collection<AA14DBEntityForBookedSlotBase> _findRangeDBEntitiesForBookedSlotsFor(final SecurityContext securityContext,
																			   				final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
																			   				final Range<Date> range) {
		if (locationOid == null && schOid == null) throw new IllegalArgumentException("Either the location or schedule is needed in order to find the booked slots!");
		
		// Get the named query to use
		String namedQuery = _namedQuery(locationOid,schOid);
		
		// Get an interval to span the day range
		Range<Date> effDateRange = _dateRange(range);
		log.debug("> finding booked slots at LOCATION={} / SCHEDULE={} within {} and {} using query={}",
				  locationOid,schOid,
				  effDateRange.getLowerBound(),effDateRange.getUpperBound(),
				  namedQuery);
		TypedQuery<AA14DBEntityForBookedSlotBase> query = this.getEntityManager()
															        .createNamedQuery(namedQuery,
																	  		          AA14DBEntityForBookedSlotBase.class)
																	.setParameter("dayStart",effDateRange.getLowerBound())
																	.setParameter("dayEnd",effDateRange.getUpperBound());
		if (locationOid != null) query.setParameter("loc",locationOid.asString());
		if (schOid != null) query.setParameter("sch",schOid.asString());

		query.setHint(QueryHints.READ_ONLY,HintValues.TRUE);		
		Collection<AA14DBEntityForBookedSlotBase> dbEntities = query.getResultList();
		
		return dbEntities;
	}
	private FindOIDsResult<AA14SlotOID> _findRangeBookedSlotsOidsFor(final SecurityContext securityContext,
																 	 final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
																 	 final Range<Date> range,
																 	 final AA14BookedSlotType slotType) {
		if (locationOid == null && schOid == null) throw new IllegalArgumentException("Either the location or schedule is needed in order to find the booked slots!");
		
		// Get the named query
		String namedQuery = _namedQuery(locationOid,schOid);
		
		// Get an interval to span the day range
		Range<Date> effDateRange = _dateRange(range);
		log.debug("> finding booked slots oids at LOCATION={} / SCHEDULE={} within {} and {} using query={}",
				  locationOid,schOid,
				  effDateRange.getLowerBound(),effDateRange.getUpperBound(),
				  namedQuery);

		CriteriaBuilder builder = _entityManager.getCriteriaBuilder();
		
		// use projections to return ONLY the oid (see http://stackoverflow.com/questions/12618489/jpa-criteria-api-select-only-specific-columns)
		CriteriaQuery<Tuple> query = builder.createTupleQuery();
		Root<AA14DBEntityForBookedSlotBase> root = query.from(AA14DBEntityForBookedSlotBase.class);
		query.multiselect(root.get("_oid"));
		
		// Create the predicates
		Collection<Predicate> wherePredicates = Lists.newArrayList();
		if (locationOid != null) {
			Predicate locPredicate = builder.equal(root.<String>get("_orgDivisionServiceLocationOid"),
											   	   locationOid.asString());
			wherePredicates.add(locPredicate);
		}
		if (schOid != null) {
			Predicate schPredicate = builder.equal(root.<String>get("_scheduleOid"),
												   schOid.asString());
			wherePredicates.add(schPredicate);
		}
		if (slotType != null) {
			Predicate typePredicate = builder.equal(root.<AA14BookedSlotType>get("_type"),
												    slotType);
			wherePredicates.add(typePredicate);
		}
		if (effDateRange!=null) {
			if (effDateRange.hasLowerBound() && effDateRange.hasUpperBound()) {
				wherePredicates.add(_entityManager.getCriteriaBuilder()
						.between(root.<AA14BookedSlotType>get("_startDate").as(java.sql.Date.class), 
											  effDateRange.getLowerBound(),
											  effDateRange.getUpperBound()));
			}
			else if (effDateRange.hasLowerBound() && !effDateRange.hasUpperBound()) {
				wherePredicates.add(_entityManager.getCriteriaBuilder()
						.greaterThanOrEqualTo(root.<AA14BookedSlotType>get("_startDate").as(java.sql.Date.class), 
											  effDateRange.getLowerBound()));
			}
			else{
				wherePredicates.add(_entityManager.getCriteriaBuilder()
						.lessThanOrEqualTo(root.<AA14BookedSlotType>get("_startDate").as(java.sql.Date.class), 
											  effDateRange.getUpperBound()));
			}
		}
		// Add the predicates
		if (CollectionUtils.hasData(wherePredicates)) {
			Predicate[] wherePredicateArray = wherePredicates.toArray(new Predicate[wherePredicates.size()]);
			query.where(wherePredicateArray);
		}
		
		// Create the query
		TypedQuery<Tuple> typedQuery = _entityManager.createQuery(query);
		List<Tuple> tupleResult = 				typedQuery
														.setHint(QueryHints.READ_ONLY,HintValues.TRUE)
											    .getResultList();
		System.out.println("Query: "+typedQuery.unwrap(JpaQuery.class).getDatabaseQuery().getSQLString());
		FindOIDsResult<AA14SlotOID> outOids = _buildOIDsResultsFromDBTuples(securityContext,
														  					tupleResult,
														  					new FactoryFrom<Tuple,AA14SlotOID>() {
																					@Override
																					public AA14SlotOID from(final Tuple dbTuple) {
																						String oidAsString = (String)dbTuple.get(0);
																						return AA14SlotOID.forId(oidAsString);
																					}
																			});
		return outOids;
	}
	private static String _namedQuery(final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid) {
		String namedQuery = null;
		if (locationOid != null && schOid != null) {
			namedQuery = "AA14DBEntitiesForSlotByLocationAndSchedule";
		} else if (locationOid != null) {
			namedQuery = "AA14DBEntitiesForSlotByLocation";
		} else if (schOid != null) {
			namedQuery = "AA14DBEntitiesForSlotBySchedule";
		} else {
			throw new IllegalArgumentException();
		}
		return namedQuery;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  APPOINTMENTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindSummariesResult<AA14Appointment> findAppointmentsBy(final SecurityContext securityContext,
																   final AA14AppointmentFilter filter,
																   final Language lang) {
		log.debug("> finding appointments' summaries > {}", 
				  filter.debugInfo());
		
		// [1] - Do the query			
		TypedQuery<AA14DBEntityForAppointment> qry = _entityManager.createQuery(_createCriteriaQuery(filter));
		qry.setHint(QueryHints.READ_ONLY,HintValues.TRUE);
		Collection<AA14DBEntityForAppointment> dbEntities = qry.getResultList();
		log.debug("> ...found {} appointments",dbEntities.size());
		
		// [2] - Transform to summarized model objects
		FindSummariesResult<AA14Appointment> outSummaries = null; 
		outSummaries = FindSummariesResultBuilder.using(securityContext)
												 .on(AA14Appointment.class)			// beware the trick!!! it's not _modelObjectType
												 .foundDBEntities(dbEntities)
												 .transformedToSummarizedModelObjectUsing(_appointmentToSummarizedFunction(securityContext,
														 																   lang));
		// [3] - Return
		return outSummaries;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BOOKED SLOTS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public FindOIDsResult<AA14SlotOID> findBookedSlotsBy(final SecurityContext securityContext,
														 final AA14BookedSlotFilter filter) {
		log.debug("> findig booked slots oids > {}", 
				  filter.debugInfo());
		// [1] - Do the query			
		TypedQuery<AA14DBEntityForBookedSlotBase> qry = _entityManager.createQuery(_createCriteriaQuery(filter));
		qry.setHint(QueryHints.READ_ONLY,HintValues.TRUE);
		Collection<AA14DBEntityForBookedSlotBase> dbEntities = qry.getResultList();
		log.debug("> ...found {} slots",dbEntities.size());
		
		// [2] - Transform to summarized model objects
		FindOIDsResult<AA14SlotOID> outOids = null; 
		outOids = FindOIDsResultBuilder.using(securityContext)
									   .on(AA14Appointment.class)			// beware the trick!!! it's not _modelObjectType
									   .foundDBEntities(dbEntities,
											   			new Function<AA14DBEntityForBookedSlotBase,AA14SlotOID>() {
																@Override
																public AA14SlotOID apply(final AA14DBEntityForBookedSlotBase dbEntity) {
																	return AA14SlotOID.forId(dbEntity.getOid());
																}
									   					});
		// [3] - Return
		return outOids;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindOIDsResult<AA14SlotOID> findNonBookablePeriodicSlotsOids(final SecurityContext securityContext,
																	    final AA14PeriodicSlotSerieOID serieOid) {
		log.debug("> finding non-bookable periodic slots related by serieOid={}",
				  serieOid);
		// [1] - Get the DBEntities
		Collection<AA14DBEntityForNonBookableSlot> dbEntities = _findNonBookablePeriodicSlotDBEntities(serieOid);
		
		// [2] - Transform
		FindOIDsResult<AA14SlotOID> outEntitiesOids = FindOIDsResultBuilder.using(securityContext)
																.on(_modelObjectType)
																.foundEntitiesWithOids(FluentIterable.from(dbEntities)
																									 .transform(new Function<AA14DBEntityForNonBookableSlot,AA14SlotOID>() {
																														@Override
																														public AA14SlotOID apply(final AA14DBEntityForNonBookableSlot dbEntity) {
																															return AA14SlotOID.fromString(dbEntity.getOid());
																														}
																									 			})
																									 .toList());
		// [3] - Return
		return outEntitiesOids;
	}
	@Override
	public FindResult<AA14NonBookableSlot> findNonBookablePeriodicSlots(final SecurityContext securityContext,
																	    final AA14PeriodicSlotSerieOID serieOid) {
		log.debug("> finding non-bookable periodic slots related by serieOid={}",
				  serieOid);
		// [1] - Get the DBEntities
		Collection<AA14DBEntityForNonBookableSlot> dbEntities = _findNonBookablePeriodicSlotDBEntities(serieOid);
		
		// [2] - Transform
		FindResult<AA14NonBookableSlot> outEntities = FindResultBuilder.using(securityContext)
													          	  .on(_modelObjectType)
													          	  .foundDBEntities(dbEntities)
													          	  .transformedToModelObjectsUsing(new TransformsDBEntityIntoModelObject<AA14DBEntityForNonBookableSlot,AA14NonBookableSlot>() {
																											@Override @SuppressWarnings("hiding")
																											public AA14NonBookableSlot dbEntityToModelObject(final SecurityContext securityContext,
																																							 final AA14DBEntityForNonBookableSlot dbEntity) {
																												return _modelObjectsMarshaller.forReading().fromXml(dbEntity.getDescriptor(),
																																									AA14NonBookableSlot.class);
																											}
													          	  								  });
		// [3] - Return
		return outEntities;
	}
	private Collection<AA14DBEntityForNonBookableSlot> _findNonBookablePeriodicSlotDBEntities(final AA14PeriodicSlotSerieOID serieOid) {
		TypedQuery<AA14DBEntityForNonBookableSlot> query = this.getEntityManager()
															        .createNamedQuery("AA14DBEntityForNonBookablePeriodicSlotBySerieOid",
																	  		          AA14DBEntityForNonBookableSlot.class)
																	.setParameter("serieOid",serieOid.asString());
		query.setHint(QueryHints.READ_ONLY,HintValues.TRUE);		
		Collection<AA14DBEntityForNonBookableSlot> dbEntities = query.getResultList();
		return dbEntities;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private Function<AA14DBEntityForBookedSlotBase,
					 AA14SummarizedBookedSlot> _slotToSummarizedFunction(final SecurityContext securityContext,
							 											 final Language lang) {
		return new Function<AA14DBEntityForBookedSlotBase,
							AA14SummarizedBookedSlot>() {
						@Override
						public AA14SummarizedBookedSlot apply(final AA14DBEntityForBookedSlotBase dbEntity) {
							// Get the appointment from the db entity 
							AA14BookedSlot slot = AA14DBFindForBookedSlot.this.dbEntityToModelObject(securityContext,
																		 							 dbEntity);
							return _slotSummarizerService.summarizedSlotFor(securityContext, 
																			slot,
																			lang);
						}
 			   };
	}
	private Function<AA14DBEntityForAppointment,
					 AA14SummarizedAppointment> _appointmentToSummarizedFunction(final SecurityContext securityContext,
							 													 final Language lang) {
		return new Function<AA14DBEntityForAppointment,
							AA14SummarizedAppointment>() {
						@Override
						public AA14SummarizedAppointment apply(final AA14DBEntityForAppointment dbEntity) {
							// Get the slot from the db entity 
							AA14BookedSlot slot = AA14DBFindForBookedSlot.this.dbEntityToModelObject(securityContext,
																		    						 dbEntity);
							return _slotSummarizerService.summarizedAppointmentFor(securityContext, 
																				   (AA14Appointment)slot,
																				   lang);
						}
 			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static Range<Date> _dateRange(final Range<Date> range) {
		Date lowerDate = range.getLowerBound() != null ? range.getLowerBound() 
													   : range.getUpperBound() != null ? range.getUpperBound() : new Date();
		Date upperDate = range.getUpperBound() != null ? range.getUpperBound() 
													   : range.getLowerBound() != null ? range.getLowerBound() : new Date();
		return Range.closed(lowerDate,upperDate);
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Builds a query with different criteria to be used in find methods that filters by two dates for the start date
	 * Null parameters will not be included in the where clause but its expected to be
	 * able to include at least the dateRange as a condition
	 * @param personId	person id. Optional
	 * @param subjectId subject id. Optional
	 * @param dateRange date range filter. Must not be null
	 * @return query with the specified criteria
	 * @throws IllegalArgumentException if dateRange is null
	 */
	private CriteriaQuery<AA14DBEntityForAppointment> _createCriteriaQuery(final AA14AppointmentFilter filter) {
		CriteriaBuilder crBldr = _entityManager.getCriteriaBuilder();
		
		// create the predicates
		Collection<Predicate> predicateList = Lists.newArrayListWithExpectedSize(3);
		CriteriaQuery<AA14DBEntityForAppointment> crQry = _entityManager.getCriteriaBuilder()
																		.createQuery(AA14DBEntityForAppointment.class); 
		Root<AA14DBEntityForAppointment> appointment = crQry.from(AA14DBEntityForAppointment.class);
		
		// Organization & Division & Service & location
		Collection<Predicate> organizationalPredicates = _buildOrganizationalPredicates(crBldr,
																						 appointment,
																						 null,null,filter.getServiceId(),filter.getServiceLocationId());
		if (CollectionUtils.hasData(organizationalPredicates)) predicateList.addAll(organizationalPredicates);
		
		// Date Range
		Collection<Predicate> dateRangePredicates = _buildDateRangePredicates(crBldr,
																			 appointment,
																			 filter.getDateRange());
		if (CollectionUtils.hasData(dateRangePredicates)) predicateList.addAll(dateRangePredicates);
		
		// Person & locator
		if (filter.getPersonLocatorId() != null) {
			predicateList.add(crBldr.equal(crBldr.upper(appointment.get("_personLocatorId").as(String.class)), 
							  crBldr.upper(crBldr.literal(filter.getPersonLocatorId().asString()))));
		}
		if (filter.getPersonId() != null) {
			predicateList.add(crBldr.equal(crBldr.upper(appointment.get("_personId").as(String.class)), 
							  crBldr.upper(crBldr.literal(filter.getPersonId().asString()))));
		}
		
		// Subject
		if (filter.getSubjectId() != null) {
			predicateList.add(crBldr.equal(crBldr.upper(appointment.get("_subjectId").as(String.class)), 
							  crBldr.upper(crBldr.literal(filter.getSubjectId().asString()))));
		}
		
		// compose the query
		crQry.select(appointment);
		if (CollectionUtils.hasData(predicateList)) {
			Predicate[] wherePredicateArray =predicateList.toArray(new Predicate[predicateList.size()]);
			crQry.where(wherePredicateArray);
		}
		// order 
		crQry.orderBy(_entityManager.getCriteriaBuilder().asc(appointment.get("_startDate").as(java.sql.Date.class)));
		return crQry;
	}
	/**
	 * Builds a query with different criteria to be used in find methods that filters by two dates for the start date
	 * Null parameters will not be included in the where clause but its expected to be
	 * able to include at least the dateRange as a condition
	 * @param personId	person id. Optional
	 * @param subjectId subject id. Optional
	 * @param dateRange date range filter. Must not be null
	 * @return query with the specified criteria
	 * @throws IllegalArgumentException if dateRange is null
	 */
	private CriteriaQuery<AA14DBEntityForBookedSlotBase> _createCriteriaQuery(final AA14BookedSlotFilter filter) {
		CriteriaBuilder crBldr = _entityManager.getCriteriaBuilder();
		
		// create the predicates
		Collection<Predicate> predicateList = Lists.newArrayListWithExpectedSize(3);
		CriteriaQuery<AA14DBEntityForBookedSlotBase> crQry = _entityManager.getCriteriaBuilder()
																		   .createQuery(AA14DBEntityForBookedSlotBase.class); 
		Root<AA14DBEntityForBookedSlotBase> slot = crQry.from(AA14DBEntityForBookedSlotBase.class);
		
		// Organization & Division & Service & location
		Collection<Predicate> organizationalPredicates = _buildOrganizationalPredicates(crBldr,
																						slot,
																						filter.getOrganizationId(),filter.getDivisionId(),filter.getServiceId(),filter.getServiceLocationId());
		if (CollectionUtils.hasData(organizationalPredicates)) predicateList.addAll(organizationalPredicates);
		
		// Date Range
		Collection<Predicate> dateRangePredicates = _buildDateRangePredicates(crBldr,
																			  slot,
																			  filter.getDateRange());
		if (CollectionUtils.hasData(dateRangePredicates)) predicateList.addAll(dateRangePredicates);
		
		// slot type
		In<AA14BookedSlotType> inPredicate = _buildSlotTypePredicate(crBldr,
																	 slot,
																	 filter.getBookedSlotTypes());
		if (inPredicate != null) predicateList.add(inPredicate);
		
		// compose the query
		crQry.select(slot);
		if (CollectionUtils.hasData(predicateList)) {
			Predicate[] wherePredicateArray = predicateList.toArray(new Predicate[predicateList.size()]);
			crQry.where(wherePredicateArray);
		}
		// order 
		crQry.orderBy(_entityManager.getCriteriaBuilder().asc(slot.get("_startDate").as(java.sql.Date.class)));
		return crQry;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private static <DB extends AA14DBEntityForBookedSlotBase> Collection<Predicate> _buildOrganizationalPredicates(final CriteriaBuilder crBldr,
																  											 	   final Root<DB> root,
																  											 	   final AA14OrganizationID orgId,final AA14OrgDivisionID divId,final AA14OrgDivisionServiceID srvcId,final AA14OrgDivisionServiceLocationID locId) {
		Collection<Predicate> predicateList = Lists.newArrayList();
		
		if (orgId != null) {
			predicateList.add(crBldr.equal(root.get("_organizationId"), 
							  orgId.asString()));			
		}
		if (divId != null) {
			predicateList.add(crBldr.equal(root.get("_orgDivisionId"), 
							  divId.asString()));			
		}
		if (srvcId != null) {
			predicateList.add(crBldr.equal(root.get("_orgDivisionServiceId"), 
							  srvcId.asString())); 
		}
		if (locId != null) {
			predicateList.add(crBldr.equal(root.get("_orgDivisionServiceLocationId"), 
							  locId.asString())); 
		}
		return predicateList;
	}
	private static <DB extends AA14DBEntityForBookedSlotBase> Collection<Predicate> _buildDateRangePredicates(final CriteriaBuilder crBldr,
																  											  final Root<DB> root,
																  											  final Range<Date> dateRange) {
		if (dateRange == null) return null;
		Collection<Predicate> predicateList = Lists.newArrayList();
		
		if (dateRange.hasLowerBound() && dateRange.hasUpperBound()) {
			predicateList.add(crBldr.between(root.get("_startDate").as(java.sql.Date.class), 
											 dateRange.getLowerBound(),
											 dateRange.getUpperBound()));
		}
		else if (dateRange.hasLowerBound() && !dateRange.hasUpperBound()) {
			predicateList.add(crBldr.greaterThanOrEqualTo(root.get("_startDate").as(java.sql.Date.class), 
										  				  dateRange.getLowerBound()));
		}
		else {
			predicateList.add(crBldr.lessThanOrEqualTo(root.get("_startDate").as(java.sql.Date.class), 
										  			   dateRange.getUpperBound()));
		}
		return predicateList;
	}
	private static <DB extends AA14DBEntityForBookedSlotBase> In<AA14BookedSlotType> _buildSlotTypePredicate(final CriteriaBuilder crBldr,
																  											 final Root<DB> root,
																  											 final Collection<AA14BookedSlotType> slotTypes) {
		if (slotTypes == null) return null;
		
		In<AA14BookedSlotType> in = crBldr.in(root.get("_type").as(AA14BookedSlotType.class));
		for (AA14BookedSlotType type : slotTypes) {
			in.value(type);
		}
		return in;
	}
}
