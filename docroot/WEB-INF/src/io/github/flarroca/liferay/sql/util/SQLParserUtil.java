package io.github.flarroca.liferay.sql.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import com.liferay.portal.kernel.util.StringPool;

public class SQLParserUtil {

   public static String parse(String sql, String where) {
      return (parse(sql, where, null, 0, 0));
   }

   public static String parse(String sql) {
      return (parse(sql, 0));
   }

   public static String parse(String sql, int limit) {
      return (parse(sql, limit, 0));
   }

   public static String parse(String sql, int limit, int offset) {
      return (parse(sql, null, null, limit, offset));
   }

   public static String parse(String sql, String where, String orderBy, int limit, int offset) {
      if (sql == null) {
         return (null);
      }

      boolean wrap = false;

      if (sql.contains(":where")) {
         if (sql.contains(":whereClause")) {
            if (where != null) {
               sql = sql.replace(":whereClause", "WHERE " + where);
            } else {
               sql = sql.replace(":whereClause", StringPool.BLANK);
            }
         } else {
            if (where != null) {
               sql = sql.replace(":where", where);
            } else {
               sql = sql.replace(":where", StringPool.BLANK);
            }
         }
      } else {
         if (where != null) {
            wrap = true;
            sql = "SELECT * FROM (" + sql + ") AS result WHERE " + where;
         }
      }

      if (sql.contains(":orderBy")) {
         if (sql.contains(":orderByClause")) {
            if (orderBy != null) {
               sql = sql.replace(":orderByClause", "ORDER BY " + orderBy);
            } else {
               sql = sql.replace(":orderByClause", StringPool.BLANK);
            }
         } else {
            if (orderBy != null) {
               sql = sql.replace(":orderBy", orderBy);
            } else {
               sql = sql.replace(":orderBy", StringPool.BLANK);
            }
         }
      } else {
         if (orderBy != null) {
            if (!wrap) {
               sql = "SELECT * FROM (" + sql + ") AS result";
            }
            sql += " ORDER BY " + orderBy;
         }
      }

      if (sql.contains(":limit")) {
         if (sql.contains(":limitClause")) {
            if (limit > 0) {
               if (offset > 0) {
                  sql = sql.replace(":limitClause", "LIMIT " + limit + " OFFSET " + offset);
               } else {
                  sql = sql.replace(":limitClause", "LIMIT " + limit);
               }
            } else {
               sql = sql.replace(":limitClause", StringPool.BLANK);
            }
         } else {
            if (limit > 0) {
               sql = sql.replace(":limit", String.valueOf(limit));
               if (offset > 0) {
                  sql = sql.replace(":offset", String.valueOf(offset));
               } else {
                  sql = sql.replace(":offset", StringPool.BLANK);
               }
            } else {
               sql = sql.replace(":limit", StringPool.BLANK);
            }
         }
      } else {
         if (limit > 0) {
            if (!wrap) {
               sql = "SELECT * FROM (" + sql + ") AS result";
            }
            sql += " LIMIT " + limit;
            if (offset > 0) {
               sql += " OFFSET " + offset;
            }
         }
      }

      if (sql.contains(":offset")) {
         if (offset > 0) {
            sql = sql.replace(":offset", String.valueOf(offset));
         } else {
            sql = sql.replace(":offset", StringPool.BLANK);
         }
      }

      return (sql);
   }

   public static String parseCount(String sql) {
      sql = parse(sql);
      return ("SELECT count(*) FROM (" + sql + ") AS result");
   }

   public static String parseOrderBy(Map<String, Boolean> orderBy) {
      if ((orderBy == null) || (orderBy.size() <= 0)) {
         return (null);
      }

      String sql = "";
      for (Entry<String, Boolean> order : orderBy.entrySet()) {
         sql += order.getKey();
         if (order.getValue()) {
            sql += ", ";
         } else {
            sql += " DESC, ";
         }
      }
      return (sql.substring(0, sql.length() - 2));
   }

   public static String parseLikes(Map<String, ArrayList<String>> likes) {
      if ((likes == null) || (likes.size() <= 0)) {
         return (null);
      }

      String sql = "(";
      for (Entry<String, ArrayList<String>> like : likes.entrySet()) {
         for (String likeValue : like.getValue()) {
            // TODO: SQL LIKE could require casting
            sql += "(" + like.getKey() + " LIKE '%" + likeValue + "%') OR ";
         }
      }
      return (sql.substring(0, sql.length() - 4) + ")");
   }
}
