# appmt: Appointments

Eclipse development environment:

1. follow the [Installation guide (maven)](https://github.com/opendata-euskadi/fabric-r01f/blob/master/docs/eclipse/install/eclipse_maven_install.md9)

2. Clone the following repos

[fabric-r01f](https://github.com/opendata-euskadi/fabric-r01f.git)  
[fabric-aspect](https://github.com/opendata-euskadi/fabric-aspect.git)  
[fabric-r01fBusinessServices](https://github.com/opendata-euskadi/fabric-r01fBusinessServices.git)  
[fabrid-r01fCOREServices](https://github.com/opendata-euskadi/fabric-r01fCOREServices.git)  

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
	
5. Configure a hotdeploy tomcat

[Configure a hotswap jdk](https://github.com/opendata-euskadi/fabric-r01f/blob/master/docs/java/java-hotswap.md)  
Configure a [tomcat] server using the previously configured [hotswap jdk]  
... do NOT forget to set the [tomcat] jvm argument that enable [aspectj weaving]:
    -javaagent:c:/develop/local-libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true -DR01_Home=c:/develop

6. Configure the DB connection

Look for `aa14b.appointments.dbpersistence.properties.xml` file and review the [db connection] properties and the [schema generation] property  

When started, the [db tables] will be automatically created








