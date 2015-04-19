<%@ include file="/html/sqldatatable/init.jsp"%>

<%
   String htmlMetadata = SQLDataTableUtil.toHTMLMetadata(sqlDriverClassName, sqlURL, sqlUserName, sqlPassword, sql, StringPool.BLANK, SQLKeys.TABLE_CLASS_DFAULT);
%>

<liferay-ui:panel-container extended="<%=true%>" persistState="<%=false%>">
   <liferay-ui:panel collapsible="<%=true%>" defaultState="close" extended="<%=false%>" title="preferences">
      <div class="table-overflow">
         <%=RenderPreferencesUtil.getHTMLTable(themeDisplay)%>
      </div>
   </liferay-ui:panel>
   <liferay-ui:panel collapsible="<%=true%>" defaultState="close" extended="<%=false%>" title="metadata">
      <div class="table-overflow">
         <%=htmlMetadata%>
      </div>
   </liferay-ui:panel>
</liferay-ui:panel-container>