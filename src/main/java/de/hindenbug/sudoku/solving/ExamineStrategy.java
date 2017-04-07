package de.hindenbug.sudoku.solving;

import de.hindenbug.sudoku.model.Field;
import de.hindenbug.sudoku.model.Sudoku;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The examine strategy selects a specific amount of most used numbers. For each number this strategy searches for
 * blocks that may contain this number. Every field of a block that is not allowed to use the number is removed. If
 * only one field is left for the block, the field can be fixed to the number.
 */
public class ExamineStrategy implements CandidateRemovalStrategy
{
    private static final Logger LOG = LoggerFactory.getLogger(ExamineStrategy.class);

    private Sudoku sudoku;
    private Set<Field> fixedFields;
    private int fixedFieldCount;

    @Override
    public Collection<Field> removeCandidates(Sudoku sudoku)
    {
        this.sudoku = sudoku;
        this.fixedFields = new HashSet<>();

        removeCandidates();

        return fixedFields;
    }

    private void removeCandidates()
    {
        this.fixedFieldCount = 0;

        // get the top x used numbers
        int[] mostFixedNumbers = sudoku.getMostFixedNumbers(sudoku.size());

        // for each number
        for (int i = 0; i < mostFixedNumbers.length; i++)
        {
            int number = mostFixedNumbers[i];

            // for each block in the sudoku
            for (Set<Field> block : sudoku.getBlocks())
            {
                Field randomField = new ArrayList<>(block).get(0);

                // if the number is not inside the block
                if (!sudoku.isInBlock(number, randomField.getRow(), randomField.getColumn()))
                {
                    List<Field> fields = new ArrayList<>(block);

                    // remove all fields that are not allowed to use the number
                    fields.removeIf(field -> !field.containsCandidate(number)
                            || sudoku.isInRow(number, field.getRow())
                            || sudoku.isInColumn(number, field.getColumn()));

                    // if one field is left, fix the number to the field
                    if (fields.size() == 1)
                    {
                        Field field = fields.get(0);
                        field.fix(number);
                        LOG.debug("field " + field + " fixed");
                        fixedFields.add(field);
                        fixedFieldCount++;
                    }
                }
            }
        }
        if (fixedFieldCount > 0)
            removeCandidates();
    }
}
