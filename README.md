# Experimental OSISoft PI SQL dialect

This plugin provides experimental connectivity to OSISoft PI through JDBC by using PIOLEDB.
You need to have a PI SQL Data Access Server.

This plugin is Copyright (c) Dataiku 2019 and is made available under the Apache Software License.

## Installation instructions

* Install the plugin
* Restart DSS (else, the first job may fail)
* Create a new JDBC connection
   * Driver class: com.osisoft.jdbc.Driver
   * JDBC URL: `jdbc:pioledb://YOUR_PISQL_DATA_ACCESS_SERVER/Data Source=YOUR_DATA_SOURCE_NAME; Integrated Security=SSPI`
   * Property: `user`
   * Property: `password`
   * Property: `ProtocolOrder` with value `Https/Soap:5461` (or another port if you use another port) 
   * SQL dialect: OSISoft PI

## Features and limitations

Using this dialect, you can connect to the main tables exposed by the PIOLEDB provider.

The following features are supported:

* You can create SQL Table datasets for most of the PIOLEDB provider tables, either by entering table name or by listing tables (see below for limitations)
* You can create SQL Table datasets using the Connections explorer
* You can create SQL Query datasets
* You can create SQL Query recipes based on the PI tables. Beware that write is not supported, you'll need to use another connection as output of your recipes
* You can create basic visual recipes using in-database engine. Very basic tests have been performed with the Grouping and Join recipes. The same limitation about output applies.
* You can use the SQL notebook

The following are not supported:

* Any kind of write-back to PI. When creating output datasets, create them in other connections
* In-database charts
* Window recipe with in-database engine

The following limitations have been observed:

* Some of the PIOLEDB tables like `piinterp, pimin, pimax, piavg, pitotal or pistd` cannot be read entirely (i.e. you can't `SELECT * FROM table` on them). For these,  use a SQL Query dataset, and specify a restriction on `time`
* Connection establishment is slow, and DSS uses it a lot. Working with PI data can thus be slow. We recommend that you synchronize data out of PI as early as possible in your Flows
* When using the DSS capabilities to list tables (either listing tables in SQL table dataset creation, using the Connections explorer), the schema won't appear (instead, "null" or "undefined" may appear). This apepars harmless. You can either ignore it or manually set the schema (which corresponds to the PIOLEDB catalogs)
* In the join recipe, LEFT JOIN is not supported. Use INNER JOIN instead.
* On the `picomp2` table, the length of the `value` field appears as zero, which can cause issues with subsequent databases. We recommend changing the length of this field in the source dataset. Other tables and fields may be affected

The following have not been validated:

* In-database engine for other visual recipes (filter, distinct, split, top n, sort, pivot, stack)

## Build instructions

* export the DKUINSTALLDIR variable to the location of your DSS data dir
* Run `make`
* The plugin is available in `dist`

## Changelog

### Version 0.0.1 (2019/08/01)

* Initial release