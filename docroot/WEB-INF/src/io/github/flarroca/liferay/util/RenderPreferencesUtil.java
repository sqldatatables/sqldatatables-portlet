package io.github.flarroca.liferay.util;

import java.util.Map;

import javax.portlet.PortletPreferences;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.service.PortletPreferencesLocalService;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

public class RenderPreferencesUtil {

   public static final String TABLE_CLASS_DEFAULT = "table table-striped table-bordered table-condensed text-overflow";

   public static String getHTMLTable(ThemeDisplay themeDisplay) {
      return(getHTMLTable(themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(), themeDisplay.getSiteGroupId(), themeDisplay.getPlid(), themeDisplay.getUserId(), themeDisplay.getPortletDisplay().getId()));
   }      
   
   public static String getHTMLTable(long companyId, long scopeGroupId, long groupId, long plid, long userId, String portletId) {
      
      StringBuilder html = new StringBuilder(); 
      try {
         PortletPreferencesLocalService service = PortletPreferencesLocalServiceUtil.getService();
         PortletPreferences p1 = null;
         PortletPreferences p2 = null;
         PortletPreferences p3 = null;
         PortletPreferences p4 = null;

         p1 = service.getPreferences(companyId,0            ,ResourceConstants.SCOPE_COMPANY       ,   0,portletId);
         p2 = service.getPreferences(companyId,0            ,ResourceConstants.SCOPE_GROUP_TEMPLATE,   0,portletId);
         p3 = service.getPreferences(companyId,0            ,ResourceConstants.SCOPE_GROUP         ,   0,portletId);
         p4 = service.getPreferences(companyId,0            ,ResourceConstants.SCOPE_INDIVIDUAL    ,   0,portletId);
         if ((p1.getMap().size()>0)||(p2.getMap().size()>0)||(p3.getMap().size()>0)||(p4.getMap().size()>0)) {
            html.append("<h4>Global owner without page</h4>");
            html.append(getHTMLTable("<h5>Company</h5>",p1));
            html.append(getHTMLTable("<h5>Scope</h5>",p2));
            html.append(getHTMLTable("<h5>Group</h5>",p3));
            html.append(getHTMLTable("<h5>Individual</h5>",p4));
         }        
         
         p1 = service.getPreferences(companyId,0            ,ResourceConstants.SCOPE_COMPANY       ,   0,portletId);
         p2 = service.getPreferences(companyId,0            ,ResourceConstants.SCOPE_GROUP_TEMPLATE,   0,portletId);
         p3 = service.getPreferences(companyId,0            ,ResourceConstants.SCOPE_GROUP         ,   0,portletId);
         p4 = service.getPreferences(companyId,0            ,ResourceConstants.SCOPE_INDIVIDUAL    ,   0,portletId);
         if ((p1.getMap().size()>0)||(p2.getMap().size()>0)||(p3.getMap().size()>0)||(p4.getMap().size()>0)) {
            html.append("<h4>Global owner without page</h4>");
            html.append(getHTMLTable("<h5>Company</h5>",p1));
            html.append(getHTMLTable("<h5>Scope</h5>",p2));
            html.append(getHTMLTable("<h5>Group</h5>",p3));
            html.append(getHTMLTable("<h5>Individual</h5>",p4));
         }
         
         p1 = service.getPreferences(companyId,companyId    ,ResourceConstants.SCOPE_COMPANY       ,   0,portletId);
         p2 = service.getPreferences(companyId,scopeGroupId ,ResourceConstants.SCOPE_GROUP_TEMPLATE,   0,portletId);
         p3 = service.getPreferences(companyId,groupId      ,ResourceConstants.SCOPE_GROUP         ,   0,portletId);
         p4 = service.getPreferences(companyId,userId       ,ResourceConstants.SCOPE_INDIVIDUAL    ,   0,portletId);
         if ((p1.getMap().size()>0)||(p2.getMap().size()>0)||(p3.getMap().size()>0)||(p4.getMap().size()>0)) {
            html.append("<h4>Without page</h4>");
            html.append(getHTMLTable("<h5>Company</h5>",p1));
            html.append(getHTMLTable("<h5>Scope</h5>",p2));
            html.append(getHTMLTable("<h5>Group</h5>",p3));
            html.append(getHTMLTable("<h5>Individual</h5>",p4));
         }
         
         p1 = service.getPreferences(companyId,0            ,ResourceConstants.SCOPE_COMPANY       ,plid,portletId);
         p2 = service.getPreferences(companyId,0            ,ResourceConstants.SCOPE_GROUP_TEMPLATE,plid,portletId);
         p3 = service.getPreferences(companyId,0            ,ResourceConstants.SCOPE_GROUP         ,plid,portletId);
         p4 = service.getPreferences(companyId,0            ,ResourceConstants.SCOPE_INDIVIDUAL    ,plid,portletId);
         if ((p1.getMap().size()>0)||(p2.getMap().size()>0)||(p3.getMap().size()>0)||(p4.getMap().size()>0)) {
            html.append("<h4>Global for page ").append(plid).append("</h4>");
            html.append(getHTMLTable("<h5>Company</h5>",p1));
            html.append(getHTMLTable("<h5>Scope</h5>",p2));
            html.append(getHTMLTable("<h5>Group</h5>",p3));
            html.append(getHTMLTable("<h5>Individual</h5>",p4));
         }
         
         p1 = service.getPreferences(companyId,companyId    ,ResourceConstants.SCOPE_COMPANY       ,plid,portletId);
         p2 = service.getPreferences(companyId,scopeGroupId ,ResourceConstants.SCOPE_GROUP_TEMPLATE,plid,portletId);
         p3 = service.getPreferences(companyId,groupId      ,ResourceConstants.SCOPE_GROUP         ,plid,portletId);
         p4 = service.getPreferences(companyId,userId       ,ResourceConstants.SCOPE_INDIVIDUAL    ,plid,portletId);
         
         if ((p1.getMap().size()>0)||(p2.getMap().size()>0)||(p3.getMap().size()>0)||(p4.getMap().size()>0)) {
            html.append("<h4>For page ").append(plid).append("</h4>");
            html.append(getHTMLTable("<h5>Company</h5>",p1));
            html.append(getHTMLTable("<h5>Scope</h5>",p2));
            html.append(getHTMLTable("<h5>Group</h5>",p3));
            html.append(getHTMLTable("<h5>Individual</h5>",p4));
         }

         html.append("<h4>Scope</h4>");
         html.append("<ul>");
         html.append("   <li>Company: "     ).append(companyId   ).append("</li>");
         html.append("   <li>scopeGroupId: ").append(scopeGroupId).append("</li>");
         html.append("   <li>groupId: "     ).append(groupId     ).append("</li>");
         html.append("   <li>plid: "        ).append(plid        ).append("</li>");
         html.append("   <li>userId: "      ).append(userId      ).append("</li>");
         html.append("   <li>portletId: "   ).append(portletId   ).append("</li>");
         html.append("</ul>");
         
      } catch (SystemException e) {
         html.append(e.getMessage());
      }

      return(html.toString());
   }   
   
   public static String getHTMLTable(String title, PortletPreferences preferences) {
      return(((preferences!=null)&&(preferences.getMap().size()>0)?title:"")+getHTMLTable(preferences, TABLE_CLASS_DEFAULT));
   }

   public static String getHTMLTable(PortletPreferences preferences) {
      return(getHTMLTable(preferences, TABLE_CLASS_DEFAULT));
   }

   public static String getHTMLTable(PortletPreferences preferences,String tableClass) {

      Map<String, String[]> map = preferences.getMap();
      if (map.size()>0) {
         StringBuilder builder = new StringBuilder();
         builder.append("<table class='"+tableClass+"'>");
         builder.append("  <thead>");
         builder.append("     <tr><th>key</th><th>value</th><th></th></tr>");
         builder.append("  </thead>");
         builder.append("  <tbody>");
         for (Map.Entry<String, String[]> entry: map.entrySet()) {
            String key=entry.getKey();
            String[] value=entry.getValue();
            for (int i = 0; i < value.length; i++) {
               builder.append("<tr><td>").append(key).append("</td><td>").append(value[i]).append("</td><td>").append(i).append("</td></tr>");
            }
         }
         builder.append("  </tbody>");
         builder.append("</table>");
         return builder.toString();
      }
      return("");
   }

  
}