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
package it.polimi.meteocal.web;

import it.polimi.meteocal.ejb.HandleAuthFacebook;
import java.io.IOException;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe that handle the Facebook login from web
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Named
@SessionScoped
public class FacebookLogin implements Serializable {

    @EJB
    private HandleAuthFacebook handleAuthFacebook;

    int cont = 0;
    
    private static final Logger LOGGER = LogManager.getLogger(FacebookLogin.class.getName());

    /**
     * Method that execute the Facebook Login
     *
     * @return true: succesfull login; false: login failed
     */
    public boolean doLogin() {
        String faceCode = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap().get("code");
        boolean ris = handleAuthFacebook.doLoginFacebook(faceCode);
        if (ris) {
            try {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("calendar/calendar.xhtml");
                return true;
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e);
                return false;
            }
        }
        return false;
    }

    /**
     * Method that returns the url for the Facebook Login
     *
     * @return Facebook login url
     */
    public String getLinkLoginFacebook() {
        cont++;
        LOGGER.log(Level.INFO, "ContB: " + cont);

        return handleAuthFacebook.getUrlLoginFacebook();
    }

}
