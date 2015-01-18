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

import it.polimi.meteocal.entities.Setting;
import it.polimi.meteocal.entities.User;
import it.polimi.meteocal.util.AuthUtil;
import it.polimi.meteocal.util.PasswordHash;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.TimeZone;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Session Bean implementation class HandleAuthTwitterImpl
 */
@Stateless
public class HandleAuthTwitterImpl implements HandleAuthTwitter {

    // OAuth Data
    private static final String CLIENT_ID = "< Insert CLIENT ID >";
    private static final String CLIENT_SECRET =  "< Insert CLIENT SECRET >";
    private static final String URL_BASE = "http://www.meteocal.tk";
    
    private static final Logger LOGGER = LogManager.getLogger(HandleAuthTwitterImpl.class.getName());

    /**
     * Method that return the Twitter object that allows the access to the
     * Twitter API
     *
     * @param user the user in MeteoCal
     * @return null if there was a problem with the creation of the Twitter
     * object
     */
    public static Twitter getTwitterObject(User user) {
        Twitter twitter;

        if (user.getTwitterToken() == null) {
            // Twitter not connected
            return null;
        }

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(CLIENT_ID);
        builder.setOAuthConsumerSecret(CLIENT_SECRET);
        Configuration configuration = builder.build();
        TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();

        AccessToken at = new AccessToken(user.getTwitterToken(),
                user.getTwitterTokenSecret());
        LOGGER.log(Level.INFO, at);
        try {
            twitter.setOAuthAccessToken(at);
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, e);
            return null;
        }
        return twitter;
    }

    /**
     * Default constructor.
     */
    @PersistenceContext
    EntityManager em;

    private final Twitter twitter;
    private RequestToken requestToken;
    private AccessToken accessToken;
    private int cont;

    public HandleAuthTwitterImpl() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(CLIENT_ID);
        builder.setOAuthConsumerSecret(CLIENT_SECRET);
        Configuration configuration = builder.build();
        TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();
        cont = 0;
    }

    @Override
    public String getUrlLoginTwitter() {
        String urlLogin = "error.xhtml";

        try {
            if (requestToken == null) {
                requestToken = twitter.getOAuthRequestToken(URL_BASE
                        + "/MeteoCal-web/loginTwitter.xhtml");
            }
            urlLogin = requestToken.getAuthenticationURL();
        } catch (TwitterException e) {
            LOGGER.log(Level.ERROR, e);
        }
        cont++;
        LOGGER.log(Level.INFO, "Conteggio: " + cont);
        LOGGER.log(Level.INFO, "URL LOGIN " + urlLogin);
        return urlLogin;
    }

    @Override
    public boolean doLoginTwitter(String verifier) {
        try {
            LOGGER.log(Level.INFO, "Verifier: " + verifier);

            accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

            if (!AuthUtil.isUserLogged()) {
                // Saves the new user data in the DB
                User utente;
                TypedQuery<User> q = em.createNamedQuery(User.FIND_BY_TWITTER_ID, User.class);
                q.setParameter("twitterId", twitter.getId());
                if (q.getResultList().isEmpty()) {
                    // The user isn't in the system
                    utente = new User();
                    twitter4j.User user = twitter.showUser(twitter.getId());
                    utente.setTwitterId(twitter.getId());
                    utente.setTwitterToken(accessToken.getToken());
                    utente.setTwitterTokenSecret(accessToken.getTokenSecret());
                    StringTokenizer stok = new StringTokenizer(user.getName());
                    utente.setFirstName(stok.nextToken());
                    utente.setLastName(stok.nextToken());
                    utente.setAvatar(user.getProfileImageURLHttps());
                    // MANCA EMAIL
                    utente.setEmail(utente.getFirstName().toLowerCase() + "." + utente.getLastName().toLowerCase() + "@twitter.com");
                    // MANCA BIRTH DATE
                    try {
                        utente.setPassword(PasswordHash.createHash(utente.getFirstName() + "." + utente.getLastName()));
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                        LOGGER.log(Level.FATAL, ex, ex);
                    }

                    Setting setting = new Setting();
                    setting.setTimeZone(TimeZone.getTimeZone(user.getTimeZone()));
                    utente.setSetting(setting);

                    LOGGER.log(Level.INFO, utente.toString());

                    em.persist(utente);
                    em.flush();
                    em.refresh(utente);
                } else {
                    // The user is already in the system
                    LOGGER.log(Level.INFO, "User already registred with twitter");

                    utente = q.getResultList().get(0);
                    if (utente.getTwitterToken().equals(accessToken.getToken())) {
                        LOGGER.log(Level.INFO, "Twitter token not changed");
                    } else {
                        LOGGER.log(Level.INFO, "Twitter token updated");
                    }
                    if (utente.getTwitterTokenSecret().equals(accessToken
                            .getTokenSecret())) {
                        LOGGER.log(Level.INFO, "TwitterToken secret not updated");
                    } else {
                        LOGGER.log(Level.INFO, "TwitterToken secret updated");
                        utente.setTwitterToken(accessToken.getToken());
                        utente.setTwitterTokenSecret(accessToken
                                .getTokenSecret());
                        em.merge(utente);
                        em.flush();
                    }

                }

                AuthUtil.makeUserSession(utente.getId());

            } else {
                // User already in the system
                User utente = em.find(User.class,
                        AuthUtil.getUserID());

                TypedQuery<User> q = em.createNamedQuery(User.FIND_BY_TWITTER_ID, User.class);
                q.setParameter("twitterId", twitter.getId());

                if (q.getResultList().isEmpty()) {
                    // The user account twitter isn't already present in the db so set the new Twitter data
                    utente.setTwitterId(twitter.getId());
                    utente.setTwitterToken(accessToken.getToken());
                    utente.setTwitterTokenSecret(accessToken.getTokenSecret());
                    em.merge(utente);
                    em.flush();
                } else {

                    // The user account is already in the system
                    LOGGER.log(Level.INFO, "User already registered in the system");
                    User utenteVecchio = q.getResultList().get(0);
                    if (!Objects.equals(utente.getId(), utenteVecchio.getId())) {
                        // Need to merge the two account
                        utente = HandleUserImpl.mergeUserAccount(utente,
                                utenteVecchio);

                        // set the new Twitter data
                        utente.setTwitterId(twitter.getId());
                        utente.setTwitterToken(accessToken.getToken());
                        utente.setTwitterTokenSecret(accessToken
                                .getTokenSecret());

                        em.merge(utente);
                        em.flush();

                        // Transfer all the settings
                        HandleUserImpl.mergeOldUserNewUser(em, utente,
                                utenteVecchio);

                        em.remove(utenteVecchio);
                        em.flush();
                    }
                }

            }

        } catch (TwitterException e) {
            LOGGER.log(Level.INFO, e);
            return false;
        }
        return true;
    }

    @Override
    public boolean isTwitterCollegato() {
        return isTwitterCollegato(AuthUtil.getUserID());
    }

    @Override
    public boolean isTwitterCollegato(long idUtente) {
        User utente = em.find(User.class, idUtente);
        if (utente == null) {
            return false;
        }
        return utente.getTwitterId() != null;

    }

}
