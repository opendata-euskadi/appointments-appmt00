# Initialize the DB

For a clean start

1. DROP ALL TABLES

2. Change the `generationMode` at aa14b.appointments.dbpersistence.xml: at `aa14b.appointments.dbpersistence.properties`  property to `<generationMode>DROP_AND_CREATE_TABLES</generationMode>`

3. Init the db:  /{context-root}/AA14ControllerServlet?op=INIT_DB

4. Restore the `generationMode` at aa14b.appointments.dbpersistence.xml: at `aa14b.appointments.dbpersistence.properties`  property to `<generationMode>NONE</generationMode>`

5. Restart the [app server] <-- **IMPORTANT!!!**
