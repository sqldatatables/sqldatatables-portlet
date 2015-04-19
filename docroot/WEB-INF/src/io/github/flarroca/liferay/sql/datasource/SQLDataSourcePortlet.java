package io.github.flarroca.liferay.sql.datasource;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class SQLPools
 */
public class SQLDataSourcePortlet extends MVCPortlet {

   public static final String ACTION_DESTROY_POOLS = "destroyPools";

   private static Log _log = LogFactoryUtil.getLog(SQLDataSourcePortlet.class);

   public void destroyPools(ActionRequest actionRequest, ActionResponse actionResponse) {
      destroyPools();
   }

   @Override
   public void destroy() {
      destroyPools();
      super.destroy();
   }
   
   private void destroyPools() {
      try {
         SQLDataSourceFactory.destroy();
         _log.info("All SQL pools destroyed");
      } catch (Exception e) {
         _log.error("Cannot destroy all SQL pools");
         e.printStackTrace();
      }
   }

}
