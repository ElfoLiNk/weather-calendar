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
package it.polimi.meteocal.util;

import it.polimi.meteocal.auth.User;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Class that give utility to user authentication in MeteoCal
 * 
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class AuthUtil {

    /**
     * Method that create a user session in MeteoCal
     * @param userID
     */
    public static void makeUserSession(Long userID) {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context
                .getExternalContext().getRequest();
        HttpSession session = request.getSession(true);
        User authUser = new User(userID);
        session.setAttribute(User.AUTH_KEY, authUser);
    }

    /**
     * if the user is logged gives the ID in MeteoCal 
     *
     * @return The ID of logged user
     */
    public static Long getUserID() {
        if (isUserLogged()) {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context
                    .getExternalContext().getRequest();
            HttpSession sessione = request.getSession();
            User authUser = (User) sessione.getAttribute(User.AUTH_KEY);
            return authUser.getUserID();
        }
        return (long) 0;
    }

    /**
     * Check if the user is logged
     *
     * @return true the user is logged in, false if the user isn't logged or the session is invalid
     */
    public static boolean isUserLogged() {
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletRequest request = (HttpServletRequest) context
                .getExternalContext().getRequest();

        HttpSession session = request.getSession();
        if (session == null) {
            return false;
        }
        User authUser = (User) session.getAttribute(User.AUTH_KEY);

        return authUser != null;
    }

}
