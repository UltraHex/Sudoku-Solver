/*
 * Copyright (C) 2015 Matthew William Noel
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
import java.util.function.BiConsumer;

/**
 *
 * @author Matthew William Noel
 */
public final class Grid {

  private final SuperGroup boxes;
  private final LinkedHashMap<Coordinate, Cell> cells =
      new LinkedHashMap<>(Digit.values().length ^ 2);
  private final SuperGroup columns;
  private final SuperGroup rows;

  public Grid() {
    Cell[][] protoCells =
        new Cell[Digit.values().length][Digit.values().length];
    this.cellGen(protoCells);

    Group[] protoBoxes = new Group[Digit.values().length];
    Group[] protoColumns = new Group[Digit.values().length];
    Group[] protoRows = new Group[Digit.values().length];

    this.groupGen(protoCells, protoBoxes, protoColumns, protoRows);
    this.boxes = new SuperGroup(protoBoxes);
    this.columns = new SuperGroup(protoColumns);
    this.rows = new SuperGroup(protoRows);
  }

  public Grid(Digit[][] cells) {
    this();

    if (cells.length != Digit.values().length) {
      throw new IllegalArgumentException("Incorrect grid size");
    }
    for (Digit[] row : cells) {
      if (row.length != Digit.values().length) {
        throw new IllegalArgumentException("Incorrect grid size");
      }
    }

    for (int i = 0; i < cells.length; i++) {
      for (int j = 0; j < cells[i].length; j++) {
        this.setCell(Coordinate.valueOf(
            Digit.values()[i], Digit.values()[j]), cells[i][j]);
      }
    }
  }

  public void forEachBox(BiConsumer<? super Digit, ? super Group> action) {
    this.boxes.forEach(action);
  }

  public void forEachCell(BiConsumer<? super Coordinate, ? super Cell> action) {
    this.cells.forEach(action);
  }

  public void forEachCellInBox(Digit box, BiConsumer<? super Digit, ? super Cell> action) {
    this.boxes.getGroup(box).forEach(action);
  }

  public void forEachCellInColumn(Digit column, BiConsumer<? super Digit, ? super Cell> action) {
    this.columns.getGroup(column).forEach(action);
  }

  public void forEachCellInRow(Digit row, BiConsumer<? super Digit, ? super Cell> action) {
    this.rows.getGroup(row).forEach(action);
  }

  public void forEachColumn(BiConsumer<? super Digit, ? super Group> action) {
    this.columns.forEach(action);
  }

  public void forEachRow(BiConsumer<? super Digit, ? super Group> action) {
    this.rows.forEach(action);
  }

  public void setAll(Digit contents) {
    this.cells.values().forEach((cell) -> cell.setContents(contents));
  }

  public Cell getCell(Coordinate coor) {
    return this.cells.get(coor);
  }

  public void setCell(Coordinate coordinate, Digit digit) {
    this.cells.get(coordinate).setContents(digit);
  }

  @Override
  public String toString() {
    String result = "";
    for (Coordinate coord : Coordinate.COORDINATES) {
      result += this.cells.get(coord);
      if (coord.getB().equals(Digit.valueOf(Digit.values().length))) {
        result += "\n";
      }
    }
    return result;
  }

  private void cellGen(Cell[][] protoCells) {
    for (int i = 0; i < Digit.values().length; i++) {
      for (int j = 0; j < Digit.values().length; j++) {
        Coordinate coord = Coordinate.valueOf(
            Digit.values()[i], Digit.values()[j]);
        Cell cell = new Cell(coord);
        protoCells[i][j] = cell;
        this.cells.put(coord, cell);
      }
    }
  }

  private void groupGen(Cell[][] protoCells, Group[] protoBoxes, Group[] protoColumns, Group[] protoRows) {
    for (Digit i : Digit.values()) {
      Cell[] box = new Cell[Digit.values().length];
      Cell[] column = new Cell[Digit.values().length];
      Cell[] row = new Cell[Digit.values().length];

      for (Digit j : Digit.values()) {
        Digit a = Coordinate.getRowFromBoxAndNumber(i, j);
        Digit b = Coordinate.getColumnFromBoxAndNumber(i, j);
        box[j.ordinal()] = protoCells[a.ordinal()][b.ordinal()];
        column[j.ordinal()] = protoCells[j.ordinal()][i.ordinal()];
        row[j.ordinal()] = protoCells[i.ordinal()][j.ordinal()];
      }

      protoBoxes[i.ordinal()] = new Group(box);
      protoColumns[i.ordinal()] = new Group(column);
      protoRows[i.ordinal()] = new Group(row);
    }
  }
}
