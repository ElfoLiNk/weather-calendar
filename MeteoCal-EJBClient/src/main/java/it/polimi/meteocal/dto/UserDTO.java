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

import it.polimi.meteocal.util.Gender;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Class that maps the User entity
 * 
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class UserDTO {

    private String id;
    private String firstName;
    private String lastName;
    private Gender gender;

    private Date dateBirth;
    private String email;
    private String password;
    private String avatar;

    private String calendarId;

    private SettingDTO setting;

    private List<NotificationDTO> notifications;

    private List<String> preferedCalendarsIDs;

    /**
     *
     * @param id the id of the user to set
     * @param firstName the first name of the user to set
     * @param lastName the last name of the user to set
     * @param gender the gender of the user to set
     * @param dateBirth the date of birth of the user to set
     * @param email the email of the user to set
     * @param password the password of the user to set
     * @param avatar the avatar of the user to set
     */
    public UserDTO(String id, String firstName, String lastName, Gender gender, Date dateBirth, String email, String password, String avatar) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateBirth = dateBirth;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
    }

    /**
     * Default Constructor
     */
    public UserDTO() {

    }

    @Override
    public String toString() {
        return "UserDTO{" + "id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", gender=" + gender + ", dateBirth=" + dateBirth + ", email=" + email + ", password=" + password + ", avatar=" + avatar + '}';
    }

    /**
     *
     * @return the id of the user's calendar
     */
    public String getCalendarId() {
        return calendarId;
    }

    /**
     *
     * @param calendarId the id of the calendar to set
     */
    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    /**
     *
     * @return the setting of the user
     */
    public SettingDTO getSetting() {
        return setting;
    }

    /**
     *
     * @param setting the setting to set
     */
    public void setSetting(SettingDTO setting) {
        this.setting = setting;
    }

    /**
     *
     * @return the list of preferred ids calendar by the user
     */
    public List<String> getPreferedCalendarsIDs() {
        return preferedCalendarsIDs;
    }

    /**
     *
     * @param preferedCalendarsIDs the list of ids of preferred calendar to set
     */
    public void setPreferedCalendarsIDs(List<String> preferedCalendarsIDs) {
        this.preferedCalendarsIDs = preferedCalendarsIDs;
    }

    /**
     *
     * @return the list of notifications of the user
     */
    public List<NotificationDTO> getNotifications() {
        return notifications;
    }

    /**
     *
     * @param notifications the notifications to set
     */
    public void setNotifications(List<NotificationDTO> notifications) {
        this.notifications = notifications;
    }

    /**
     *
     * @return the gendere of the user
     */
    public Gender getGender() {
        return gender;
    }

    /**
     *
     * @param gender the gender to set
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return the birth date of the user
     */
    public Date getDateBirth() {
        return dateBirth;
    }

    /**
     *
     * @param dateBirth the birth date to set
     */
    public void setDateBirth(Date dateBirth) {
        this.dateBirth = dateBirth;
    }

    /**
     *
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return the avatar of the user
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     *
     * @param avatar the avatar to set
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     *
     * @return the id of the user
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

    /**
     *
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDTO)) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id) &&
                Objects.equals(firstName, userDTO.firstName) &&
                Objects.equals(lastName, userDTO.lastName) &&
                gender == userDTO.gender &&
                Objects.equals(dateBirth, userDTO.dateBirth) &&
                Objects.equals(email, userDTO.email) &&
                Objects.equals(password, userDTO.password) &&
                Objects.equals(avatar, userDTO.avatar) &&
                Objects.equals(calendarId, userDTO.calendarId) &&
                Objects.equals(setting, userDTO.setting) &&
                Objects.equals(notifications, userDTO.notifications) &&
                Objects.equals(preferedCalendarsIDs, userDTO.preferedCalendarsIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, gender, dateBirth, email, password, avatar, calendarId, setting, notifications, preferedCalendarsIDs);
    }

}
