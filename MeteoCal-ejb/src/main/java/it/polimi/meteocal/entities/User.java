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
import java.util.Objects;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

/**
 * Entity that rappresent the User in MeteoCal
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Entity
@Table(name = "user")
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

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_BY_SEARCHQUERY = "User.FIND_BY_SEARCHQUERY";

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_BY_SEARCH = "User.FIND_BY_SEARCH";

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_BY_CALENDAR_ID = "User.FIND_BY_CALENDAR_ID";

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_BY_TWITTER_ID = "User.FIND_BY_TWITTER_ID";

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_BY_GOOGLE_ID = "User.FIND_BY_GOOGLE_ID";

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_BY_FACEBOOK_ID = "User.FIND_BY_FACEBOOK_ID";

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_BY_EMAIL = "User.FIND_BY_EMAIL";

    /**
     * Name for the NamedQuery
     */
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

    @OneToOne(cascade = CascadeType.ALL)
    private Calendar calendar = new Calendar();

    @OneToMany
    @JoinTable(name = "user_preferedcalendar")
    private List<Calendar> listPreferedCalendars;

    @OneToOne(cascade = CascadeType.ALL)
    private Setting setting;

    @OneToMany
    private List<Notification> listNotifications;

    // GROUP
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

    /**
     * Default Constructor
     */
    public User() {
        super();
    }

    /**
     *
     * @return the calendar of the user
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     *
     * @param calendar the calendar to set
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     *
     * @return the list of preferred calendars of the user
     */
    public List<Calendar> getListPreferedCalendars() {
        return listPreferedCalendars;
    }

    /**
     *
     * @param listPreferedCalendars the list of preferred calendars to set
     */
    public void setListPreferedCalendars(List<Calendar> listPreferedCalendars) {
        this.listPreferedCalendars = listPreferedCalendars;
    }

    /**
     *
     * @return the setting of the user
     */
    public Setting getSetting() {
        return setting;
    }

    /**
     *
     * @param setting the setting to set
     */
    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    /**
     *
     * @return the list of notification of the user
     */
    public List<Notification> getListNotifications() {
        return listNotifications;
    }

    /**
     *
     * @param listNotifications the list of notification to set
     */
    public void setListNotifications(List<Notification> listNotifications) {
        this.listNotifications = listNotifications;
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
     * @return the date of birth of the user
     */
    public Date getDateBirth() {
        return dateBirth;
    }

    /**
     *
     * @param dateBirth the date of birth to set
     */
    public void setDateBirth(Date dateBirth) {
        this.dateBirth = dateBirth;
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
    public Long getId() {
        return this.id;
    }

    /**
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *
     * @return the twitter token of the user
     */
    public String getTwitterToken() {
        return this.twitterToken;
    }

    /**
     *
     * @param twitterToken the twitter token to set
     */
    public void setTwitterToken(String twitterToken) {
        this.twitterToken = twitterToken;
    }

    /**
     *
     * @return the twitter token secret of the user
     */
    public String getTwitterTokenSecret() {
        return this.twitterTokenSecret;
    }

    /**
     *
     * @param twitterTokenSecret the twitter token secret to set
     */
    public void setTwitterTokenSecret(String twitterTokenSecret) {
        this.twitterTokenSecret = twitterTokenSecret;
    }

    /**
     *
     * @return the twitter id of the user
     */
    public Long getTwitterId() {
        return twitterId;
    }

    /**
     *
     * @param twitterId the twitter id to set
     */
    public void setTwitterId(Long twitterId) {
        this.twitterId = twitterId;
    }

    /**
     *
     * @return the role of the user
     */
    public String getRole() {
        return role;
    }

    /**
     *
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     *
     * @return the google id of the user
     */
    public String getGoogleId() {
        return googleId;
    }

    /**
     *
     * @param googleId the google id to set
     */
    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    /**
     *
     * @return the google token of the user
     */
    public String getGoogleToken() {
        return googleToken;
    }

    /**
     *
     * @param googleToken the google token to set
     */
    public void setGoogleToken(String googleToken) {
        this.googleToken = googleToken;
    }

    /**
     *
     * @return the facebook id of the user
     */
    public String getFacebookId() {
        return facebookId;
    }

    /**
     *
     * @param facebookId the facebook id to set
     */
    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    /**
     *
     * @return the facebook token of the user
     */
    public String getFacebookToken() {
        return facebookToken;
    }

    /**
     *
     * @param facebookToken the facebook token to set
     */
    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
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
     * @return the gender of the user
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.id);
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
        final User other = (User) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

//    @Override
//    public String toString() {
//        return "User{" + "id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", dateBirth=" + dateBirth + ", email=" + email + ", password=" + password + ", avatar=" + avatar + ", calendar=" + calendar.getId() + ", listPreferedCalendars=" + listPreferedCalendars.size() + ", setting=" + setting + ", listNotifications=" + listNotifications + ", role=" + role + ", twitterId=" + twitterId + ", twitterToken=" + twitterToken + ", twitterTokenSecret=" + twitterTokenSecret + ", googleId=" + googleId + ", googleToken=" + googleToken + ", facebookId=" + facebookId + ", facebookToken=" + facebookToken + '}';
//    }

}
