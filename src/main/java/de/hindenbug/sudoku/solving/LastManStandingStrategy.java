package de.hindenbug.sudoku.solving;

import de.hindenbug.sudoku.model.Field;
import de.hindenbug.sudoku.model.Sudoku;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * The last man stands strategy fixes field where all other fields of row and column contain all other fields and
 * only one candidate is left.
 */
public class LastManStandingStrategy implements CandidateRemovalStrategy
{
    private static final Logger LOG = LoggerFactory.getLogger(LastManStandingStrategy.class);

    private Sudoku sudoku;
    private int[] allNumbers;
    private Set<Field> fixedFields;

    @Override
    public Collection<Field> removeCandidates(Sudoku sudoku)
    {
        this.sudoku = sudoku;
        this.fixedFields = new HashSet<>();
        this.allNumbers = IntStream.rangeClosed(1, sudoku.size()).toArray();

        sudoku.forEach(this::removeCandidates);

        return fixedFields;
    }

    private void removeCandidates(Field field)
    {
        if (field.isFix())
            return;

        // build all numbers that are present in row and column
        Set<Integer> numbers = new HashSet<>();
        numbers.addAll(sudoku.getNumbersInRow(field.getRow()));
        numbers.addAll(sudoku.getNumbersInColumn(field.getColumn()));

        // if one number is missing
        if (numbers.size() == sudoku.size() - 1)
        {
            // iterate over all available numbers
            for (int i = 0; i < allNumbers.length && !field.isFix(); i++)
            {
                // and fix the number, that is missing
                if (!numbers.contains(allNumbers[i]))
                {
                    field.fix(allNumbers[i]);
                    LOG.debug("field " + field + " fixed");
                    fixedFields.add(field);
                }
            }
        }
    }
}
