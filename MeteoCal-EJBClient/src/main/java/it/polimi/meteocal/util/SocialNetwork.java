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
package it.polimi.meteocal.util;

/**
 * SocialNetwork: Facebook,Twitter,GooglePlus;
 * @author Matteo Gazzetta, Alessandro Fato
 */
public enum SocialNetwork {

    FACEBOOK, TWITTER, GOOGLE_PLUS;

    public static SocialNetwork use(String socialNetwork) {
        if (socialNetwork.equals(SocialNetwork.FACEBOOK.name())) {
            return SocialNetwork.FACEBOOK;
        }
        if (socialNetwork.equals(SocialNetwork.TWITTER.name())) {
            return SocialNetwork.TWITTER;
        }
        if (socialNetwork.equals(SocialNetwork.GOOGLE_PLUS.name())) {
            return SocialNetwork.GOOGLE_PLUS;
        }
        return null;
    }
}
