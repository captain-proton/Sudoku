package de.hindenbug.sudoku.solving;

import de.hindenbug.sudoku.model.Sudoku;
import de.hindenbug.sudoku.model.Field;

import java.util.Collection;

/**
 * A removal strategy is able to remove candidates that are set to a {@link Field} and eventually fix the field.
 */
public interface CandidateRemovalStrategy
{
    /**
     * Removes all possible candidates of fields used inside target sudoku.
     *
     * @return all fields that could be fixed by this strategy
     */
    Collection<Field> removeCandidates(Sudoku sudoku);
}
