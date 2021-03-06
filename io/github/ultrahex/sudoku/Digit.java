/*
 * Copyright © 2015-2016, 2019 Matthew William Noel
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

package io.github.ultrahex.sudoku;

/**
 * @author Matthew William Noel
 */
public enum Digit {

  ONE(1),
  TWO(2),
  THREE(3),
  FOUR(4),
  FIVE(5),
  SIX(6),
  SEVEN(7),
  EIGHT(8),
  NINE(9);

  private final int value;

  Digit(int value) {
    this.value = value;
  }

  public static Digit valueOf(int i) {
    return Digit.values()[i - 1];
  }

  public int getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return String.valueOf(this.value);
  }

}
