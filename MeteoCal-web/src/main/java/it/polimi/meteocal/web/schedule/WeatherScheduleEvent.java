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
package it.polimi.meteocal.web.schedule;

import it.polimi.meteocal.dto.UserDTO;
import it.polimi.meteocal.dto.WeatherDTO;
import it.polimi.meteocal.util.Site;
import it.polimi.meteocal.util.Visibility;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.primefaces.model.DefaultScheduleEvent;

/**
 * Class that the extends the DefaultScheduleEvent adding the information needed
 * in MeteoCal
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class WeatherScheduleEvent extends DefaultScheduleEvent {

    public WeatherScheduleEvent(String id, String title, String description, Date start, Date end, boolean allDay, String location, Site site, Visibility visibility, String eoId, List<UserDTO> listParticipantAndInvitedUsers, List<UserDTO> eventParticipants, List<UserDTO> invitedUsers, WeatherDTO weather) {
        super(title, start, end, allDay);
        super.setId(id);
        super.setDescription(description);
        this.location = location;
        this.eoId = eoId;
        this.listParticipantAndInvitedUsers = listParticipantAndInvitedUsers;
        this.eventParticipants = eventParticipants;
        this.invitedUsers = invitedUsers;
        this.weather = weather;
        this.site = site.name();
        this.visibility = visibility.name();

    }

    public WeatherScheduleEvent() {
    }

    public WeatherScheduleEvent(String title, Date start, Date end) {
        super(title, start, end);
    }

    public WeatherScheduleEvent(String title, Date start, Date end, boolean allDay) {
        super(title, start, end, allDay);
    }

    public WeatherScheduleEvent(String title, Date start, Date end, String styleClass) {
        super(title, start, end, styleClass);
    }

    public WeatherScheduleEvent(String title, Date start, Date end, Object data) {
        super(title, start, end, data);
    }
    private String location;

    private String eoId;

    private List<UserDTO> listParticipantAndInvitedUsers;

    private List<UserDTO> eventParticipants;

    private List<UserDTO> invitedUsers;

    private WeatherDTO weather;

    private String site = "INDOOR";

    private String visibility = "PRIVATE";

    public String getEoId() {
        return eoId;
    }

    public void setEoId(String eoId) {
        this.eoId = eoId;
    }

    public List<UserDTO> getListParticipantAndInvitedUsers() {
        return listParticipantAndInvitedUsers;
    }

    public void setListParticipantAndInvitedUsers(List<UserDTO> listParticipantAndInvitedUsers) {
        this.listParticipantAndInvitedUsers = listParticipantAndInvitedUsers;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public List<UserDTO> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(List<UserDTO> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public WeatherDTO getWeather() {
        return weather;
    }

    public void setWeather(WeatherDTO weather) {
        this.weather = weather;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
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

    @Override
    public String toString() {
        return "WeatherScheduleEvent{" + "location=" + location + ", eoId=" + eoId + ", listParticipantAndInvitedUsers=" + listParticipantAndInvitedUsers + ", eventParticipants=" + eventParticipants + ", invitedUsers=" + invitedUsers + ", weather=" + weather + ", site=" + site + ", visibility=" + visibility + '}' + " SUPER: " + super.getDescription() + " " + super.getTitle() + " " + super.getStartDate().toString() + " " + super.getEndDate().toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.location);
        hash = 23 * hash + Objects.hashCode(this.eoId);
        hash = 23 * hash + Objects.hashCode(this.listParticipantAndInvitedUsers);
        hash = 23 * hash + Objects.hashCode(this.eventParticipants);
        hash = 23 * hash + Objects.hashCode(this.invitedUsers);
        hash = 23 * hash + Objects.hashCode(this.weather);
        hash = 23 * hash + Objects.hashCode(this.site);
        hash = 23 * hash + Objects.hashCode(this.visibility);
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
        final WeatherScheduleEvent other = (WeatherScheduleEvent) obj;
        if (!Objects.equals(this.location, other.location)) {
            return false;
        }
        if (!Objects.equals(this.eoId, other.eoId)) {
            return false;
        }
        if (!Objects.equals(this.listParticipantAndInvitedUsers, other.listParticipantAndInvitedUsers)) {
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
        if (!Objects.equals(this.site, other.site)) {
            return false;
        }
        if (!Objects.equals(this.visibility, other.visibility)) {
            return false;
        }
        return true;
    }

}
