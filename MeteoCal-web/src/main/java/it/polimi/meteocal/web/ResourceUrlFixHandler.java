package it.polimi.meteocal.web;

import jakarta.faces.application.Resource;
import jakarta.faces.application.ResourceHandler;
import jakarta.faces.application.ResourceHandlerWrapper;
import jakarta.faces.application.ResourceWrapper;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Workaround for Mojarra 4.1.x on GlassFish 8:
 * 1. isResourceRequest() fails to recognise /jakarta.faces.resource/* as resource requests.
 * 2. handleResourceRequest() fails to serve resources even when recognised.
 * 3. Resource URLs have .xhtml appended due to the *.xhtml extension mapping.
 */
public class ResourceUrlFixHandler extends ResourceHandlerWrapper {

    private static final String RESOURCE_IDENTIFIER = ResourceHandler.RESOURCE_IDENTIFIER;
    private static final String XHTML_SUFFIX = ".xhtml";
    private static final int BUFFER_SIZE = 8192;

    private final ResourceHandler wrapped;

    public ResourceUrlFixHandler(ResourceHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ResourceHandler getWrapped() {
        return wrapped;
    }

    // ── Force correct resource-request detection ──────────────────────────────

    @Override
    public boolean isResourceRequest(FacesContext context) {
        String servletPath = context.getExternalContext().getRequestServletPath();
        String pathInfo    = context.getExternalContext().getRequestPathInfo();
        if (RESOURCE_IDENTIFIER.equals(servletPath)) return true;
        if (servletPath != null && servletPath.startsWith(RESOURCE_IDENTIFIER + "/")) return true;
        if (pathInfo != null && pathInfo.startsWith(RESOURCE_IDENTIFIER)) return true;
        return super.isResourceRequest(context);
    }

    // ── Serve resource directly, bypassing broken Mojarra 4.1 handling ────────

    @Override
    public void handleResourceRequest(FacesContext context) throws IOException {
        ExternalContext ec = context.getExternalContext();
        String pathInfo = ec.getRequestPathInfo();
        Map<String, String> params = ec.getRequestParameterMap();
        String libraryName = params.get("ln");

        if (pathInfo == null || pathInfo.isEmpty()) {
            ec.responseSendError(404, "Resource not found");
            return;
        }

        String resourceName = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;

        // Mojarra 4.1 evaluates #{resource[...]} EL in CSS files and produces relative URLs like
        // "../jakarta.faces.resource/primeicons/primeicons.woff2". The browser resolves these
        // relative to the CSS URL (/jakarta.faces.resource/primeicons/) producing a doubled path:
        // /jakarta.faces.resource/jakarta.faces.resource/primeicons/primeicons.woff2
        // Strip the duplicate prefix so we can locate the actual resource.
        String resourcePrefix = RESOURCE_IDENTIFIER.substring(1); // "jakarta.faces.resource"
        if (resourceName.startsWith(resourcePrefix)) {
            resourceName = resourceName.substring(resourcePrefix.length());
            if (resourceName.startsWith("/")) resourceName = resourceName.substring(1);
        }

        Resource resource = context.getApplication().getResourceHandler()
                .createResource(resourceName, libraryName);
        if (resource == null) {
            ec.responseSendError(404, "Resource not found: " + libraryName + "/" + resourceName);
            return;
        }

        Object responseObj = ec.getResponse();
        if (responseObj instanceof jakarta.servlet.http.HttpServletResponse response) {
            String contentType = resource.getContentType();
            if (contentType != null) response.setContentType(contentType);

            // Basic cache headers
            response.setHeader("Cache-Control", "max-age=86400, public");

            try (InputStream in = resource.getInputStream();
                 OutputStream out = response.getOutputStream()) {
                byte[] buf = new byte[BUFFER_SIZE];
                int n;
                while ((n = in.read(buf)) != -1) {
                    out.write(buf, 0, n);
                }
            }
            context.responseComplete();
        } else {
            super.handleResourceRequest(context);
        }
    }

    // ── Strip .xhtml from generated resource URLs ─────────────────────────────

    @Override
    public Resource createResource(String resourceName) {
        return wrap(super.createResource(resourceName));
    }

    @Override
    public Resource createResource(String resourceName, String libraryName) {
        return wrap(super.createResource(resourceName, libraryName));
    }

    @Override
    public Resource createResource(String resourceName, String libraryName, String contentType) {
        return wrap(super.createResource(resourceName, libraryName, contentType));
    }

    @Override
    public Resource createResourceFromId(String resourceId) {
        return wrap(super.createResourceFromId(resourceId));
    }

    private Resource wrap(Resource resource) {
        if (resource == null) return null;
        return new ResourceWrapper(resource) {
            @Override
            public String getRequestPath() {
                String path = getWrapped().getRequestPath();
                if (path == null) return null;

                // Strip .xhtml suffix (before or after query string)
                int q = path.indexOf('?');
                if (q > 0) {
                    String p = path.substring(0, q);
                    if (p.endsWith(XHTML_SUFFIX)) {
                        path = p.substring(0, p.length() - XHTML_SUFFIX.length()) + path.substring(q);
                    }
                } else if (path.endsWith(XHTML_SUFFIX)) {
                    path = path.substring(0, path.length() - XHTML_SUFFIX.length());
                }

                // Convert relative resource URLs to absolute to prevent CSS @font-face path doubling.
                // Mojarra 4.1 may return paths like "../jakarta.faces.resource/..." which browsers
                // resolve relative to the CSS location, producing doubled resource identifiers.
                if (!path.startsWith("/") && !path.startsWith("http")) {
                    int idx = path.indexOf(RESOURCE_IDENTIFIER);
                    if (idx >= 0) {
                        FacesContext ctx = FacesContext.getCurrentInstance();
                        if (ctx != null) {
                            String contextPath = ctx.getExternalContext().getRequestContextPath();
                            path = contextPath + path.substring(idx);
                        }
                    }
                }

                return path;
            }
        };
    }
}
