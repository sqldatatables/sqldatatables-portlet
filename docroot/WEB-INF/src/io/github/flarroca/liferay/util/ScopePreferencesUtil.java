package io.github.flarroca.liferay.util;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

public class ScopePreferencesUtil {

   public static PortletPreferences getPortalWidePreferences(PortletRequest portletRequest) throws SystemException {
      int ownerType = ResourceConstants.SCOPE_COMPANY;
      long ownerId = PortalUtil.getPortal().getCompanyId(portletRequest);
      long plid = 0;

      return getPreferences(portletRequest, ownerType, ownerId, plid);
   }

   public static PortletPreferences getSiteWidePreferences(PortletRequest portletRequest) throws SystemException {
      ThemeDisplay themeDisplay = getThemeDisplay(portletRequest);
      int ownerType = ResourceConstants.SCOPE_GROUP_TEMPLATE;
      long ownerId = themeDisplay.getScopeGroupId();
      long plid = 0;

      return getPreferences(portletRequest, ownerType, ownerId, plid);
   }

   public static PortletPreferences getPortletSpecificPreferences(PortletRequest portletRequest) throws SystemException {
      ThemeDisplay themeDisplay = getThemeDisplay(portletRequest);
      int ownerType = ResourceConstants.SCOPE_GROUP_TEMPLATE;
      long ownerId = themeDisplay.getScopeGroupId();
      long plid = themeDisplay.getPlid();

      return getPreferences(portletRequest, ownerType, ownerId, plid);
   }

   public static PortletPreferences getUserSpecificPortletSpecificPreferences(PortletRequest portletRequest) throws SystemException {
      ThemeDisplay themeDisplay = getThemeDisplay(portletRequest);
      int ownerType = ResourceConstants.SCOPE_INDIVIDUAL;
      long ownerId = PortalUtil.getPortal().getUserId(portletRequest);
      long plid = themeDisplay.getPlid();

      return getPreferences(portletRequest, ownerType, ownerId, plid);
   }

   public static PortletPreferences getUserSpecificPreferences(PortletRequest portletRequest) throws SystemException {
      int ownerType = ResourceConstants.SCOPE_INDIVIDUAL;
      long ownerId = PortalUtil.getPortal().getUserId(portletRequest);
      long plid = 0;

      return getPreferences(portletRequest, ownerType, ownerId, plid);
   }

   public static PortletPreferences getPreferences(PortletRequest portletRequest, int ownerType, long ownerId, long plid) throws SystemException {
      ThemeDisplay themeDisplay = getThemeDisplay(portletRequest);
      long companyId = PortalUtil.getPortal().getCompanyId(portletRequest);
      String portletId = themeDisplay.getPortletDisplay().getId();
      return PortletPreferencesLocalServiceUtil.getService().getPreferences(companyId, ownerId, ownerType, plid, portletId);
   }
   
   public static ThemeDisplay getThemeDisplay(PortletRequest portletRequest) {
      return((ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY));
   }
  
}