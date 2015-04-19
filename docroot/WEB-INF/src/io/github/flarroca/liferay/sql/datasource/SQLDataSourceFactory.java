package io.github.flarroca.liferay.sql.datasource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.sql.DataSource;

import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.PropsUtil;

public class SQLDataSourceFactory {

   private Map<String, DataSource> dataSources = new HashMap<String, DataSource>();

   private static final SQLDataSourceFactory INSTANCE = new SQLDataSourceFactory();
   private static Log _log = LogFactoryUtil.getLog(SQLDataSourceFactory.class);

   private SQLDataSourceFactory() {
   }

   protected static Connection getConnection(Properties properties) throws SQLException, ClassNotFoundException, Exception {
      return (getInstance().getDataSourceConnection(properties));
   }

   protected static Connection getConnection(String propertiesString) throws SQLException, ClassNotFoundException, Exception {
      return (getInstance().getDataSourceConnection(propertiesString));
   }

   protected static Connection getConnection(String driverClassName, String url, String userName, String password) throws SQLException, ClassNotFoundException, Exception {
      return (getInstance().getDataSourceConnection(driverClassName, url, userName, password));
   }

   protected static void destroy(String url) throws Exception {
      getInstance().destroyDataSource(url);
   }

   protected static void destroy(Properties properties) throws Exception {
      getInstance().destroyDataSource(properties);
   }

   protected static void destroy() throws Exception {
      getInstance().destroyDataSources();
   }

   protected static SQLDataSourceFactory getInstance() {
      return INSTANCE;
   }

   protected Map<String, DataSource> getDataSources() {
      return dataSources;
   }

   private void destroyDataSources() throws Exception {
      int exceptions = 0;

      String defaultURL = PropsUtil.get(PropsKeys.JDBC_DEFAULT_URL);

      for (Entry<String, DataSource> entry : getDataSources().entrySet()) {
         if ((entry.getKey() != null) && (!(entry.getKey().equals(defaultURL)))) {
            if (entry.getValue() != null) {
               try {
                  DataSourceFactoryUtil.destroyDataSource(entry.getValue());
                  _log.info("Datasource destroyed: " + entry.getKey());
                  getDataSources().put(entry.getKey(), null);
               } catch (Exception e) {
                  exceptions++;
                  _log.error("Cannot destroy data source: " + entry.getKey());
                  e.printStackTrace();
               }
            }
         } else {
            _log.info("Cannot destroy the default data source: " + defaultURL);
         }
      }

      // Remove null entries
      Iterator<Entry<String, DataSource>> iterator = getDataSources().entrySet().iterator();
      while (iterator.hasNext()) {
         Entry<String, DataSource> entry = iterator.next();
         if (entry.getValue() == null) {
            iterator.remove();
         }
      }

      if (exceptions > 0) {
         _log.info("destroy exceptions: " + exceptions);
         throw new Exception();
      }
   }

   private void destroyDataSource(Properties properties) throws Exception {
      String url = properties.getProperty("url");
      destroyDataSource(url);
   }

   private void destroyDataSource(String url) throws Exception {
      if (Validator.isNull(url)) {
         _log.error("url cannot be empty");
         throw new Exception();
      }

      String defaultURL = PropsUtil.get(PropsKeys.JDBC_DEFAULT_URL);
      if (!(url.equals(defaultURL))) {

         DataSource dataSource = getDataSources().get(url);
         if (dataSource == null) {
            _log.error("Cannot find the data source, URL: " + url);
            throw new Exception();
         }

         try {
            DataSourceFactoryUtil.destroyDataSource(dataSource);
         } catch (Exception e) {
            _log.error("Cannot destroy the data source, URL: " + url + ", message: " + e.getMessage());
            e.printStackTrace();
         }
      }
   }

   private Connection getDataSourceConnection(String driverClassName, String url, String userName, String password) throws SQLException, ClassNotFoundException, Exception {
      Properties properties = new Properties();
      properties.setProperty("driverClassName", driverClassName);
      properties.setProperty("url", url);
      properties.setProperty("username", userName);
      properties.setProperty("password", password);
      properties.setProperty("maxPoolSize", "10"); // TODO: get maxPoolSize from portal properties
      properties.setProperty("minPoolSize", "5"); // TODO: get minPoolSize from portal properties
      return (getDataSourceConnection(properties));
   }

   private Connection getDataSourceConnection(String propertiesString) throws SQLException, ClassNotFoundException, Exception {
      if (Validator.isNull(propertiesString)) {
         _log.error("Connection properties are empty");
         throw new Exception();
      }
      InputStream propertiesStream = new ByteArrayInputStream(propertiesString.getBytes(StandardCharsets.UTF_8));
      Properties properties = new Properties();
      properties.load(propertiesStream);
      return (getDataSourceConnection(properties));
   }

   private Connection getDataSourceConnection(Properties properties) throws SQLException, ClassNotFoundException, Exception {
      String url = properties.getProperty("url");
      if (Validator.isNull(url)) {
         url = PropsUtil.get(PropsKeys.JDBC_DEFAULT_URL);
         if (Validator.isNull(url)) {
            _log.error("URL cannot be null");
            throw new Exception();
         }
      }

      DataSource dataSource = getDataSources().get(url);
      if (dataSource == null) {
         dataSource = createDataSource(properties);
         getDataSources().put(url, dataSource);
      }

      if (dataSource == null) {
         _log.error("Cannot create data source, URL: " + url);
         throw new Exception();
      }

      Connection connection = null;
      try {
         connection = dataSource.getConnection();
      } catch (SQLException e) {
         _log.error("Cannot get connection, URL: " + url + ", message:" + e.getMessage());
         throw new SQLException();
      }

      // Read only and disable auto commit always, for everything
      connection.setReadOnly(true);
      connection.setAutoCommit(false);

      return (connection);
   }

   private DataSource createDataSource(Properties properties) throws ClassNotFoundException, Exception {

      DataSource dataSource = null;
      String driverClassName = properties.getProperty("driverClassName");
      try {
         if (Validator.isNotNull(driverClassName)) {
            Class.forName(driverClassName);
         }
         dataSource = DataSourceFactoryUtil.initDataSource(properties);

      } catch (ClassNotFoundException e) {
         _log.error("Driver class name not found, " + driverClassName);
         throw e;
      } catch (Exception e) {
         String url = properties.getProperty("driverClassName");
         _log.error("Cannot init the data source, URL: " + url);
         throw e;
      }

      return dataSource;
   }

}
