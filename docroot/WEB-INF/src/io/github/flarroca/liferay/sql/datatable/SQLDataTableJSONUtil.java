package io.github.flarroca.liferay.sql.datatable;

import io.github.flarroca.liferay.sql.util.SQLRenderUtil;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class SQLDataTableJSONUtil {

   private static Log _log = LogFactoryUtil.getLog(SQLDataTableJSONUtil.class);

   public static String toHTMLTable(JSONObject json, int limit, String tableId, String tableClass) {
      if (!json.isNull("data")) {
         
         JSONArray columns = null;
         if (!(json.isNull("columns"))) {
            columns = json.getJSONArray("columns");
         } else if (!json.isNull("columnDefs")) {
            _log.info("TODO: support columnDefs");
         }
         
         return (toHTMLTable(json.getJSONArray("data"), columns, limit, tableId, tableClass));
      } else if (!json.isNull("error")) {
         return (json.getString("error"));
      }

      return (null);
   }
   
   public static JSONArray toJSONTitles (JSONArray columns) {
      if (columns==null) {
         return(null);
      }
      
      JSONArray titles = JSONFactoryUtil.createJSONArray();
      int columnsLength = columns.length();
      for (int column = 0; column < columnsLength; column++) {
         JSONObject jsonObject = columns.getJSONObject(column);
         if (jsonObject!=null) {
            titles.put(jsonObject.getString("title",jsonObject.getString("data",String.valueOf(column))));
         }
      }
      return(titles);
   }

   private static String toHTMLTable(JSONArray json, JSONArray columns, int limit, String tableId, String tableClass) {
      if ((json == null) || (json.length() <= 0)) {
         return (null);
      }

      // Check for objects or arrays
      if (json.getJSONObject(0) != null) {
         return (SQLRenderUtil.toHTMLTable(json, limit, tableId, tableClass));
      } else if (json.getJSONArray(0) != null) {
         return (SQLRenderUtil.toHTMLTable(json, toJSONTitles(columns), limit, tableId, tableClass));
      }
      return (null);
   }
}
