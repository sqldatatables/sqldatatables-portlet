package io.github.flarroca.liferay.sql.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

public class SQLRenderUtil {

   public static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

   private static Log _log = LogFactoryUtil.getLog(SQLRenderUtil.class);

   public static String toHTMLTable(ResultSet resultSet, int limit, String tableId, String tableClass) throws SQLException {
      return (toHTMLTable(resultSet, limit, 0, tableId, tableClass));
   }

   public static String toHTMLTable(ResultSet resultSet, int limit, int offset, String tableId, String tableClass) throws SQLException {
      StringBuilder html = new StringBuilder();

      int count = 0;
      while ((count < offset) && (resultSet.next())) {
         count++;
      }

      // Table
      html.append("<table");
      if (Validator.isNotNull(tableClass)) {
         html.append(" class='" + tableClass + "'");
      }
      if (Validator.isNotNull(tableId)) {
         html.append(" id='" + tableId + "'");
      }
      html.append(">\n");

      // Table head
      html.append("<thead>\n");
      html.append("<tr>\n");
      int fields = resultSet.getMetaData().getColumnCount();
      for (int field = 1; field <= fields; field++) {

         html.append("<th>");
         html.append(resultSet.getMetaData().getColumnLabel(field));
         html.append("</th>");
      }
      html.append("</tr>\n");
      html.append("</thead>\n");

      // Table body
      html.append("<tbody>\n");
      while ((count < (offset + limit)) && (resultSet.next())) {
         count++;
         html.append("<tr>\n");

         for (int field = 1; field <= fields; field++) {
            html.append("<td>");
            html.append(resultSet.getString(field));
            html.append("</td>");
         }
         html.append("</tr>\n");
      }
      html.append("</tbody>\n");
      html.append("</table>\n");

      return (html.toString());
   }

   public static String toHTMLTable(JSONArray json, int limit, String tableId, String tableClass) {
      if ((json != null) && (json.length() <= 0)) {
         return (null);
      }

      StringBuilder html = new StringBuilder();

      // Table
      html.append("<table");
      if (Validator.isNotNull(tableClass)) {
         html.append(" class='" + tableClass + "'");
      }
      if (Validator.isNotNull(tableId)) {
         html.append(" id='" + tableId + "'");
      }
      html.append(">\n");

      // Table head
      JSONObject jsonHead = json.getJSONObject(0);
      if (jsonHead != null) {
         html.append("<thead>\n");
         html.append("<tr>\n");
         for (Iterator<String> keys = jsonHead.keys(); keys.hasNext();) {
            String key = keys.next();
            html.append("<th>");
            if (Validator.isNotNull(key)) {
               html.append(key);
            }
            html.append("</th>");
         }
         html.append("</tr>\n");
         html.append("</thead>\n");
      }

      // Table body
      html.append("<tbody>\n");
      int length = json.length();
      length = (limit > length ? length : limit);
      for (int index = 0; index < length; index++) {
         JSONObject jsonObject = json.getJSONObject(index);
         if (jsonObject != null) {
            html.append("<tr>");
            for (Iterator<String> keys = jsonHead.keys(); keys.hasNext();) {
               String key = keys.next();
               html.append("<td>");
               if (Validator.isNotNull(key)) {
                  String value = jsonObject.getString(key);
                  if (Validator.isNotNull(value)) {
                     html.append(value);
                  }
               }
               html.append("</td>");
            }
            html.append("</tr>");
         }
      }
      html.append("</tbody>");
      html.append("</table>");

      return (html.toString());
   }

   public static String toHTMLTable(JSONArray json, JSONArray columns, int limit, String tableId, String tableClass) {
      if ((json != null) && (json.length() <= 0)) {
         return (null);
      }

      StringBuilder html = new StringBuilder();

      // Table
      html.append("<table");
      if (Validator.isNotNull(tableClass)) {
         html.append(" class='" + tableClass + "'");
      }
      if (Validator.isNotNull(tableId)) {
         html.append(" id='" + tableId + "'");
      }
      html.append(">\n");

      // Table head
      if (columns != null) {
         html.append("<thead>\n");
         html.append("<tr>\n");

         int columnsLength = columns.length();
         for (int column = 0; column < columnsLength; column++) {
            html.append("<th>");
            String title = columns.getString(column);
            if (Validator.isNotNull(title)) {
               html.append(title);
            } else {
               html.append(column);
            }
            html.append("</th>");
         }

         html.append("</tr>\n");
         html.append("</thead>\n");

         // Table body
         html.append("<tbody>\n");
         int length = json.length();
         limit = (limit > length ? length : limit);
         for (int index = 0; index < limit; index++) {

            JSONArray jsonArray = json.getJSONArray(index);
            if (jsonArray != null) {
               html.append("<tr>");
               int arrayLength = jsonArray.length();
               for (int column = 0; column < columnsLength; column++) {
                  html.append("<td>");
                  if (column < arrayLength) {
                     String value = jsonArray.getString(column);
                     if (value != null) {
                        html.append(value);
                     }
                  }
                  html.append("</td>");
               }
               html.append("</tr>");
            }
         }
         html.append("</tbody>");
         html.append("</table>");
      }

      return (html.toString());
   }

   public static JSONArray toJSONArray(ResultSet resultSet, int limit) throws SQLException {
      return (toJSONArray(resultSet, limit, 0));
   }

   public static JSONArray toJSONArray(ResultSet resultSet, int limit, int offset) throws SQLException {
      JSONArray json = JSONFactoryUtil.createJSONArray();

      int count = 0;
      while ((count < offset) && (resultSet.next())) {
         count++;
      }

      int fields = resultSet.getMetaData().getColumnCount();
      while ((count < (offset + limit)) && (resultSet.next())) {
         count++;
         JSONArray jsonArray = JSONFactoryUtil.createJSONArray();
         for (int column = 1; column <= fields; column++) {

            String columnClassName = resultSet.getMetaData().getColumnClassName(column);
            if (columnClassName == null) {
               jsonArray.put(StringPool.BLANK);
            } else if (columnClassName.equals(String.class.getName())) {
               jsonArray.put(resultSet.getString(column));
            } else if (columnClassName.equals(Integer.class.getName())) {
               jsonArray.put(resultSet.getInt(column));
            } else if (columnClassName.equals(Long.class.getName())) {
               jsonArray.put(resultSet.getLong(column));
            } else if (columnClassName.equals(Double.class.getName())) {
               jsonArray.put(resultSet.getDouble(column));
            } else if (columnClassName.equals(java.sql.Date.class.getName())) {
               jsonArray.put(DATE_FORMAT.format(resultSet.getDate(column)));
            } else if (columnClassName.equals(java.sql.Timestamp.class.getName())) {
               jsonArray.put(DATE_FORMAT.format(new Date(resultSet.getTimestamp(column).getTime())));
            } else if (columnClassName.equals(java.sql.Time.class.getName())) {
               jsonArray.put(resultSet.getTime(column).getTime());
            } else {
               jsonArray.put(StringPool.BLANK);
            }
         }
         json.put(jsonArray);
      }

      return (json);
   }

   public static JSONArray toJSONObject(ResultSet resultSet, int limit) throws SQLException {
      return (toJSONObject(resultSet, limit, 0));
   }

   public static JSONArray toJSONObject(ResultSet resultSet, int limit, int offset) throws SQLException {
      JSONArray json = JSONFactoryUtil.createJSONArray();

      int count = 0;
      while ((count < offset) && (resultSet.next())) {
         count++;
      }

      int fields = resultSet.getMetaData().getColumnCount();
      while ((count < (offset + limit)) && (resultSet.next())) {
         count++;
         JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
         for (int column = 1; column <= fields; column++) {

            String columnLabel = resultSet.getMetaData().getColumnLabel(column);
            String columnClassName = resultSet.getMetaData().getColumnClassName(column);
            if (columnClassName == null) {
               jsonObject.put(columnLabel,resultSet.getString(column));
            } else if (columnClassName.equals(String.class.getName())) {
               jsonObject.put(columnLabel,resultSet.getString(column));
            } else if (columnClassName.equals(Integer.class.getName())) {
               jsonObject.put(columnLabel,resultSet.getInt(column));
            } else if (columnClassName.equals(Long.class.getName())) {
               jsonObject.put(columnLabel,resultSet.getLong(column));
            } else if (columnClassName.equals(Double.class.getName())) {
               jsonObject.put(columnLabel,resultSet.getDouble(column));
            } else if (columnClassName.equals(java.sql.Date.class.getName())) {
               jsonObject.put(columnLabel,DATE_FORMAT.format(resultSet.getDate(column)));
            } else if (columnClassName.equals(java.sql.Timestamp.class.getName())) {
               jsonObject.put(columnLabel,DATE_FORMAT.format(new Date(resultSet.getTimestamp(column).getTime())));
            } else if (columnClassName.equals(java.sql.Time.class.getName())) {
               jsonObject.put(columnLabel,resultSet.getTime(column).getTime());
            } else {
               jsonObject.put(columnLabel,resultSet.getString(column));
            }
         }
         json.put(jsonObject);
      }

      return (json);
   }

   public static JSONArray toJSONMetadata(ResultSet resultSet) {

      JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

      try {
         int columns = resultSet.getMetaData().getColumnCount();
         for (int column = 1; column <= columns; column++) {
            JSONObject json = JSONFactoryUtil.createJSONObject();
            json.put("catalogName", resultSet.getMetaData().getCatalogName(column));
            json.put("schemaName", resultSet.getMetaData().getSchemaName(column));
            json.put("tableName", resultSet.getMetaData().getTableName(column));
            json.put("columnName", resultSet.getMetaData().getColumnName(column));
            json.put("columnLabel", resultSet.getMetaData().getColumnLabel(column));
            json.put("columnDisplaySize", resultSet.getMetaData().getColumnDisplaySize(column));
            json.put("columnClassName", resultSet.getMetaData().getColumnClassName(column));
            json.put("columnTypeName", resultSet.getMetaData().getColumnTypeName(column));
            json.put("precision", resultSet.getMetaData().getPrecision(column));
            json.put("scale", resultSet.getMetaData().getScale(column));
            json.put("isAutoIncrement", resultSet.getMetaData().isAutoIncrement(column));
            json.put("isCurrency", resultSet.getMetaData().isCurrency(column));
            json.put("isNullable", resultSet.getMetaData().isNullable(column));
            json.put("isSearchable", resultSet.getMetaData().isSearchable(column));
            json.put("isSigned", resultSet.getMetaData().isSigned(column));
            json.put("isDefinitelyWritable", resultSet.getMetaData().isDefinitelyWritable(column));
            json.put("isWritable", resultSet.getMetaData().isWritable(column));
            jsonArray.put(json);
         }
      } catch (Exception e) {
         _log.error("Cannot get metadata");
         // e.printStackTrace();
      }

      return (jsonArray);
   }

   public static String toHTMLMetadata(ResultSet resultSet, String tableId, String tableClass) {

      StringBuilder html = new StringBuilder();

      // Table
      html.append("<table");
      if (Validator.isNotNull(tableClass)) {
         html.append(" class='" + tableClass + "'");
      }
      if (Validator.isNotNull(tableId)) {
         html.append(" id='" + tableId + "'");
      }
      html.append(">");
      
      html.append("<thead>");
      html.append("<tr>");
      html.append("<th>").append("columnName").append("</th>");
      html.append("<th>").append("columnLabel").append("</th>");
      html.append("<th>").append("columnDisplaySize").append("</th>");
      html.append("<th>").append("columnClassName").append("</th>");
      html.append("<th>").append("columnTypeName").append("</th>");
      html.append("<th>").append("precision").append("</th>");
      html.append("<th>").append("scale").append("</th>");
      html.append("<th>").append("catalogName").append("</th>");
      html.append("<th>").append("schemaName").append("</th>");
      html.append("<th>").append("tableName").append("</th>");
      html.append("<th>").append("isAutoIncrement").append("</th>");
      html.append("<th>").append("isCurrency").append("</th>");
      html.append("<th>").append("isNullable").append("</th>");
      html.append("<th>").append("isSearchable").append("</th>");
      html.append("<th>").append("isSigned").append("</th>");
      html.append("<th>").append("isDefinitelyWritable").append("</th>");
      html.append("<th>").append("isWritable").append("</th>");
      html.append("</tr>");
      html.append("</thead>");

      html.append("<tbody>");
      try {
         int columns = resultSet.getMetaData().getColumnCount();
         for (int column = 1; column <= columns; column++) {
            html.append("<tr>");
            html.append("<td>").append(resultSet.getMetaData().getColumnName(column)).append("</td>");
            html.append("<td>").append(resultSet.getMetaData().getColumnLabel(column)).append("</td>");
            html.append("<td>").append(resultSet.getMetaData().getColumnDisplaySize(column)).append("</td>");
            html.append("<td>").append(resultSet.getMetaData().getColumnClassName(column)).append("</td>");
            html.append("<td>").append(resultSet.getMetaData().getColumnTypeName(column)).append("</td>");
            html.append("<td>").append(resultSet.getMetaData().getPrecision(column)).append("</td>");
            html.append("<td>").append(resultSet.getMetaData().getScale(column)).append("</td>");
            html.append("<td>").append(resultSet.getMetaData().getCatalogName(column)).append("</td>");
            html.append("<td>").append(resultSet.getMetaData().getSchemaName(column)).append("</td>");
            html.append("<td>").append(resultSet.getMetaData().getTableName(column)).append("</td>");
            html.append("<td>").append(resultSet.getMetaData().isAutoIncrement(column)).append("</td>");
            html.append("<td>").append(resultSet.getMetaData().isCurrency(column)).append("</td>");
            html.append("<td>").append(resultSet.getMetaData().isNullable(column)).append("</td>");
            html.append("<td>").append(resultSet.getMetaData().isSearchable(column)).append("</td>");
            html.append("<td>").append(resultSet.getMetaData().isSigned(column)).append("</td>");
            html.append("<td>").append(resultSet.getMetaData().isDefinitelyWritable(column)).append("</td>");
            html.append("<td>").append(resultSet.getMetaData().isWritable(column)).append("</td>");
            html.append("</tr>");
         }
      } catch (Exception e) {
         _log.error("Exception getting metadata");
      }
      html.append("</tbody>");
      html.append("</table>");

      return (html.toString());
   }
   
}
