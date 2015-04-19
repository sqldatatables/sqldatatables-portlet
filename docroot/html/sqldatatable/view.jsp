<%@ include file="/html/sqldatatable/init.jsp"%>

<portlet:resourceURL var="ajaxURL">
   <portlet:param name="<%=Constants.CMD%>" value="<%=SQLDataTablePortlet.CMD_AJAX%>" />
</portlet:resourceURL>

<%
   JSONObject jsonAJAX = SQLDataTableUtil.executeOptions(sqlDriverClassName, sqlURL, sqlUserName, sqlPassword, sql, sqlTimeout, sqlCount, dataTablePageLength, ajaxURL);
   String htmlTable = SQLDataTableJSONUtil.toHTMLTable(jsonAJAX, dataTablePageLength, portletDisplay.getNamespace()+"table",dataTableClass); 
   if (htmlTable==null) {
      htmlTable=LanguageUtil.get(pageContext,"no-data");
   }
%>

<%=htmlTable%>

<aui:script>
   // Objects in scope for javascript
   var namespace = new String("<portlet:namespace/>");
   var tableNamespace = new String(namespace + "table");

   var dataTableOptions = null;
   try {
      dataTableOptions = <%= (Validator.isNull(dataTableOptions)?"null":dataTableOptions) %>;
   } catch (e) {
      console.log("<portlet:namespace/>: options syntax error");
   }

   var dataTableExtensions = null;
   try {
      dataTableExtensions = "<%=dataTableExtensions %>";
   } catch (e) {
      console.log("<portlet:namespace/>: extensions syntax error");
   }

   var jsonAJAX = null;
   try {
      jsonAJAX = <%=jsonAJAX.toString() %>;
      jsonAJAX2 = <%=jsonAJAX.toString() %>;
   } catch (e) {
      console.log("<portlet:namespace/>: json syntax error");
   }

   // Extend and swap
   if (jsonAJAX != null) {
      $.extend(jsonAJAX, dataTableOptions);
      dataTableOptions = jsonAJAX;
   }

   // Call datatables with extensions
   var table = null;
   try {
      table = SQLDataTable(tableNamespace, dataTableOptions, dataTableExtensions);
   } catch (e) {
      console.log("<portlet:namespace/>: SQLDataTable.js error");
   }

   // Custom javascript, all objects on scope
   try {
      <%=dataTableJavascript %>
   } catch (e) {
      console.log("<portlet:namespace/>: javascript exception");
   }
</aui:script>
   




