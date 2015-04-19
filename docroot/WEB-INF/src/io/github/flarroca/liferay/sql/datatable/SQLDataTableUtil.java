package io.github.flarroca.liferay.sql.datatable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import io.github.flarroca.liferay.sql.datasource.SQLDataSourceFactoryUtil;
import io.github.flarroca.liferay.sql.util.SQLKeys;
import io.github.flarroca.liferay.sql.util.SQLParserUtil;
import io.github.flarroca.liferay.sql.util.SQLRenderUtil;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

public class SQLDataTableUtil {

   private static Log _log = LogFactoryUtil.getLog(SQLDataTableUtil.class);

   public static JSONObject executeServerSide(String driverClassName, String url, String userName, String password, String sql, String where, String orderBy, int timeout, int count, int countTimeout, int draw, int start, int length) {

      int limit = length; // Limit results to the requested length
      int offset = start;
      if ((limit <= 0) || (limit > SQLDataTableKeys.HTML_LIMIT)) {
         limit = SQLDataTableKeys.HTML_LIMIT; // The page cannot be be out of limits
      }

      JSONObject json = execute(driverClassName, url, userName, password, SQLParserUtil.parse(sql, where, orderBy, limit, offset), limit, timeout);

      int recordsTotal = 0;
      int recordsFiltered = 0;
      if (count > 0) {
         recordsTotal = count;
      } else {
         recordsTotal = count(driverClassName, url, userName, password, SQLParserUtil.parse(sql), countTimeout);
      }
      if ((where != null) && (recordsTotal > 0)) {
         recordsFiltered = count(driverClassName, url, userName, password, SQLParserUtil.parse(sql, where), countTimeout);
      } else {
         recordsFiltered = recordsTotal;
      }

      json.put("draw", draw);
      if (recordsTotal > 0) {
         json.put("recordsTotal", recordsTotal);
         if (recordsFiltered > 0) {
            json.put("recordsFiltered", recordsFiltered);
         } else {
            json.put("recordsFiltered", recordsTotal);
         }
      }
      return (json);
   }

   public static JSONObject executeOptions(String driverClassName, String url, String userName, String password, String sql, int timeout, int count, int pageLength, String ajaxURL) {

      int limit = pageLength; // Limit results to render in one page
      boolean processing = true; // Worst case require processing message
      boolean serverSide = true; // Worst case require server side processing
      int deferLoading = 0; // Worst case cannot defer loading

      if (count > 0) {
         if (count < SQLDataTableKeys.HTML_LIMIT) {
            limit = count; // Will fit in HTML
            serverSide = false; // Server side not longer required, defer loading not required
         } else {
            deferLoading = count; // Deferring loading
         }
      }
      if ((limit <= 0) || (limit > SQLDataTableKeys.HTML_LIMIT)) {
         limit = SQLDataTableKeys.HTML_LIMIT; // The page cannot be be out of limits
      }

      if (_log.isDebugEnabled()) {
         _log.debug("sql: " + sql);
         _log.debug("count: " + count);
         _log.debug("limit: " + limit);
         _log.debug("pageLength: " + pageLength);
      }

      JSONObject json = execute(driverClassName, url, userName, password, SQLParserUtil.parse(sql, limit), limit, timeout);
      if (pageLength > 0) {
         json.put("pageLength", pageLength);
      }
      if (serverSide) {
         json.put("processing", processing);
         json.put("serverSide", serverSide);
         if (deferLoading > 0) {
            json.put("deferLoading", deferLoading);
         }
         if (Validator.isNotNull(ajaxURL)) {
            json.put("ajax", JSONFactoryUtil.createJSONObject().put("url", ajaxURL).put("type", "POST"));
         }
      }

      if (_log.isDebugEnabled()) {
         _log.debug("processing: " + processing);
         _log.debug("serverSide: " + serverSide);
         _log.debug("deferLoading: " + deferLoading);
         _log.debug("ajax: " + ajaxURL);
      }

      return (json);
   }

   public static JSONObject execute(String driverClassName, String url, String userName, String password, String sql, int limit, int timeout) {

      Connection connection = null;
      Statement statement = null;
      ResultSet resultSet = null;
      JSONArray columns = null;
      JSONArray data = null;
      String error = null;
      try {
         connection = SQLDataSourceFactoryUtil.getConnection(driverClassName, url, userName, password);
         statement = SQLDataSourceFactoryUtil.createStatement(connection, timeout);
         resultSet = SQLDataSourceFactoryUtil.execute(statement, sql);

         data = SQLRenderUtil.toJSONArray(resultSet, limit);
         columns = SQLDataTableUtil.toJSONColumns(resultSet);

      } catch (SQLException e) {
         error = e.getMessage();
         error += (error == null ? "SQL:" : ". SQL:") + sql;
         _log.error(error);
      } catch (ClassNotFoundException e) {
         error = e.getMessage();
         error += (error == null ? "" : ". ") + "Driver class name: " + driverClassName;
         _log.error(error);
      } catch (Exception e) {
         error = e.getMessage();
         error = (error == null ? "" : ". ") + "URL: " + url + ", SQL: " + sql + ", driver class name: " + driverClassName;
         _log.error(error);
      } finally {
         SQLDataSourceFactoryUtil.cleanUp(resultSet);
         SQLDataSourceFactoryUtil.cleanUp(statement);
         SQLDataSourceFactoryUtil.cleanUp(connection);
      }

      JSONObject json = JSONFactoryUtil.createJSONObject();
      if (data != null) {
         json.put("data", data);
      }
      if (columns != null) {
         json.put("columns", columns);
      }
      if (error != null) {
         json.put("error", error);
      }

      return (json);
   }

   public static int count(String driverClassName, String url, String userName, String password, String sql, int timeout) {

      Connection connection = null;
      int count = 0;
      String error = null;

      try {
         connection = SQLDataSourceFactoryUtil.getConnection(driverClassName, url, userName, password);

         count = SQLDataSourceFactoryUtil.count(connection, sql);

      } catch (SQLException e) {
         error = e.getMessage();
         error += (error == null ? "SQL:" : ". SQL:") + sql;
         _log.error(error);
      } catch (ClassNotFoundException e) {
         error = e.getMessage();
         error += (error == null ? "" : ". ") + "Driver class name not found: " + driverClassName;
         _log.error(error);
      } catch (Exception e) {
         error = e.getMessage();
         error = (error == null ? "" : ". ") + "URL: " + url + ", SQL: " + sql + ", driver class name: " + driverClassName;
         _log.error(error);
      } finally {
         SQLDataSourceFactoryUtil.cleanUp(connection);
      }

      if (count <= 0) {
         _log.error("Count 0");
      }

      return (count);
   }

   public static String toHTMLMetadata(String driverClassName, String url, String userName, String password, String sql, String tableId, String tableClass) {

      Connection connection = null;
      Statement statement = null;
      ResultSet resultSet = null;
      String htmlMetadata = null;
      try {
         connection = SQLDataSourceFactoryUtil.getConnection(driverClassName, url, userName, password);
         statement = SQLDataSourceFactoryUtil.createStatement(connection, SQLKeys.TIMEOUT_DEFAULT);
         resultSet = SQLDataSourceFactoryUtil.execute(statement, SQLParserUtil.parse(sql, 1));

         htmlMetadata = SQLRenderUtil.toHTMLMetadata(resultSet, tableId, tableClass);

      } catch (SQLException e) {
         _log.error(e.getMessage());
      } catch (ClassNotFoundException e) {
         _log.error(e.getMessage());
      } catch (Exception e) {
         _log.error(e.getMessage());
      } finally {
         SQLDataSourceFactoryUtil.cleanUp(resultSet);
         SQLDataSourceFactoryUtil.cleanUp(statement);
         SQLDataSourceFactoryUtil.cleanUp(connection);
      }

      return (htmlMetadata);
   }

   private static JSONArray toJSONColumns(ResultSet resultSet) throws SQLException {
      JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

      int columns = resultSet.getMetaData().getColumnCount();
      for (int column = 1; column <= columns; column++) {
         JSONObject json = JSONFactoryUtil.createJSONObject();
         jsonArray.put(json);

         // json.put("data", resultSet.getMetaData().getColumnLabel(column));
         json.put("name", resultSet.getMetaData().getColumnLabel(column));
         json.put("title", resultSet.getMetaData().getColumnLabel(column));

         String type = null;
         boolean searchable = resultSet.getMetaData().isSearchable(column);
         String columnClassName = resultSet.getMetaData().getColumnClassName(column);
         if (columnClassName == null) {
            type = "string";
            searchable = false;
         } else if (columnClassName.equals(String.class.getName())) {
            type = "html";
            searchable = true;
         } else if (columnClassName.equals(Integer.class.getName())) {
            type = "num";
            searchable = false;
         } else if (columnClassName.equals(Long.class.getName())) {
            type = "num";
            searchable = false;
         } else if (columnClassName.equals(Double.class.getName())) {
            type = "num";
            searchable = false;
         } else if (columnClassName.equals(java.sql.Date.class.getName())) {
            type = "date";
            searchable = false;
         } else if (columnClassName.equals(java.sql.Timestamp.class.getName())) {
            type = "date";
            searchable = false;
         } else if (columnClassName.equals(java.sql.Time.class.getName())) {
            type = "date";
            searchable = false;
         } else {
            type = "string";
         }
         json.put("type", type);
         json.put("searchable", searchable);
      }

      return (jsonArray);
   }

}
