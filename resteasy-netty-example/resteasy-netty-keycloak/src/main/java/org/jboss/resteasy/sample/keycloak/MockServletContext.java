package org.jboss.resteasy.sample.keycloak;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class MockServletContext implements ServletContext {

    @Override
    public String getContextPath() {
        return KeycloakNettyRunner.KEYCLOAK_PATH;
    }

    @Override
    public ServletContext getContext(String uripath) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getMajorVersion() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getMinorVersion() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getEffectiveMajorVersion() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getEffectiveMinorVersion() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getMimeType(String file) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Servlet getServlet(String name) throws ServletException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Enumeration<Servlet> getServlets() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Enumeration<String> getServletNames() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void log(String msg) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void log(Exception exception, String msg) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void log(String message, Throwable throwable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getRealPath(String path) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getServerInfo() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getInitParameter(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getAttribute(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAttribute(String name, Object object) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeAttribute(String name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getServletContextName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addListener(String className) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends EventListener> void addListener(T t) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void declareRoles(String... roleNames) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getVirtualServerName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
