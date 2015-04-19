<%@ include file="/html/sqldatatable/init.jsp"%>

<%
   String redirect = ParamUtil.getString(request, "redirect");
%>


<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<aui:form action="<%=configurationURL%>" method="post" name="fm">
   <aui:input name="<%=Constants.CMD%>" type="hidden" value="<%=Constants.UPDATE%>" />
   <aui:input name="redirect" type="hidden" value="<%=redirect%>" />

   <liferay-ui:tabs names="sql,datatables" refresh="<%=false%>">
      <liferay-ui:section>
         <aui:fieldset label="connection">
            <aui:select label="driver-class-name" name="preferences--sqlDriverClassName--">
               <aui:option label="com.mysql.jdbc.Driver" value="com.mysql.jdbc.Driver" selected='<%="com.mysql.jdbc.Driver".equals(sqlDriverClassName)%>' />
               <aui:option label="org.postgresql.Driver" value="org.postgresql.Driver" selected='<%="org.postgresql.Driver".equals(sqlDriverClassName)%>' />
               <aui:option label="com.ibm.db2.jcc.DB2Driver" value="com.ibm.db2.jcc.DB2Driver" selected='<%="com.ibm.db2.jcc.DB2Driver".equals(sqlDriverClassName)%>' />
               <aui:option label="org.apache.derby.jdbc.EmbeddedDriver" value="org.apache.derby.jdbc.EmbeddedDriver" selected='<%="org.apache.derby.jdbc.EmbeddedDriver".equals(sqlDriverClassName)%>' />
               <aui:option label="org.hsqldb.jdbcDriver" value="org.hsqldb.jdbcDriver" selected='<%="org.hsqldb.jdbcDriver".equals(sqlDriverClassName)%>' />
               <aui:option label="com.ingres.jdbc.IngresDriver" value="com.ingres.jdbc.IngresDriver" selected='<%="com.ingres.jdbc.IngresDriver".equals(sqlDriverClassName)%>' />
               <aui:option label="oracle.jdbc.driver.OracleDriver" value="oracle.jdbc.driver.OracleDriver" selected='<%="oracle.jdbc.driver.OracleDriver".equals(sqlDriverClassName)%>' />
               <aui:option label="net.sourceforge.jtds.jdbc.Driver" value="net.sourceforge.jtds.jdbc.Driver" selected='<%="net.sourceforge.jtds.jdbc.Driver".equals(sqlDriverClassName)%>' />
            </aui:select>
            <aui:input type="text" label="url" name="preferences--sqlURL--" value="<%=sqlURL%>"></aui:input>
            <aui:input type="text" label="user" name="preferences--sqlUserName--" value="<%=sqlUserName%>"></aui:input>
            <aui:input type="text" label="password" name="preferences--sqlPassword--" value="<%=sqlPassword%>"></aui:input>

            <liferay-ui:panel-container extended="<%=true%>" persistState="<%=false%>">
               <liferay-ui:panel collapsible="<%=true%>" defaultState="close" extended="<%=false%>" title="properties">
                  <aui:input type="textarea" label="" name="preferences--sqlProperties--" value="<%=sqlProperties%>"></aui:input>
               </liferay-ui:panel>
            </liferay-ui:panel-container>

         </aui:fieldset>
         <aui:fieldset label="query">
            <aui:input type="textarea" label="sql" name="preferences--sql--" value="<%=sql%>"></aui:input>
            <aui:input type="text" label="timeout" name="preferences--sqlTimeout--" value="<%=sqlTimeout%>"></aui:input>
            <aui:input type="text" label="count" name="preferences--sqlCount--" value="<%=sqlCount%>"></aui:input>
            <aui:input type="text" label="timeout" name="preferences--sqlCountTimeout--" value="<%=sqlCountTimeout%>"></aui:input>
         </aui:fieldset>
      </liferay-ui:section>

      <liferay-ui:section>
         <aui:fieldset label="options">
            <aui:input type="text" label="page-length" name="preferences--dataTablePageLength--" value="<%=dataTablePageLength%>"></aui:input>
            <aui:input type="textarea" label="json" name="preferences--dataTableOptions--" value="<%=dataTableOptions%>"></aui:input>
            <%@ include file="/html/sqldatatable/config-help.jspf"%>
         </aui:fieldset>

         <aui:fieldset label="extensions">
            <aui:select label="pre-installed" name="preferences--dataTableExtensions--" multiple="true">
               <aui:option label="FixedColumns" value="FixedColumns" selected='<%=dataTableExtensions.contains("FixedColumns")%>' />
               <aui:option label="ColReorder" value="ColReorder" selected='<%=dataTableExtensions.contains("ColReorder")%>' />
               <aui:option label="HighlightingColumns" value="HighlightingColumns" selected='<%=dataTableExtensions.contains("HighlightingColumns")%>' />
               <aui:option label="" value="" selected='<%=dataTableExtensions.isEmpty()%>' />
            </aui:select>
            <aui:input type="textarea" label="javascript" name="preferences--dataTableJavascript--" value="<%=dataTableJavascript%>"></aui:input>
         </aui:fieldset>

         <aui:fieldset label="styling">
            <aui:input type="text" label="table-css-class" name="preferences--dataTableClass--" value="<%=dataTableClass%>"></aui:input>
         </aui:fieldset>
      </liferay-ui:section>
   </liferay-ui:tabs>

   <aui:button-row>
      <aui:button type="submit" />
   </aui:button-row>
</aui:form>