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
import it.polimi.meteocal.ejb.HandleAuthGoogle;
import it.polimi.meteocal.ejb.HandleAuthTwitter;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Class that handle the social network account
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Named
@RequestScoped
public class HandleAccount {

    @EJB
    HandleAuthTwitter handleAuthTwitter;
    @EJB
    HandleAuthGoogle handleAuthGoogle;
    @EJB
    HandleAuthFacebook handleAuthFacebook;

    /**
     *
     * @return
     */
    public boolean isTwitterCollegato() {
        return handleAuthTwitter.isTwitterCollegato();
    }

    /**
     *
     * @return
     */
    public boolean isGoogleCollegato() {
        return handleAuthGoogle.isGoogleCollegato();
    }

    /**
     *
     * @return
     */
    public boolean isFacebookCollegato() {
        return handleAuthFacebook.isFacebookCollegato();
    }

}
