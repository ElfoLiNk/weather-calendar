package it.polimi.meteocal.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

/**
 * Strips the .xhtml suffix that Mojarra 4.1 appends to resource URLs
 * (e.g. /jakarta.faces.resource/theme.css.xhtml → theme.css).
 */
@WebFilter("/jakarta.faces.resource/*")
public class ResourcePathFilter implements Filter {

    private static final String XHTML_SUFFIX = ".xhtml";

    @Override
    public void init(FilterConfig config) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String pathInfo = httpRequest.getPathInfo();
        if (pathInfo != null && pathInfo.endsWith(XHTML_SUFFIX)) {
            final String stripped = pathInfo.substring(0, pathInfo.length() - XHTML_SUFFIX.length());
            chain.doFilter(new HttpServletRequestWrapper(httpRequest) {
                @Override public String getPathInfo() { return stripped; }
                @Override public String getRequestURI() {
                    return getContextPath() + getServletPath() + stripped;
                }
            }, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {}
}
