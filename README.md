# SQL DataTable portlet for Liferay
Aimed to deliver a simple and fast option to analyze multiple databases simultaneously, this portlet can execute plain SQL queries using multiple connection pools ("pool of pools"), even across diferent database engines. To render the results the portlet use JQuery DataTables.

DataTables is a popular JQuery plugin designed to enhance the accessibility of data in HTML tables, providing pagination, sort and search capabilities, with several options and extensions to support a wide variety of end user and developer requirements.

To support millions of records, a server side processing was implemented to sort, search and paginate the results using database capabilities. But the portlet can switch the processing to the client automaticaly to reduce AJAX calls and improve the response for small tables (less than 10000 records).

This is a spin-off of a larger project, with simplified configuration and without aditional infraestructure requirements. It lacks advanced metadata management, query and connection abstractions and caching mechanisms.

## Database support
Supports the same databases that Liferay Portal support, including MySQL, PostgreSQL, DB2, Oracle, SQL Server, etc.. Also can be configured to work with C3P0, DHCP or Tomcat connection pool libraries.

DB2, Oracle and SQL Server databases could require especial SQL syntaxes to support server side processing for pagination.

## Current status
First pre-release. The first release will support only Liferay 6.2+, targeting MySQL and PostgreSQL with CP3P0.

## Source Code
This is the main source code repository of the project.

## Quick Start
You need a running Liferay Portal 6.2+ to deploy this portlet. A pre-build portal can be found at [Liferay download page](http://liferay.com/downloads), follow the Liferay Portal [Quick Start](http://liferay.com/quick-start) for more instructions.

Download the .lpk file from the [Liferay Marketplace](http://www.liferay.com/marketplace), or from this repository.

Go to the portal Control Panel / Apps / App Manager / Install, select the .lpk and click on Install. The portlet will be available to place on any page. You can monitor connections pools in Control Panel / Configuration / SQL DataSource.

## Bug Tracker, Comments and Requests
Please place any issue [here](https://github.com/flarroca/sqldatatable-portlet/issues/new).

Comments, critics and new feature requests are very appreciated, but everything will be treated as issues to simplify project administration.

## Contributions
All kind of contributions are welcome. This repository is public, you can commit changes directly.
