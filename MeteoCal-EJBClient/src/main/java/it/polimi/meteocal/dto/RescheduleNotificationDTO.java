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

import it.polimi.meteocal.util.Status;

/**
 * Class that maps the RescheduleNotification entity
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class RescheduleNotificationDTO extends NotificationDTO {

    private String eventId;
    private String suggestedEventId;

    private Status status;

    /**
     * Default Constructor
     */
    public RescheduleNotificationDTO() {

    }

    /**
     *
     * @param id the id of the reschedule notification
     * @param eventId the id of the event related to the reschedule notification
     * @param status the status of the reschedule notification
     * @param userId the id of the user wich the notification is related
     * @param message the message of the notification
     * @param suggestedEventId the id of the suggested event of the reschedule notification
     */
    public RescheduleNotificationDTO(String id, String eventId, String suggestedEventId, Status status, String userId, String message) {
        super(id, userId, message);
        this.eventId = eventId;
        this.suggestedEventId = suggestedEventId;
        this.status = status;
    }

    /**
     *
     * @return the suggested event id of the reschedule notification
     */
    public String getSuggestedEventId() {
        return suggestedEventId;
    }

    /**
     *
     * @param suggestedEventId the suggested event id to set
     */
    public void setSuggestedEventId(String suggestedEventId) {
        this.suggestedEventId = suggestedEventId;
    }

    /**
     *
     * @return the event id of the reschedule notification
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * 
     * @param eventId the event id to set
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * 
     * @return the status of the reschedule notification
     */
    public Status getStatus() {
        return status;
    }

    /**
     *
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

}
