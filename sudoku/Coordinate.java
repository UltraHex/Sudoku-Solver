/*
 * Copyright (C) 2016 Matthew William Noel <matthew.william.noel@gmail.com>
 *
 * This file is part of Sudoku-Solver.
 *
 * Sudoku-Solver is free software: you can redistribute it and/or modify
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

import java.util.ArrayList;

/**
 * Immutable
 *
 * @author Matthew William Noel <matthew.william.noel@gmail.com>
 */
public final class Coordinate {

    private static final ArrayList<Coordinate> COORDINATES;

    static {
        COORDINATES = new ArrayList<>(81);
        for (Digit a : Digit.values()) {
            for (Digit b : Digit.values()) {
                COORDINATES.add(new Coordinate(a, b));
            }
        }
    }

    /*
     * These functions translate between coordinates like this:
     *
     *  1 2 3 ...
     * 1
     * 2
     * 3
     *...
     *
     * and coordinates like this:
     *
     * grid    box
     * 1 2 3 /1 2 3\
     * 4 5 6<|4 5 6|
     * 7 8 9 \7 8 9/
     */
    // Speciosa mathematica. Math is beautiful.
    //TODO generalize formulas
    public static Digit getBoxNumber(Digit row, Digit column) {
        int root = (int) Math.sqrt(Digit.values().length);
        int coarse = root * (row.ordinal() / root);
        int fine = column.ordinal() / root;
        return Digit.values()[coarse + fine];
    }

    public static Digit getColumnFromBoxAndNumber(Digit box, Digit number) {
        int root = (int) Math.sqrt(Digit.values().length);
        int coarse = root * (box.ordinal() % root);
        int fine = number.ordinal() % root;
        return Digit.values()[coarse + fine];
    }

    public static Digit getNumberInBox(Digit row, Digit column) {
        int root = (int) Math.sqrt(Digit.values().length);
        int coarse = root * (row.ordinal() % root);
        int fine = column.ordinal() % root;
        return Digit.values()[coarse + fine];
    }

    public static Digit getRowFromBoxAndNumber(Digit box, Digit number) {
        int root = (int) Math.sqrt(Digit.values().length);
        int coarse = root * (box.ordinal() / root);
        int fine = number.ordinal() / root;
        return Digit.values()[coarse + fine];
    }

    public static Coordinate valueOf(Digit a, Digit b) {
        return COORDINATES.get(COORDINATES.indexOf(new Coordinate(a, b)));
    }

    private final Digit a;
    private final Digit b;

    private Coordinate(Digit a, Digit b) {
        this.a = a;
        this.b = b;
    }

    public Digit getA() {
        return this.a;
    }

    public Digit getB() {
        return this.b;
    }

    public Coordinate toBoxCoordinate() {
        return Coordinate.valueOf(getBoxNumber(this.a, this.b),
                getNumberInBox(this.a, this.b));
    }
}
