@rem ConfigureJDBC
@rem (c) Copyright IBM Corp. 2015 
@rem US Government Users Restricted Rights - Use duplication or
@rem disclosure restricted by GSA ADP Schedule Contract with
@rem IBM Corp


@echo off
CALL "%ProgramFiles%\IBM\IIB\10.0.0.0\server\bin\mqsiprofile.cmd"
@if '%1'=='' goto usage

SET BROKER=TESTNODE_iibuser
SET DSN=AUDITDB
SET UID=iibuser
SET PWD=passw0rd
@if '%2' NEQ '' set DSN=%2
@if '%3' NEQ '' set BROKER=%3
@echo on
@rem mqsireportproperties %BROKER% -c JDBCProviders -o DB2 -r
mqsicreateconfigurableservice %BROKER% -c JDBCProviders -o %DSN% -n databaseName -v %DSN%
mqsichangeproperties %BROKER% -c JDBCProviders -o %DSN% -n databaseType -v DB2
mqsichangeproperties %BROKER% -c JDBCProviders -o %DSN% -n connectionUrlFormat -v "jdbc:db2://[serverName]:[portNumber]/[databaseName]:user=[user];password=[password];"
mqsichangeproperties %BROKER% -c JDBCProviders -o %DSN% -n serverName -v localhost
mqsichangeproperties %BROKER% -c JDBCProviders -o %DSN% -n portNumber -v 50000
mqsichangeproperties %BROKER% -c JDBCProviders -o %DSN% -n jarsURL -v "C:\IBM\SQLLIB\java"
mqsichangeproperties %BROKER% -c JDBCProviders -o %DSN% -n type4DatasourceClassName -v com.ibm.db2.jcc.DB2XADataSource
mqsichangeproperties %BROKER% -c JDBCProviders -o %DSN% -n type4DriverClassName -v com.ibm.db2.jcc.DB2Driver
mqsisetdbparms %BROKER% -n %DSN% -u %UID% -p %PWD%
mqsisetdbparms %BROKER% -n jdbc::JDBC -u %UID% -p %PWD% 
mqsistop %BROKER%
mqsistart %BROKER%
@rem mqsireportproperties %BROKER% -c JDBCProviders -a -o AllReportableEntityNames
mqsireportproperties %BROKER% -c JDBCProviders -o %DSN% -r
@goto end
:usage
@echo Usage: ConfigureJDBC [DatabaseName] [BrokerName]
:end
