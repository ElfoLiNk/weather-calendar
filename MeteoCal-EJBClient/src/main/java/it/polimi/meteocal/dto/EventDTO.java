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

import it.polimi.meteocal.util.Site;
import it.polimi.meteocal.util.Visibility;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class EventDTO {

    private String id;

    private String eoId;

    private String title;

    private Date startDate;

    private Date endDate;

    private boolean allDay = false;

    private boolean editable;

    private Site site;

    private Visibility visibility;

    private String description;

    private String location;

    private List<UserDTO> eventParticipants;

    private List<UserDTO> invitedUsers;

    private WeatherDTO weather;

    public EventDTO(String id, String eoId, String title, Date startDate, Date endDate, boolean editable, Site site, Visibility visibility, String description, String location, List<UserDTO> eventParticipants, List<UserDTO> invitedUsers, WeatherDTO weather) {
        this.id = id;
        this.eoId = eoId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.editable = editable;
        this.site = site;
        this.visibility = visibility;
        this.description = description;
        this.location = location;
        this.eventParticipants = eventParticipants;
        this.invitedUsers = invitedUsers;
        this.weather = weather;
    }

    public WeatherDTO getWeather() {
        return weather;
    }

    public void setWeather(WeatherDTO weather) {
        this.weather = weather;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<UserDTO> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(List<UserDTO> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public String getEoId() {
        return eoId;
    }

    public void setEoId(String eoId) {
        this.eoId = eoId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<UserDTO> getEventParticipants() {
        return eventParticipants;
    }

    public void setEventParticipants(List<UserDTO> eventParticipants) {
        this.eventParticipants = eventParticipants;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public String toString() {
        return "EventDTO{" + "id=" + id + ", eoId=" + eoId + ", title=" + title + ", startDate=" + startDate + ", endDate=" + endDate + ", allDay=" + allDay + ", editable=" + editable + ", site=" + site + ", visibility=" + visibility + ", description=" + description + ", location=" + location + ", eventParticipants=" + eventParticipants + ", invitedUsers=" + invitedUsers + ", weather=" + weather + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.id);
        hash = 23 * hash + Objects.hashCode(this.eoId);
        hash = 23 * hash + Objects.hashCode(this.title);
        hash = 23 * hash + Objects.hashCode(this.startDate);
        hash = 23 * hash + Objects.hashCode(this.endDate);
        hash = 23 * hash + (this.allDay ? 1 : 0);
        hash = 23 * hash + (this.editable ? 1 : 0);
        hash = 23 * hash + Objects.hashCode(this.site);
        hash = 23 * hash + Objects.hashCode(this.visibility);
        hash = 23 * hash + Objects.hashCode(this.description);
        hash = 23 * hash + Objects.hashCode(this.location);
        hash = 23 * hash + Objects.hashCode(this.eventParticipants);
        hash = 23 * hash + Objects.hashCode(this.invitedUsers);
        hash = 23 * hash + Objects.hashCode(this.weather);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EventDTO other = (EventDTO) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.eoId, other.eoId)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.startDate, other.startDate)) {
            return false;
        }
        if (!Objects.equals(this.endDate, other.endDate)) {
            return false;
        }
        if (this.allDay != other.allDay) {
            return false;
        }
        if (this.editable != other.editable) {
            return false;
        }
        if (this.site != other.site) {
            return false;
        }
        if (this.visibility != other.visibility) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.location, other.location)) {
            return false;
        }
        if (!Objects.equals(this.eventParticipants, other.eventParticipants)) {
            return false;
        }
        if (!Objects.equals(this.invitedUsers, other.invitedUsers)) {
            return false;
        }
        if (!Objects.equals(this.weather, other.weather)) {
            return false;
        }
        return true;
    }

}
