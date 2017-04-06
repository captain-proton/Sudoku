package de.hindenbug.sudoku.solving;

import de.hindenbug.sudoku.model.Field;
import de.hindenbug.sudoku.model.Sudoku;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * A <code>Solver</code> uses by default a brute force attack on a {@link Sudoku} to solve it. Every possible value
 * on a {@link Field} is tried until a solution is found. To increase the speed of solution candidate removal
 * strategies can be used to reduce the number of candidates of a field.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Sudoku_solving_algorithms">Sudoku solving algorithms</a>
 */
public class Solver
{
    private static final Logger LOG = LoggerFactory.getLogger(Solver.class);
    private final List<CandidateRemovalStrategy> strategies;

    public Solver(CandidateRemovalStrategy...strategies)
    {
        this.strategies = Arrays.asList(strategies);
    }

    public void solve(Sudoku sudoku)
    {
        if (!sudoku.isValid())
            throw new IllegalArgumentException("sudoku is not valid " + sudoku);

        sudoku.buildCandidates();
        strategies.forEach(s -> s.removeCandidates(sudoku));
        LOG.debug(System.lineSeparator() + sudoku.toString());

        Field field = sudoku.getField(0, 0);

        // last field is used to prevent to much checks on sudoku.isSolved()
        Field lastField = sudoku.getField(sudoku.size() - 1, sudoku.size() - 1);
        boolean isSolvable = true;
        boolean isSolved = sudoku.isSolved();

        // for each step on successors and predecessors the count is increased
        long stepCount = 1;
        long time = System.currentTimeMillis();

        // try solving until it can not be solved
        while (!isSolved && isSolvable)
        {
            /*
            if current field contains candidates or is fixed set the
            next candidate and successor of the field.
             */
            if (field.containsCandidates() || field.isFix())
            {
                field.setNextCandidate();

                // if the sudoku is valid
                if (sudoku.isValid())
                {
                    // get the next possible successor
                    Field successor = sudoku.getSuccessor(field);
                    stepCount++;

                    /*
                    if a successor is available set it and continue with the next
                    step to solve the sudoku.
                     */
                    if (successor != null)
                    {
                        field = successor;
                    }
                    /*
                    if no successor is available the end of the field is reached
                    and the sudoku may be solved.
                     */
                    isSolved = field == lastField && sudoku.isSolved();
                }
            } else
            {
                /*
                if the field contains no candidates that can be tested,
                the solver has to go one step back and check the
                next possible candidate.
                 */
                field.reset();
                Field predecessor = sudoku.getPredecessor(field);
                stepCount++;

                /*
                if no predecessor is available, the solver is at the first field,
                the sudoku can not be solved.
                 */
                if (predecessor == null)
                {
                    LOG.info("sudoku not solveable");
                    isSolvable = false;
                } else
                {
                    field = predecessor;
                }
            }
        }

        String fmt = "sudoku solved with %d steps in %s";
        LOG.info(String.format(fmt, stepCount, fmtMillis(System.currentTimeMillis() - time)));
    }

    private String fmtMillis(long time)
    {
        long millis = time % 1000;
        time = time / 1000;
        long seconds = time % 60;
        time = time / 60;
        long minutes = time % 60;
        time = time / 60;
        long hours = time;
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }

}
