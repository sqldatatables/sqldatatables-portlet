package io.github.flarroca.liferay.sql.datasource;

import com.liferay.portal.model.Portlet;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portlet.BaseControlPanelEntry;


/**
 * Control panel entry class SQLPoolsControlPanelEntry
 */
public class SQLDataSourceControlPanelEntry extends BaseControlPanelEntry {

    @Override
    public boolean isVisible(PermissionChecker permissionChecker, Portlet portlet)
            throws Exception {
        return false;
    }
    
    // TODO: Add hasAccessPermission

}