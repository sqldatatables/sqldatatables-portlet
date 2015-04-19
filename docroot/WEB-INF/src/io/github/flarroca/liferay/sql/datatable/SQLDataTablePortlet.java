package io.github.flarroca.liferay.sql.datatable;

import io.github.flarroca.liferay.sql.util.SQLKeys;
import io.github.flarroca.liferay.sql.util.SQLParserUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ValidatorException;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class SQLDataTable
 */
public class SQLDataTablePortlet extends MVCPortlet {

   public static final String CMD_AJAX = "ajax";

   private static Log _log = LogFactoryUtil.getLog(SQLDataTablePortlet.class);

   @Override
   public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException, ReadOnlyException {

      String cmd = ParamUtil.getString(resourceRequest, "cmd", "");
      if (Validator.isNotNull(cmd)) {
         if (CMD_AJAX.equals(cmd)) {
            ajax(resourceRequest, resourceResponse);
         }
      }
   }

   public void ajax(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException, ReadOnlyException {

      if (_log.isDebugEnabled()) {
         for (String key : resourceRequest.getParameterMap().keySet()) {
            String value = ParamUtil.getString(resourceRequest, key, "");
            _log.debug(key + ": " + value);
         }
      }

      int draw = ParamUtil.getInteger(resourceRequest, "draw", 1);
      int start = ParamUtil.getInteger(resourceRequest, "start", 0);
      int length = ParamUtil.getInteger(resourceRequest, "length", SQLDataTableKeys.PAGE_LENGTH_DEFAULT);

      String where = SQLParserUtil.parseLikes(getLikes(resourceRequest));
      String orderBy = SQLParserUtil.parseOrderBy(getOrderBy(resourceRequest));

      PortletPreferences portletPreferences = resourceRequest.getPreferences();
      String driverClassName = portletPreferences.getValue(SQLKeys.DRIVER_CLASS_NAME, StringPool.BLANK);
      String url = portletPreferences.getValue(SQLKeys.URL, StringPool.BLANK);
      String userName = portletPreferences.getValue(SQLKeys.USER_NAME, StringPool.BLANK);
      String password = portletPreferences.getValue(SQLKeys.PASSWORD, StringPool.BLANK);
      String sql = portletPreferences.getValue(SQLKeys.SQL, StringPool.BLANK);
      int timeout = GetterUtil.get(portletPreferences.getValue(SQLKeys.TIMEOUT, null), SQLKeys.TIMEOUT_DEFAULT);
      int count = GetterUtil.get(portletPreferences.getValue(SQLKeys.COUNT, null), 0);
      int countTimeout = GetterUtil.get(portletPreferences.getValue(SQLKeys.COUNT_TIMEOUT, null), SQLKeys.COUNT_TIMEOUT_DEFAULT);

      JSONObject json = SQLDataTableUtil.executeServerSide(driverClassName, url, userName, password, sql, where, orderBy, timeout, count, countTimeout, draw, start, length);
      if (json != null) {
         PrintWriter writer = resourceResponse.getWriter();
         writer.write(json.toString());

         int recordsTotal = json.getInt("recordsTotal", 0);
         if (recordsTotal > 0) {
            portletPreferences.setValue(SQLKeys.COUNT, String.valueOf(recordsTotal));
            try {
               portletPreferences.store();
            } catch (ValidatorException e) {
               _log.error("Cannot store count portlet preference");
            }
         }
      }
   }

   public HashMap<String, Boolean> getOrderBy(ResourceRequest resourceRequest) {

      int order = 0;
      int orderCount = 0;
      HashMap<String, Boolean> orderBy = new HashMap<String, Boolean>();
      while (orderCount == order) {
         int orderColumn = ParamUtil.getInteger(resourceRequest, "order[" + order + "][column]", -1);
         if (orderColumn != -1) {
            orderCount++;
            
            String columnName = ParamUtil.getString(resourceRequest, "columns[" + orderColumn + "][name]", null);
            if (Validator.isNotNull(columnName)) {
               String orderDirection = ParamUtil.getString(resourceRequest, "order[" + order + "][dir]", "asc");
               orderBy.put(columnName, orderDirection.equals("asc"));
            }
         }
         order++;
      }

      return (orderBy);
   }

   public HashMap<String, ArrayList<String>> getLikes(ResourceRequest resourceRequest) {
      HashMap<String, ArrayList<String>> likes = new HashMap<String, ArrayList<String>>();

      String searchValue = ParamUtil.getString(resourceRequest, "search[value]", null);

      int column = 0;
      int columnCount = 0;
      while (columnCount == column) {
         // String columnData = ParamUtil.getString(resourceRequest, "columns[" + column + "][data]", null);
         String columnName = ParamUtil.getString(resourceRequest, "columns[" + column + "][name]", null);
         if (Validator.isNotNull(columnName)) {
            columnCount++;

            boolean columnSearchable = ParamUtil.getBoolean(resourceRequest, "columns[" + column + "][searchable]", false);
            if (columnSearchable) {
               String columnSearchValue = ParamUtil.getString(resourceRequest, "columns[" + column + "][search][value]", null);
               // boolean columnSearchRegex = ParamUtil.getBoolean(resourceRequest, "columns[" + column + "][search][regex]", false);

               ArrayList<String> searches = new ArrayList<String>();
               if (Validator.isNotNull(columnSearchValue)) {
                  searches.add(columnSearchValue);
               }
               if (Validator.isNotNull(searchValue)) {
                  searches.add(searchValue);
               }
               if (searches.size() > 0) {
                  likes.put(columnName, searches);
               }
            }
         }

         column++;
      }

      return (likes);
   }

   public HashMap<String, ArrayList<String>> getSearch(ResourceRequest resourceRequest) {
      HashMap<String, ArrayList<String>> columnSearches = new HashMap<String, ArrayList<String>>();

      String searchValue = ParamUtil.getString(resourceRequest, "search[value]", null);

      int column = 0;
      int columnCount = 0;
      while (columnCount == column) {
         String columnName = ParamUtil.getString(resourceRequest, "columns[" + column + "][name]", null);
         if (Validator.isNotNull(columnName)) {
            columnCount++;

            boolean columnSearchable = ParamUtil.getBoolean(resourceRequest, "columns[" + column + "][searchable]", false);
            if (columnSearchable) {
               String columnSearchValue = ParamUtil.getString(resourceRequest, "columns[" + column + "][search][value]", null);
               // boolean columnSearchRegex = ParamUtil.getBoolean(resourceRequest, "columns[" + column + "][search][regex]", false);

               ArrayList<String> searches = new ArrayList<String>();
               if (Validator.isNotNull(columnSearchValue)) {
                  searches.add(columnSearchValue);
               }
               if (Validator.isNotNull(searchValue)) {
                  searches.add(searchValue);
               }
               if (searches.size() > 0) {
                  columnSearches.put(columnName, searches);
               }
            }
         }

         column++;
      }

      return (columnSearches);
   }
   
}
