/*
 * Copyright (C) 2015 Matthew William Noel <matthew.william.noel@gmail.com>
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
package sudoku;

import java.util.Objects;

/**
 * Mutable
 *
 * @author Matthew William Noel <matthew.william.noel@gmail.com>
 */
public final class Cell implements Cloneable {

    private Digit contents; //immutable
    private final Coordinate coordinate; //immutable

    Cell(Coordinate coordinate, Digit contents) {
        this.coordinate = coordinate;
        this.contents = contents;
    }

    Cell(Coordinate coordinate) {
        this(coordinate, null);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Cell) ? this.contents.equals(
                ((Cell) obj).getContents()) : false;
    }

    public Digit getContents() {
        return this.contents;
    }

    public void setContents(Digit contents) {
        this.contents = contents;
    }

    public Coordinate getCoordinate() {
        return this.coordinate;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.contents);
        hash = 67 * hash + Objects.hashCode(this.coordinate);
        return hash;
    }

    @Override
    public String toString() {
        return String.valueOf(this.contents);
    }
}
