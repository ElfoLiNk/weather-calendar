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
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * AuthenticationFilter that check the session of the users
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class AuthenticationFilter implements Filter {

    private FilterConfig config;

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
            ((HttpServletResponse) resp).sendError(
                    HttpServletResponse.SC_FORBIDDEN, "Forbiden Access!");
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
        this.config = config;
    }

    /**
     * Destroy the filter config
     */
    @Override
    public void destroy() {
        config = null;
    }

}
