# appmt: Appointments

## Eclipse development environment:

1. follow the [Installation guide (maven)](https://github.com/opendata-euskadi/fabric-r01f/blob/master/docs/eclipse/install/eclipse_maven_install.md9)

2. Clone the following repos

[fabric-r01f](https://github.com/opendata-euskadi/fabric-r01f.git)  
[fabric-aspect](https://github.com/opendata-euskadi/fabric-aspect.git)  
[fabric-r01fBusinessServices](https://github.com/opendata-euskadi/fabric-r01fBusinessServices.git)  
[fabric-r01fCOREServices](https://github.com/opendata-euskadi/fabric-r01fCOREServices.git)  

3. Import projects in [eclipse]  
Tips:  
	- use [maven] > [update project] a few times until the projects compile  
	- start with [r01fProperties]  
	- next [r01fBase]   
	- next all projects  
	- Usually [r01fXXAspectClasses] need an independent build: just select the projects manually and issue manual build: [project] > [clean] (*only selected projects*) 
	
	
4. Clone appointment repos

[appointments-appmt00](https://github.com/opendata-euskadi/appointments-appmt00.git)  
[appointments-appmt01](https://github.com/opendata-euskadi/appointments-appmt01.git)  
[appointments-appmt00ConfigByEnv](https://github.com/opendata-euskadi/appointments-appmt00ConfigByEnv.git)
	
5. Configure a hotdeploy tomcat

[Configure a hotswap jdk](https://github.com/opendata-euskadi/fabric-r01f/blob/master/docs/java/java-hotswap.md)  
Configure a [tomcat] server using the previously configured [hotswap jdk]  
... do NOT forget to set the [tomcat] jvm argument that enable [aspectj weaving]:

    -javaagent:c:/develop/local-libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true -DR01_Home=c:/develop -DR01-ENV=default

6. Configure the DB connection

Look for `aa14b.appointments.dbpersistence.properties.xml` file and review the [db connection] properties and the [schema generation] property  

When started, the [db tables] will be automatically created

7. Configure the notifier properties

Look for `aa14b.appointments.notifier.properties.xml`file 



## Properties

Properties are splitted in TWO parts  
- Environment independent properties: located at [appmt00/appmt00Config]  
- Environment dependent properties: located at [appmt00ConfigByEnv]  

Usually the [env INdependent] properties contains properties that DO NOT change within environments while the [env dependent] properties contains those properties specific to a certain environment (say, dev, test or prod): things like paths, users, passwords, etc

When started, the system _mixes_ the [env INdependent] files with the [env dependent] files to create a single file

The [env INdependent] properties layout is like

	[xxConfig]
		+ properties
	
Then [env dependent] properties are stored in a single repo and splitted by [env id] like:

	[xxConfigByEnv]
		+ default
			+ properties
		+ des
			+ properties
		+ test
			+ properties
		+ prod
			+ properties

All properties files are bundled as JARs inside the app so there will exist TWO properties JARs

	+ xxConfig.jar 
	+ xxConfigByEnv.jar

... so the app artifact will contain properties for ALL environments and a _clue_ to know which environment properties to use is needed. Use a JVM argument named R01-ENV={env name} when starting the appserver ie: `-DR01-ENV=des`

### DB connection

The DB connection is usually configured at the `aa14b.appointments.dbpersistence.properties` properties file.
The [env-INdependent] file usually does NOT need to be modified; set the [db connection] at the [env-dependent] file: `{env}/aa14b.appointments.dbpersistence.properties`

Set the connection type: DRIVER MANAGER or DATASOURCE (_only for app servers like weblogic_)

	<persistence unitType='DRIVER_MANAGER / DATASOURCE' ...>

Depending on the configured `unitType` look for the corresponding `<unit type=`> section

For example, for a `DRIVER_MANAGER` `MySQL` the connection properties are:

	<unit type='DRIVER_MANAGER'
		  targetDB='MySQL 8.0'		<-- ie Oracle 11.0 or MySQL 8.0

		<connection vendor='MySQL'>
			<javax.persistence.jdbc.user>pci</javax.persistence.jdbc.user>
			<javax.persistence.jdbc.password>pci</javax.persistence.jdbc.password>
			<javax.persistence.jdbc.driver>com.mysql.cj.jdbc.Driver</javax.persistence.jdbc.driver>
			<javax.persistence.jdbc.url><![CDATA[jdbc:mysql://localhost:3306/pci?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC]]></javax.persistence.jdbc.url>
		</connection>
	</unit>

BEWARE that the db-tables are auto-created / extended; use the `schema/generationMode` property:

	<generationMode>CREATE_OR_EXTEND_TABLES</generationMode>	<!-- NONE / DROP_AND_CREATE_TABLES / CREATE_OR_EXTEND_TABLES / DROP_TABLES / CREATE_TABLES -->

When tables are created, a file with the `ddl-script` is created at `{dev-home}/db/ddl-scripts/aa14b` so ensure this folder exists or the tables will not be created







