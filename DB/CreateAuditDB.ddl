-- drop, create and connect to AuditDB@
echo *******************************************************@
echo the following command may fail because the database did@
echo NOT previously exist, this is an acceptable error@
echo *******************************************************@
drop database AuditDB  @
create database AuditDB   @
connect to AuditDB   @

-- catalog AuditDB as a odbc data source
catalog system odbc data source AuditDB  @

-- create the schema
CREATE SCHEMA IIBADMIN  @


commit @

-- Connect to AuditDB@
connect to AuditDB @

DROP TABLE IIBADMIN.LOGTABLE  @


CREATE TABLE IIBADMIN.LOGTABLE
(
    ID INT NOT NULL GENERATED ALWAYS 
      AS IDENTITY 
      (START WITH 1 
       INCREMENT BY 1 
       MINVALUE 1 
       NO MAXVALUE 
       NO CYCLE 
       NO CACHE 
       ORDER),
	   
	SFID VARCHAR(30),   
    NAME VARCHAR(100), 
    DEPT VARCHAR(10),   
    OWNER VARCHAR(30) NOT NULL 
)
DATA CAPTURE NONE     @
                                           
ALTER TABLE LOGTABLE ADD CONSTRAINT LOGTABLE PRIMARY KEY (ID)    @

GRANT ALL ON IIBADMIN.LOGTABLE TO IIBUSER  @

commit  @