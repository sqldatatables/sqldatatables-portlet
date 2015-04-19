<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>

<%@ page import="io.github.flarroca.liferay.sql.datasource.SQLDataSourceFactoryUtil"%>
<%@ page import="io.github.flarroca.liferay.sql.util.SQLParserUtil"%>
<%@ page import="io.github.flarroca.liferay.sql.util.SQLKeys"%>
<%@ page import="io.github.flarroca.liferay.sql.util.SQLRenderUtil"%>
<%@ page import="io.github.flarroca.liferay.util.RenderPreferencesUtil"%>
<%@ page import="io.github.flarroca.liferay.util.ScopePreferencesUtil"%>

<%@ page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>
<%@ page import="com.liferay.portal.kernel.util.StringPool"%>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@ page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ page import="com.liferay.portal.model.Portlet"%>
<%@ page import="com.liferay.portal.service.PortletLocalServiceUtil"%>
<%@ page import="com.liferay.portlet.PortletPreferencesFactoryUtil"%>

<%@ page import="javax.portlet.ActionRequest"%>
<%@ page import="javax.portlet.PortletPreferences"%>

<portlet:defineObjects />

<liferay-theme:defineObjects />