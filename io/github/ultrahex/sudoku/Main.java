/*
 * Copyright (C) 2015-2016, 2019 Matthew William Noel
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matthew William Noel
 */
public class Main {

  private static final Logger logger = Logger.getGlobal();

  public static void main(String[] args) {
    if (args.length != 1) {
      logger.log(Level.INFO,
          "usage: Java Sudoku-Solver <filename>");
      System.exit(0);
    }

    File file = new File(args[0]);
    if (!file.exists()) {
      logger.log(Level.SEVERE, "{0} does not exist", args[0]);
      System.exit(1);
    }
    if (file.isDirectory()) {
      logger.log(Level.SEVERE, "{0} is a directory", args[0]);
      System.exit(1);
    }
    if (!file.isFile()) {
      logger.log(Level.SEVERE, "{0} is invalid", args[0]);
      System.exit(1);
    }
    if (!file.canRead()) {
      logger.log(Level.SEVERE, "{0} cannot be read", args[0]);
      System.exit(1);
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      Object[] lines = reader.lines().toArray();
      Digit[][] puzzle = new Digit[lines.length][lines.length];
      Arrays.parallelSetAll(puzzle, (i) -> {
        char[] tokens = ((String) lines[i]).toCharArray();
        Digit[] row = new Digit[lines.length];
        Arrays.parallelSetAll(row,
            (j) -> {
              char c = tokens[j];
              if (c >= '1' && c <= '9') {
                return Digit.valueOf(Character.getNumericValue(c));
              } else {
                return null;
              }
            });
        return row;
      });

      Solver solver = new Solver(new Grid(puzzle));
      solver.solve();
      System.out.print(solver.toString());
    } catch (IOException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
