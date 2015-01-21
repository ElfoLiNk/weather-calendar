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
package it.polimi.meteocal.auth;

/**
 * Class for the user session on MeteoCal
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class User {

    private Long userID;

    /**
     * AUTH_KEY
     */
    public static final String AUTH_KEY = "USER";

    /**
     * Constructor wirh id
     * @param userID the user id logged 
     */
    public User(Long userID) {
        this.userID = userID;
    }

    /**
     * @return the id of the user
     */
    public Long getUserID() {
        return userID;
    }

    /**
     * @param userID to set
     */
    public void setUserID(Long userID) {
        this.userID = userID;
    }

}
