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
package it.polimi.meteocal.ejb;

import javax.ejb.Local;

/**
 * Class that handle the user authentication with Facebook
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Local
public interface HandleAuthFacebook {

    /**
     * Method that execute the procedure for the Facebook Login and check/create
     * the user in the DB
     *
     * @param faceCode initial code for the OAuth authorization
     * @return true if the login is succesfull, false otherwise
     */
    public boolean doLoginFacebook(String faceCode);

    /**
     * Method that creates the Facebook login url for the user session
     *
     * @return the Facebook login url
     */
    public String getUrlLoginFacebook();

    /**
     * Method that check if the user is connected by Facebook
     *
     * @return true if the user is connected by the Facebook login, false
     * otherwise
     */
    public boolean isFacebookCollegato();

    /**
     * Method that check if the user identified by the userID is connected by
     * Facebook
     *
     * @param userID
     * @return true if the user is connected by the Facebook login, false
     * otherwise
     */
    public boolean isFacebookCollegato(long userID);

}
