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
package it.polimi.meteocal.dto;

/**
 * Class that maps the Notification entity
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class NotificationDTO {

    /**
     *
     * @param id the id of the notification
     * @param userId the id of the user wich the notification is related
     * @param message the message of the notification
     */
    public NotificationDTO(String id, String userId, String message) {
        this.id = id;
        this.userId = userId;
        this.message = message;
    }

    private String id;

    /**
     *
     * @return the id of the notification
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    private String userId;

    private String message;

    /**
     * Default Constructor
     */
    public NotificationDTO() {
    }

    /**
     *
     * @return the id of the user related to the notification
     */
    public String getUserId() {
        return userId;
    }

    /**
     *
     * @param userId the user id to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     *
     * @return the message of the notification
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
