package de.hindenbug.sudoku.solving;

import de.hindenbug.sudoku.model.Field;
import de.hindenbug.sudoku.model.Sudoku;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>SingleCandidateStrategy</code> checks if a candidate of a {@link Field} is the only one left and fixes
 * the field to the value. After the fix all fields of the row, column and block are checked if the number is used
 * as a candidate and removes them. If again one candidate is left the process is
 */
public class SingleCandidateStrategy implements CandidateRemovalStrategy
{
    private static final Logger LOG = LoggerFactory.getLogger(SingleCandidateStrategy.class);

    private Sudoku sudoku;

    @Override
    public void removeCandidates(Sudoku sudoku)
    {
        this.sudoku = sudoku;
        this.sudoku.forEach(this::tryFixField);
    }

    /**
     * Tries to fix target field to its candidate. The candidate can only be set if it is the only one
     * left and the field is not fixed.
     */
    private void tryFixField(Field field)
    {
        /*
        if the field is not fixed and only one candidate is left fix the number
        and start the process again.
         */
        if (!field.isFix() && field.getCandidateCount() == 1)
        {
            int candidate = field.getNextCandidate();
            field.fix(candidate);
            LOG.debug("field " + field + " fixed");

            removeCandidatesFromRow(field.getRow(), candidate);
            removeCandidatesFromColumn(field.getColumn(), candidate);
            removeCandidatesFromBlock(field.getRow(), field.getColumn(), candidate);
        }
    }

    private void removeCandidatesFromRow(int row, int candidate)
    {
        sudoku.getRow(row).forEach(f -> removeCandidate(f, candidate));
    }

    private void removeCandidatesFromColumn(int column, int candidate)
    {
        sudoku.getColumn(column).forEach(f -> removeCandidate(f, candidate));
    }

    private void removeCandidatesFromBlock(int row, int column, int candidate)
    {
        sudoku.getBlock(row, column).forEach(f -> removeCandidate(f, candidate));
    }

    private void removeCandidate(Field field, int candidate)
    {
        field.removeCandidate(candidate);

        tryFixField(field);
    }
}
