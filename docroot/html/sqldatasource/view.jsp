<%@page import="io.github.flarroca.liferay.sql.datasource.SQLDataSourcePortlet"%>
<%@ include file="/html/sqldatasource/init.jsp"%>

<portlet:actionURL var="destroySQLConnectionsURL">
   <portlet:param name="<%=ActionRequest.ACTION_NAME%>" value="<%=SQLDataSourcePortlet.ACTION_DESTROY_POOLS%>" />
</portlet:actionURL>

<div class="table-overflow">
   <%=GetterUtil.get(SQLDataSourceFactoryUtil.getHTMLStats(null,SQLKeys.TABLE_CLASS_DFAULT),"("+LanguageUtil.get(pageContext, "the-pool-is-empty")+")") %>
</div> 

<aui:button href="<%=destroySQLConnectionsURL%>" value="destroy-all"></aui:button>
