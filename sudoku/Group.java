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

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * @author Matthew William Noel
 */
public final class Group {

  private final LinkedHashMap<Digit, Cell> cells =
       new LinkedHashMap<>(Digit.values().length);

  Group(Cell[] cells) {
    if (cells.length != Digit.values().length) {
      throw new IllegalArgumentException("Groups must contain exactly "
          + Digit.values().length + " Cells");
    }
    for (int i = 0; i < cells.length; i++) {
      this.cells.put(Digit.values()[i], cells[i]);
    }
  }

  @Override
  @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
  public boolean equals(Object obj) {
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    final Group other = (Group) obj;
    return Objects.equals(this.cells, other.cells);
  }

  public void forEach(BiConsumer<Digit, Cell> action) {
    this.cells.forEach(action);
  }

  public void forEachCell(Consumer<? super Cell> action) {
    this.cells.values().forEach(action);
  }

  public Cell getCell(Digit digit) {
    return this.cells.get(digit);
  }

  @SuppressWarnings("unchecked")
  public LinkedHashMap<Digit, Cell> getCells() {
    return (LinkedHashMap<Digit, Cell>) this.cells.clone();
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 23 * hash + Objects.hashCode(this.cells);
    return hash;
  }
}
