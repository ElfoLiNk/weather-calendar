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

/**
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class StatusDTO {

    private boolean facebookCollegato;
    private boolean googlePlusCollegato;
    private boolean twitterCollegato;

    public StatusDTO() {
    }

    public boolean isFacebookCollegato() {
        return facebookCollegato;
    }

    public void setFacebookCollegato(boolean facebookCollegato) {
        this.facebookCollegato = facebookCollegato;
    }

    public boolean isGooglePlusCollegato() {
        return googlePlusCollegato;
    }

    public void setGooglePlusCollegato(boolean googlePlusCollegato) {
        this.googlePlusCollegato = googlePlusCollegato;
    }

    public boolean isTwitterCollegato() {
        return twitterCollegato;
    }

    public void setTwitterCollegato(boolean twitterCollegato) {
        this.twitterCollegato = twitterCollegato;
    }

}
