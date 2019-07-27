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

import static io.github.ultrahex.sudoku.Coordinate.COORDINATES;
import static io.github.ultrahex.sudoku.Digit.ONE;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

/**
 * @author Matthew William Noel
 */
public class Solver {

  private static Digit[] candidates(Group group) {
    ArrayList<Digit> candidates = new ArrayList<>(0);
    group.forEach((digit, cell) -> {
      if (cell.getContents() == null) {
        candidates.add(digit);
      }
    });
    return candidates.toArray(new Digit[0]);
  }

  private final LinkedHashMap<Digit, SuperGroup> boxVerticals =
      new LinkedHashMap<>(Digit.values().length);
  private final LinkedHashMap<Coordinate, Group> cellVerticals =
      new LinkedHashMap<>(Digit.values().length ^ 2);
  private final LinkedHashMap<Digit, SuperGroup> columnVerticals =
      new LinkedHashMap<>(Digit.values().length);
  private boolean hasChanged;
  private final Grid puzzle;
  private final LinkedHashMap<Digit, Grid> registers =
      new LinkedHashMap<>(Digit.values().length);
  private final LinkedHashMap<Digit, SuperGroup> rowVerticals =
      new LinkedHashMap<>(Digit.values().length);

  public Solver(Grid puzzle) {
    this.puzzle = puzzle;
    this.hasChanged = true;

    for (Digit digit : Digit.values()) {
      this.registers.put(digit, new Grid());
    }

    this.initRegisters();
  }

  public boolean isComplete() {
    return COORDINATES.stream().noneMatch((coord)
        -> (this.puzzle.getCell(coord).getContents() == null));
  }

  public void solve() {
    while (this.hasChanged) {
      this.hasChanged = false;
      if (this.isComplete()) {
        return;
      }

      this.updateRegisters();

      this.singleCandidate();
      if (this.hasChanged) {
        continue;
      }

      this.candidateLines();
    }
  }

  @Override
  public String toString() {
    return this.puzzle.toString();
  }

  private void candidateLines() {
    this.registers.forEach((regi, register) -> {
      register.forEachBox((boxi, box) -> {
        Digit[] candidates = candidates(box);
        if (candidates.length < 2 || candidates.length > 3) {
          return;
        }

        Digit row = box.getCell(candidates[0]).getCoordinate().getA();
        Digit column = box.getCell(candidates[0]).getCoordinate().getB();

        BiConsumer<Digit, Cell> filter = (celli, cell) -> {
          if (!cell.getCoordinate().toBoxCoordinate().getA().equals(boxi)) {
            cell.setContents(ONE);
          }
        };

        if (box.getCell(candidates[1]).getCoordinate().getA().equals(row)) {
          if (candidates.length == 3 && !box.getCell(candidates[2]).getCoordinate().getA()
              .equals(row)) {
            return;
          }
          register.forEachCellInRow(row, filter);
        } else if (box.getCell(candidates[1]).getCoordinate().getB().equals(column)) {
          if (candidates.length == 3 && !box.getCell(candidates[2]).getCoordinate().getB()
              .equals(column)) {
            return;
          }
          register.forEachCellInColumn(column, filter);
        }
      });
    });
  }

  private void initRegisters() {
    for (Digit row : Digit.values()) {
      for (Digit column : Digit.values()) {
        Cell[] vertical = new Cell[Digit.values().length];
        for (Digit reg : Digit.values()) {
          vertical[reg.getValue() - 1] =
              this.registers.get(reg).getCell(
                  Coordinate.valueOf(row, column));
        }
        this.cellVerticals.put(Coordinate.valueOf(row, column),
            new Group(vertical));
      }
    }
  }

  private void singleCandidate() {
    this.registers.forEach((digit, register) -> {
      BiConsumer<Digit, Group> singleCandidate = (groupi, group) -> {
        Digit[] candidates = candidates(group);
        if (candidates.length == 1) {
          this.puzzle.getCell(group.getCell(candidates[0])
              .getCoordinate()).setContents(digit);
          this.hasChanged = true;
        }
      };
      register.forEachBox(singleCandidate);
      register.forEachRow(singleCandidate);
      register.forEachColumn(singleCandidate);
    });

    this.cellVerticals.forEach((coordinate, vertical) -> {
      Digit[] candidates = candidates(vertical);
      if (candidates.length == 1) {
        this.puzzle.getCell(coordinate).setContents(candidates[0]);
        this.hasChanged = true;
      }
    });
  }

  private void updateRegisters() {
    this.puzzle.forEachCell((coord, puzlleCell) -> {
      if (puzlleCell.getContents() != null) {
        this.cellVerticals.get(coord).forEach(
            (Celli, cell) -> cell.setContents(ONE));

        Grid reg = this.registers.get(puzlleCell.getContents());
        reg.forEachCellInBox(
            coord.toBoxCoordinate().getA(),
            (celli, cell) -> cell.setContents(ONE));
        reg.forEachCellInRow(
            puzlleCell.getCoordinate().getA(),
            (celli, cell) -> cell.setContents(ONE));
        reg.forEachCellInColumn(
            puzlleCell.getCoordinate().getB(),
            (celli, cell) -> cell.setContents(ONE));
      }
    });
  }

}
