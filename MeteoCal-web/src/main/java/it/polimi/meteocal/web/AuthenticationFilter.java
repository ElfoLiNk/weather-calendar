/*
 * Copyright (C) 2014 Matteo Gazzetta, Alessandro Fato
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.polimi.meteocal.web;

import it.polimi.meteocal.auth.User;
import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * AuthenticationFilter that check the session of the users
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class AuthenticationFilter implements Filter {

    /**
     * The doFilter method that check if the user is authenticated so if there
     * is a valid session
     *
     * @param req the request of the client
     * @param resp the response of the server
     * @param chain the chain of the communication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpSession session = request.getSession();
        User userAuth = (User) session.getAttribute(User.AUTH_KEY);

        if (userAuth == null) {
            HttpServletResponse response = (HttpServletResponse) resp;
            String loginUrl = request.getContextPath() + "/index.xhtml";
            String ajaxHeader = request.getHeader("Faces-Request");
            if ("partial/ajax".equals(ajaxHeader)) {
                response.setContentType("text/xml;charset=UTF-8");
                response.getWriter().write(
                        "<?xml version='1.0' encoding='UTF-8'?>"
                        + "<partial-response><redirect url=\"" + loginUrl + "\"/></partial-response>");
            } else {
                response.sendRedirect(loginUrl);
            }
        } else {
            chain.doFilter(req, resp);
        }
    }

    /**
     * Init of the filter
     *
     * @param config the config of the filter to set
     */
    @Override
    public void init(FilterConfig config) {
        // No initialization required
    }

    /**
     * Destroy the filter
     */
    @Override
    public void destroy() {
        // No cleanup required
    }

}
