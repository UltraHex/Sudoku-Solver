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
import java.util.Map;
import java.util.function.Consumer;
import static sudoku.Coordinate.COORDINATES;
import static sudoku.Digit.ONE;
import static sudoku.Digit.TWO;

/**
 *
 * @author Matthew William Noel <matthew.william.noel@gmail.com>
 */
public class Solver {

    private static Digit[] candidates(Group group, Digit digit) {
        Digit[] candidates = new Digit[digit.getValue()];
        int count = 0;
        for (Map.Entry<Digit, Cell> entry : group.getCells().entrySet()) {
            Cell cell = entry.getValue();
            if (cell.getContents() == null) {
                if (count == digit.getValue()) {
                    return null;
                }

                candidates[count] = entry.getKey();
                count++;
            }
        }

        if (count == digit.getValue()) {
            return candidates;
        }
        return null;
    }

    private final LinkedHashMap<Digit, SuperGroup> boxVerticals
            = new LinkedHashMap<>(Digit.values().length);
    private final LinkedHashMap<Coordinate, Group> cellVerticals
            = new LinkedHashMap<>(Digit.values().length ^ 2);
    private final LinkedHashMap<Digit, SuperGroup> columnVerticals
            = new LinkedHashMap<>(Digit.values().length);
    private boolean hasChanged;
    private final Grid puzzle;
    private final LinkedHashMap<Digit, Grid> registers
            = new LinkedHashMap<>(Digit.values().length);
    private final LinkedHashMap<Digit, SuperGroup> rowVerticals
            = new LinkedHashMap<>(Digit.values().length);

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

    private void candidateLines() {
        this.registers.forEach((Digit digit, Grid register) -> {
            register.forEachBox((Group box) -> {
                Digit[] candidates = candidates(box, TWO);
                if (candidates != null) {
                    Cell cell1 = box.getCell(candidates[0]);
                    Cell cell2 = box.getCell(candidates[1]);
                    Coordinate coordinate1 = cell1.getCoordinate();
                    Coordinate coordinate2 = cell2.getCoordinate();

                    Consumer<Cell> eliminate = (Cell cell) -> {
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
                    vertical[reg.getValue() - 1]
                            = this.registers.get(reg).getCell(
                                    Coordinate.valueOf(row, column));
                }
                this.cellVerticals.put(Coordinate.valueOf(row, column),
                        new Group(vertical));
            }
        }
    }

    private void singleCandidate() {
        this.registers.forEach((Digit i, Grid register) -> {
            register.forEachBox((Group box) -> {
                Digit[] candidates = candidates(box, ONE);
                if (candidates != null) {
                    this.puzzle.getCell(box.getCell(candidates[0])
                            .getCoordinate()).setContents(i);
                    this.hasChanged = true;
                }
            });
            register.forEachRow((Group row) -> {
                Digit[] candidates = candidates(row, ONE);
                if (candidates != null) {
                    this.puzzle.getCell(row.getCell(candidates[0])
                            .getCoordinate()).setContents(i);
                    this.hasChanged = true;
                }
            });
            register.forEachColumn((Group column) -> {
                Digit[] candidates = candidates(column, ONE);
                if (candidates != null) {
                    this.puzzle.getCell(column.getCell(candidates[0])
                            .getCoordinate()).setContents(i);
                    this.hasChanged = true;
                }
            });
        });

        this.cellVerticals.forEach((Coordinate coordinate, Group vertical) -> {
            Digit[] candidates = candidates(vertical, ONE);
            if (candidates != null) {
                this.puzzle.getCell(coordinate).setContents(candidates[0]);
                this.hasChanged = true;
            }
        });
    }

    private void updateRegisters() {
        this.puzzle.forEachCell((Cell cell) -> {
            if (cell.getContents() != null) {
                this.cellVerticals.get(cell.getCoordinate()).forEachCell(
                        (Cell c)
                        -> c.setContents(ONE));

                Grid reg = this.registers.get(cell.getContents());
                reg.forEachCellInBox(
                        cell.getCoordinate().toBoxCoordinate().getA(), (Cell c)
                        -> c.setContents(ONE));
                reg.forEachCellInRow(cell.getCoordinate().getA(), (Cell c)
                        -> c.setContents(ONE));
                reg.forEachCellInColumn(cell.getCoordinate().getB(), (Cell c)
                        -> c.setContents(ONE));
            }
        });
    }

}
