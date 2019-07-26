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

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * @author Matthew William Noel
 */
public class SuperGroup {

  private final LinkedHashMap<Digit, Group> groups =
      new LinkedHashMap<>(Digit.values().length);

  public SuperGroup(Group[] groups) {
    if (groups.length != Digit.values().length) {
      throw new IllegalArgumentException(
          "SuperGroups must contain exactly " + Digit.values().length
              + " Groups");
    }
    for (int i = 0; i < groups.length; i++) {
      this.groups.put(Digit.values()[i], groups[i]);
    }
  }

  @Override
  @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
  public boolean equals(Object obj) {
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    final SuperGroup other = (SuperGroup) obj;
    return Objects.equals(this.groups, other.groups);
  }

  public void forEach(BiConsumer<? super Digit, ? super Group> action) {
    this.groups.forEach(action);
  }

  public Group getGroup(Digit digit) {
    return this.groups.get(digit);
  }

  @SuppressWarnings("unchecked")
  public LinkedHashMap<Digit, Group> getGroups() {
    return (LinkedHashMap<Digit, Group>) this.groups.clone();
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + Objects.hashCode(this.groups);
    return hash;
  }
}
