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
package it.polimi.meteocal.entities;

import it.polimi.meteocal.util.Gender;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

/**
 * Entity implementation class for Entity: User
 *
 */
@Entity
@NamedQueries({
    @NamedQuery(name = User.FIND_BY_TWITTER_ID, query = "SELECT u FROM User u WHERE u.twitterId = :twitterId "),
    @NamedQuery(name = User.FIND_BY_GOOGLE_ID, query = "SELECT u FROM User u WHERE u.googleId = :googleId "),
    @NamedQuery(name = User.FIND_BY_FACEBOOK_ID, query = "SELECT u FROM User u WHERE u.facebookId = :facebookId "),
    @NamedQuery(name = User.FIND_BY_EMAIL, query = "SELECT u FROM User u WHERE u.email = :email "),
    @NamedQuery(name = User.FIND_BY_EMAIL_AND_PASSWORD, query = "SELECT u FROM User u WHERE u.email = :email and u.password = :password "),
    @NamedQuery(name = User.FIND_BY_SEARCHQUERY, query = "SELECT u FROM User u WHERE u.calendar.visibility = it.polimi.meteocal.util.Visibility.PUBLIC AND (u.email LIKE :query OR u.firstName LIKE :query OR u.lastName LIKE :query OR CONCAT(u.firstName, ' ', u.lastName) LIKE :query)"),
    @NamedQuery(name = User.FIND_BY_SEARCH, query = "SELECT u FROM User u WHERE u.email LIKE :query OR u.firstName LIKE :query OR u.lastName LIKE :query OR CONCAT(u.firstName, ' ', u.lastName) LIKE :query"),
    @NamedQuery(name = User.FIND_BY_CALENDAR_ID, query = "SELECT u FROM User u WHERE u.calendar.id = :calendarId"),})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String FIND_BY_SEARCHQUERY = "User.FIND_BY_SEARCHQUERY";
    public static final String FIND_BY_SEARCH = "User.FIND_BY_SEARCH";
    public static final String FIND_BY_CALENDAR_ID = "User.FIND_BY_CALENDAR_ID";
    public static final String FIND_BY_TWITTER_ID = "User.FIND_BY_TWITTER_ID";
    public static final String FIND_BY_GOOGLE_ID = "User.FIND_BY_GOOGLE_ID";
    public static final String FIND_BY_FACEBOOK_ID = "User.FIND_BY_FACEBOOK_ID";
    public static final String FIND_BY_EMAIL = "User.FIND_BY_EMAIL";
    public static final String FIND_BY_EMAIL_AND_PASSWORD = "User.FIND_BY_EMAIL_AND_PASSWORD";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "First Name cannot be empty")
    private String firstName;
    @NotNull(message = "Last Name cannot be empty")
    private String lastName;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateBirth;
    @NotNull(message = "Email cannot be empty")
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "Invalid email")
    private String email;
    @NotNull(message = "Password cannot be empty")
    private String password;

    private String avatar;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Calendar calendar = new Calendar();

    @OneToMany
    @JoinTable(name = "user_preferedcalendar")
    private List<Calendar> listPreferedCalendars;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Setting setting;

    @OneToMany
    private List<Notification> listNotifications;

    // 
    private String role = "USER";

    // SOCIAL 
    private Long twitterId;
    private String twitterToken;
    private String twitterTokenSecret;
    private String googleId;

    // Il token di autenticazione che utilizza  google è molto lungo
    @Column(columnDefinition = "TEXT")
    private String googleToken;
    private String facebookId;
    @Column(columnDefinition = "TEXT")
    private String facebookToken;

    @ObjectTypeConverter(name = "gender", objectType = Gender.class, dataType = String.class, conversionValues = {
        @ConversionValue(objectValue = "MALE", dataValue = "M"),
        @ConversionValue(objectValue = "FEMALE", dataValue = "F")})
    @Convert("gender")
    private Gender gender;

    public User() {
        super();
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public List<Calendar> getListPreferedCalendars() {
        return listPreferedCalendars;
    }

    public void setListPreferedCalendars(List<Calendar> listPreferedCalendars) {
        this.listPreferedCalendars = listPreferedCalendars;
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public List<Notification> getListNotifications() {
        return listNotifications;
    }

    public void setListNotifications(List<Notification> listNotifications) {
        this.listNotifications = listNotifications;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(Date dateBirth) {
        this.dateBirth = dateBirth;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTwitterToken() {
        return this.twitterToken;
    }

    public void setTwitterToken(String twitterToken) {
        this.twitterToken = twitterToken;
    }

    public String getTwitterTokenSecret() {
        return this.twitterTokenSecret;
    }

    public void setTwitterTokenSecret(String twitterTokenSecret) {
        this.twitterTokenSecret = twitterTokenSecret;
    }

    public Long getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(Long twitterId) {
        this.twitterId = twitterId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getGoogleToken() {
        return googleToken;
    }

    public void setGoogleToken(String googleToken) {
        this.googleToken = googleToken;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getFacebookToken() {
        return facebookToken;
    }

    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", dateBirth=" + dateBirth + ", email=" + email + ", password=" + password + ", avatar=" + avatar + ", calendar=" + calendar + ", listPreferedCalendars=" + listPreferedCalendars + ", setting=" + setting + ", listNotifications=" + listNotifications + ", role=" + role + ", twitterId=" + twitterId + ", twitterToken=" + twitterToken + ", twitterTokenSecret=" + twitterTokenSecret + ", googleId=" + googleId + ", googleToken=" + googleToken + ", facebookId=" + facebookId + ", facebookToken=" + facebookToken + '}';
    }

}
