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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import sudoku.Digit;
import sudoku.Grid;
import sudoku.Solver;

/**
 *
 * @author Matthew William Noel
 */
public class Main {

  public static void main(String[] args) {
    if (args.length != 1) {
      Logger.getGlobal().log(Level.SEVERE,
          "usage: Java Sudoku-Solver <filename>");
      System.exit(1);
    }

    File file = new File(args[0]);
    if (!file.exists()) {
      Logger.getGlobal().log(Level.SEVERE, "{0} does not exist", args[0]);
      System.exit(1);
    }
    if (file.isDirectory()) {
      Logger.getGlobal().log(Level.SEVERE, "{0} is a directory", args[0]);
      System.exit(1);
    }
    if (!file.isFile()) {
      Logger.getGlobal().log(Level.SEVERE, "{0} is invalid", args[0]);
      System.exit(1);
    }
    if (!file.canRead()) {
      Logger.getGlobal().log(Level.SEVERE, "{0} cannot be read", args[0]);
      System.exit(1);
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      Object[] lines = reader.lines().toArray();
      Digit[][] puzzle = new Digit[lines.length][lines.length];
      Arrays.parallelSetAll(puzzle, (i) -> {
        String[] tokens = ((String) lines[i]).split(",");
        Digit[] row = new Digit[lines.length];
        Arrays.parallelSetAll(row,
            (j) -> {
              String str = tokens[j];
              if (str.matches("^\\d+$")) {
                return Digit.valueOf(Integer.valueOf(str));
              } else {
                return null;
              }
            });
        return row;
      });

      Solver solver = new Solver(new Grid(puzzle));
      solver.solve();
      Logger.getGlobal().log(Level.INFO, "\n{0}", solver.toString());
    } catch (FileNotFoundException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
