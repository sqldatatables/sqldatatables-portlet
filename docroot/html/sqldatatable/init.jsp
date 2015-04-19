<%@ include file="/html/common/init.jsp"%>

<%@ page import="io.github.flarroca.liferay.sql.datatable.SQLDataTablePortlet"%>
<%@ page import="io.github.flarroca.liferay.sql.datatable.SQLDataTableKeys"%>
<%@ page import="io.github.flarroca.liferay.sql.datatable.SQLDataTableUtil"%>
<%@ page import="io.github.flarroca.liferay.sql.datatable.SQLDataTableJSONUtil"%>

<%@ page import="com.liferay.portal.kernel.json.JSONObject"%>

<%
   String sqlProperties = portletPreferences.getValue(SQLKeys.PROPERTIES, StringPool.BLANK);
   String sqlDriverClassName = portletPreferences.getValue(SQLKeys.DRIVER_CLASS_NAME, StringPool.BLANK);
   String sqlURL = portletPreferences.getValue(SQLKeys.URL, StringPool.BLANK);
   String sqlUserName = portletPreferences.getValue(SQLKeys.USER_NAME, StringPool.BLANK);
   String sqlPassword = portletPreferences.getValue(SQLKeys.PASSWORD, StringPool.BLANK);
   String sql = portletPreferences.getValue(SQLKeys.SQL, StringPool.BLANK);
   int sqlTimeout = GetterUtil.get(portletPreferences.getValue(SQLKeys.TIMEOUT,null), SQLKeys.TIMEOUT_DEFAULT);
   int sqlCount = GetterUtil.get(portletPreferences.getValue(SQLKeys.COUNT,null), 0);
   int sqlCountTimeout = GetterUtil.get(portletPreferences.getValue(SQLKeys.COUNT_TIMEOUT,null), SQLKeys.COUNT_TIMEOUT_DEFAULT);
   
   String dataTableOptions = portletPreferences.getValue(SQLDataTableKeys.OPTIONS, StringPool.BLANK);
   String dataTableJavascript = portletPreferences.getValue(SQLDataTableKeys.JAVASCRIPT, StringPool.BLANK);
   String dataTableExtensions = portletPreferences.getValue(SQLDataTableKeys.EXTENSIONS, StringPool.BLANK);
   int dataTablePageLength = GetterUtil.get(portletPreferences.getValue(SQLDataTableKeys.PAGE_LENGTH,null), SQLDataTableKeys.PAGE_LENGTH_DEFAULT);
   String dataTableClass = portletPreferences.getValue(SQLDataTableKeys.TABLE_CLASS, SQLDataTableKeys.TABLE_CLASS_DEFAULT);
   
   PortletPreferences userSpecificPreferences = ScopePreferencesUtil.getUserSpecificPortletSpecificPreferences(renderRequest);
   String userSQLWhere = userSpecificPreferences.getValue(SQLKeys.WHERE, StringPool.BLANK);
%>