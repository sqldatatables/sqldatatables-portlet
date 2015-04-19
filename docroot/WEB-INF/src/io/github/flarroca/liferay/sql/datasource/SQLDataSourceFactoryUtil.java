package io.github.flarroca.liferay.sql.datasource;

import io.github.flarroca.liferay.sql.util.SQLParserUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Map.Entry;

import javax.sql.DataSource;

import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.mchange.v2.c3p0.PooledDataSource;

public class SQLDataSourceFactoryUtil {

   private static Log _log = LogFactoryUtil.getLog(SQLDataSourceFactoryUtil.class);

   public static Connection getConnection(Properties properties) throws SQLException, ClassNotFoundException, Exception {
      return (SQLDataSourceFactory.getConnection(properties));
   }

   public static Connection getConnection(String driverClassName, String url, String userName, String password) throws SQLException, ClassNotFoundException, Exception {
      return (SQLDataSourceFactory.getConnection(driverClassName, url, userName, password));
   }

   public static Statement createStatement(Connection connection) throws SQLException {
      return (createStatement(connection, 0));
   }

   public static Statement createStatement(Connection connection, int timeout) throws SQLException {
      Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      
      try {
         statement.setFetchSize(Integer.MIN_VALUE); // TODO: improve suport for dialects
      } catch (Exception e) {
         statement.setFetchSize(100); // TODO: read default fetch size form portal properties
      }

      if (timeout > 0) {
         // TODO: timeout create additional monitoring threads
         // statement.setQueryTimeout(timeout);
      }

      return (statement);
   }

   public static int count(Connection connection, String sql) throws SQLException, Exception {
      return (count(connection, sql, 0));
   }

   public static int count(Connection connection, String sql, int timeout) throws SQLException, Exception {
      if (connection == null) {
         throw new Exception();
      }

      sql = SQLParserUtil.parseCount(sql);
      if (_log.isDebugEnabled()) {
         _log.debug(sql);
      }

      int count = 0;
      Statement statement = null;
      ResultSet resultSet = null;
      try {
         statement = createStatement(connection);
         if (timeout > 0) {
            statement.setQueryTimeout(timeout);
         }
         resultSet = statement.executeQuery(sql);
         if (resultSet.next()) {
            count = resultSet.getInt(1);
         }
      } catch (SQLException e) {
         if (_log.isDebugEnabled()) {
            _log.debug("SQL exception on counting, message: " + e.getMessage());
         }
         throw e;
      } finally {
         SQLDataSourceFactoryUtil.cleanUp(resultSet);
         SQLDataSourceFactoryUtil.cleanUp(statement);
      }

      return (count);
   }

   public static ResultSet execute(Statement statement, String sql) throws ClassNotFoundException, SQLException, Exception {
      if (statement == null) {
         throw new Exception();
      }

      ResultSet resultSet = null;
      try {
         resultSet = statement.executeQuery(sql);
      } catch (SQLException e) {
         throw e;
      }

      return (resultSet);
   }

   public static void cleanUp(Connection connection) {
      DataAccess.cleanUp(connection);
   }

   public static void cleanUp(PreparedStatement preparedStatement) {
      DataAccess.cleanUp(preparedStatement);
   }

   public static void cleanUp(Statement statement) {
      DataAccess.cleanUp(statement);
   }

   public static void cleanUp(ResultSet resultSet) {
      DataAccess.cleanUp(resultSet);
   }

   public static boolean test(String driverClassName, String url, String userName, String password) {

      if ((Validator.isNull(driverClassName)) || (Validator.isNull(url))) {
         _log.error("driverClassName and url cannot be empty");
         return false;
      }

      boolean test = false;
      DataSource dataSource = null;
      Connection connection = null;
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;
      String sql = "select 1";

      try {
         Class.forName(driverClassName);

         dataSource = DataSourceFactoryUtil.initDataSource(driverClassName, url, userName, password, StringPool.BLANK);
         connection = dataSource.getConnection();

         // Read only and disable auto commit always, for everything
         connection.setReadOnly(true);
         connection.setAutoCommit(false);

         preparedStatement = connection.prepareStatement(sql);
         resultSet = preparedStatement.executeQuery();

         int rows = 0;
         while ((resultSet.next()) && (rows++ < 10)) {
            _log.info("Row: " + resultSet.getRow());
         }
         if (rows > 1) {
            _log.info("Have more than 10 rows");
         }
         test = true;
      } catch (ClassNotFoundException e) {
         _log.error("Cannot get connection, URL: " + url + ", message: " + e.getMessage());
      } catch (SQLException e) {
         _log.error("SQL exception, SQL: " + sql + ", message: " + e.getMessage());
      } catch (Exception e) {
         _log.error("Data source exception, URL: " + url + ", message: " + e.getMessage());
      } finally {
         DataAccess.cleanUp(connection, preparedStatement, resultSet);
         try {
            DataSourceFactoryUtil.destroyDataSource(dataSource);
         } catch (Exception e) {
            _log.error("Cannot destroy datasource, URL: " + url + ", message: " + e.getMessage());
         }
      }

      return (test);
   }

   public static String getHTMLStats(String tableId, String tableClass) {
      if (SQLDataSourceFactory.getInstance().getDataSources().isEmpty()) {
         return (null);
      }

      StringBuilder html = new StringBuilder();
      html.append("<table");
      if (Validator.isNotNull(tableId)) {
         html.append(" id='").append(tableId).append("'");
      }
      if (Validator.isNotNull(tableClass)) {
         html.append(" class='").append(tableClass).append("'");
      }
      html.append(">");

      html.append("<thead>");
      html.append("<tr><th>URL</th><th>Stats</th></tr>");
      html.append("</thead>");
      html.append("<tbody>");
      for (Entry<String, DataSource> entry : SQLDataSourceFactory.getInstance().getDataSources().entrySet()) {
         DataSource dataSource = entry.getValue();

         html.append("<tr><td>").append(entry.getKey()).append("</td>");
         html.append("<td>");
         if (dataSource != null) {
            JSONObject jsonStats = JSONFactoryUtil.createJSONObject();
            jsonStats.put("class", dataSource.getClass().getName());
            html.append(jsonStats.toString());
         }
         html.append("</td></tr>");
      }
      html.append("</tbody>");
      html.append("</table>");
      return (html.toString());
   }

   public JSONArray getJSONStatus() {
      if (SQLDataSourceFactory.getInstance().getDataSources().isEmpty()) {
         return (null);
      }

      JSONArray json = JSONFactoryUtil.createJSONArray();
      for (Entry<String, DataSource> entry : SQLDataSourceFactory.getInstance().getDataSources().entrySet()) {
         DataSource dataSource = entry.getValue();

         JSONObject jsonDataSource = JSONFactoryUtil.createJSONObject();
         json.put(jsonDataSource);
         jsonDataSource.put("url", entry.getKey());
         jsonDataSource.put("status", dataSource != null);

         JSONObject jsonStats = JSONFactoryUtil.createJSONObject();
         jsonDataSource.put("stats", jsonStats);
         jsonStats.put("className", dataSource.getClass().getName());

         boolean isC3p0 = false;
         try {
            dataSource.isWrapperFor(PooledDataSource.class);
         } catch (SQLException e) {
            _log.error("SQL exception checking data source unwrap for c3p0");
         }

         if (isC3p0) {
            PooledDataSource pooledDataSource = null;
            try {
               pooledDataSource = dataSource.unwrap(PooledDataSource.class);
            } catch (SQLException e) {
               _log.error("SQL exception unwrapping the datasource to c3p0");
            }

            if (pooledDataSource != null) {

               try {
                  jsonStats.put("numBusyConnectionsAllUsers", pooledDataSource.getNumBusyConnectionsAllUsers());
                  jsonStats.put("numBusyConnectionsDefaultUser", pooledDataSource.getNumBusyConnectionsDefaultUser());
                  jsonStats.put("numConnectionsAllUsers ", pooledDataSource.getNumConnectionsAllUsers());
                  jsonStats.put("numConnectionsDefaultUser ", pooledDataSource.getNumConnectionsDefaultUser());
                  jsonStats.put("numFailedCheckinsDefaultUser ", pooledDataSource.getNumFailedCheckinsDefaultUser());
                  jsonStats.put("numFailedCheckoutsDefaultUser", pooledDataSource.getNumFailedCheckoutsDefaultUser());
                  jsonStats.put("numFailedIdleTestsDefaultUser", pooledDataSource.getNumFailedIdleTestsDefaultUser());
                  jsonStats.put("numHelperThreads ", pooledDataSource.getNumHelperThreads());
                  jsonStats.put("numIdleConnectionsAllUsers", pooledDataSource.getNumIdleConnectionsAllUsers());
                  jsonStats.put("numIdleConnectionsDefaultUser", pooledDataSource.getNumIdleConnectionsDefaultUser());
                  jsonStats.put("numThreadsAwaitingCheckoutDefaultUser ", pooledDataSource.getNumThreadsAwaitingCheckoutDefaultUser());
                  jsonStats.put("numUnclosedOrphanedConnectionsAllUsers", pooledDataSource.getNumUnclosedOrphanedConnectionsAllUsers());
                  jsonStats.put("numUnclosedOrphanedConnectionsDefaultUser", pooledDataSource.getNumUnclosedOrphanedConnectionsDefaultUser());
                  jsonStats.put("numUserPools  ", pooledDataSource.getNumUserPools());
                  jsonStats.put("startTimeMillisDefaultUser", pooledDataSource.getStartTimeMillisDefaultUser());
                  jsonStats.put("statementCacheNumCheckedOutDefaultUser", pooledDataSource.getStatementCacheNumCheckedOutDefaultUser());
                  jsonStats.put("statementCacheNumCheckedOutStatementsAllUsers  ", pooledDataSource.getStatementCacheNumCheckedOutStatementsAllUsers());
                  jsonStats.put("statementCacheNumConnectionsWithCachedStatementsAllUsers", pooledDataSource.getStatementCacheNumConnectionsWithCachedStatementsAllUsers());
                  jsonStats.put("statementCacheNumConnectionsWithCachedStatementsDefaultUser", pooledDataSource.getStatementCacheNumConnectionsWithCachedStatementsDefaultUser());
                  jsonStats.put("statementCacheNumStatementsAllUsers", pooledDataSource.getStatementCacheNumStatementsAllUsers());
                  jsonStats.put("statementCacheNumStatementsDefaultUser", pooledDataSource.getStatementCacheNumStatementsDefaultUser());
                  jsonStats.put("statementDestroyerNumActiveThreads ", pooledDataSource.getStatementDestroyerNumActiveThreads());
                  jsonStats.put("statementDestroyerNumConnectionsInUseAllUsers  ", pooledDataSource.getStatementDestroyerNumConnectionsInUseAllUsers());
                  jsonStats.put("statementDestroyerNumConnectionsInUseDefaultUser  ", pooledDataSource.getStatementDestroyerNumConnectionsInUseDefaultUser());
                  jsonStats.put("statementDestroyerNumConnectionsWithDeferredDestroyStatementsAllUsers  ", pooledDataSource.getStatementDestroyerNumConnectionsWithDeferredDestroyStatementsAllUsers());
                  jsonStats.put("statementDestroyerNumConnectionsWithDeferredDestroyStatementsDefaultUser  ", pooledDataSource.getStatementDestroyerNumConnectionsWithDeferredDestroyStatementsDefaultUser());
                  jsonStats.put("statementDestroyerNumDeferredDestroyStatementsAllUsers  ", pooledDataSource.getStatementDestroyerNumDeferredDestroyStatementsAllUsers());
                  jsonStats.put("statementDestroyerNumDeferredDestroyStatementsDefaultUser  ", pooledDataSource.getStatementDestroyerNumDeferredDestroyStatementsDefaultUser());
                  jsonStats.put("statementDestroyerNumIdleThreads", pooledDataSource.getStatementDestroyerNumIdleThreads());
                  jsonStats.put("statementDestroyerNumTasksPending  ", pooledDataSource.getStatementDestroyerNumTasksPending());
                  jsonStats.put("statementDestroyerNumThreads ", pooledDataSource.getStatementDestroyerNumThreads());
                  jsonStats.put("threadPoolNumActiveThreads", pooledDataSource.getThreadPoolNumActiveThreads());
                  jsonStats.put("threadPoolNumIdleThreads  ", pooledDataSource.getThreadPoolNumIdleThreads());
                  jsonStats.put("threadPoolNumTasksPending ", pooledDataSource.getThreadPoolNumTasksPending());
                  jsonStats.put("threadPoolSize", pooledDataSource.getThreadPoolSize());
                  jsonStats.put("upTimeMillisDefaultUser", pooledDataSource.getUpTimeMillisDefaultUser());
               } catch (SQLException e) {
                  _log.error("SQL exception getting data source stats");
               } catch (Exception e) {
                  _log.error("Exception getting data source stats");
               }
            }
         }
      }
      return (json);
   }
}
