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

import java.util.Objects;

/**
 * Class that rappresent the result of the search query
 * 
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class ResultDTO {

    private String type;
    private String id;
    private String name;

    /**
     * Default Constructor
     */
    public ResultDTO() {
    }

    /**
     *
     * @return the id of the result
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
     * @return the type of the result
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return the name of the result
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultDTO)) return false;
        ResultDTO resultDTO = (ResultDTO) o;
        return Objects.equals(type, resultDTO.type) &&
                Objects.equals(id, resultDTO.id) &&
                Objects.equals(name, resultDTO.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id, name);
    }

    @Override
    public String toString() {
        return "ResultDTO{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
