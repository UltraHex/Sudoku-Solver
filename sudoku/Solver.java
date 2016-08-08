/*
 * Copyright (C) 2016 Matthew William Noel
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

import static sudoku.Coordinate.COORDINATES;
import static sudoku.Digit.ONE;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

/**
 *
 * @author Matthew William Noel
 */
public class Solver {

  private static Digit[] candidates(Group group) {
    ArrayList<Digit> candidates = new ArrayList<>(0);
    for (Digit digit : Digit.values()) {
      if (group.getCell(digit).getContents() == null) {
        candidates.add(digit);
      }
    }

    return candidates.toArray(new Digit[candidates.size()]);
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
    this.registers.forEach((Digit regi, Grid register) -> {
      register.forEachBox((Digit boxi, Group box) -> {
        Digit[] candidates = candidates(box);
        if (candidates.length == 2) {
          Cell cell1 = box.getCell(candidates[0]);
          Cell cell2 = box.getCell(candidates[1]);
          Coordinate coordinate1 = cell1.getCoordinate();
          Coordinate coordinate2 = cell2.getCoordinate();

          BiConsumer<Digit, Cell> eliminate = (Digit celli, Cell cell) -> {
            if (!cell.getCoordinate().toBoxCoordinate().getA()
                .equals(coordinate1.toBoxCoordinate().getA())) {
              cell.setContents(ONE);
            }
          };

          if (coordinate1.getA().equals(coordinate2.getA())) {
            register.forEachCellInRow(coordinate1.getA(),
                eliminate);
          } else if (coordinate1.getB().equals(coordinate2.getB())) {
            register.forEachCellInColumn(coordinate1.getB(),
                eliminate);
          }
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
    this.registers.forEach((Digit digit, Grid register) -> {
      BiConsumer<Digit, Group> singleCandidate = (Digit groupi, Group group) -> {
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

    this.cellVerticals.forEach((Coordinate coordinate, Group vertical) -> {
      Digit[] candidates = candidates(vertical);
      if (candidates.length == 1) {
        this.puzzle.getCell(coordinate).setContents(candidates[0]);
        this.hasChanged = true;
      }
    });
  }

  private void updateRegisters() {
    this.puzzle.forEachCell((Coordinate coord, Cell puzlleCell) -> {
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
