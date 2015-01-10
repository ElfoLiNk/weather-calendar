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
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class RescheduleNotificationDTO extends NotificationDTO {

    private String eventId;
    private String suggestedEventId;

    public String getSuggestedEventId() {
        return suggestedEventId;
    }

    public void setSuggestedEventId(String suggestedEventId) {
        this.suggestedEventId = suggestedEventId;
    }
    private Status status;

    public RescheduleNotificationDTO() {

    }

    public String getEventId() {
        return eventId;
    }

    public RescheduleNotificationDTO(String id, String eventId, String suggestedEventId, Status status, String userId, String message) {
        super(id, userId, message);
        this.eventId = eventId;
        this.suggestedEventId = suggestedEventId;
        this.status = status;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
