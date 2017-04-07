package de.hindenbug.sudoku;

import de.hindenbug.sudoku.model.Sudoku;
import de.hindenbug.sudoku.solving.ExamineStrategy;
import de.hindenbug.sudoku.solving.NakedOneStrategy;
import de.hindenbug.sudoku.solving.Solver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit test for simple App.
 */
public class AppTest
{
    private static final Logger LOG = LoggerFactory.getLogger(AppTest.class);

    private Sudoku simpleSudoku;
    private Sudoku mediumSudoku;
    private Sudoku hardSudoku;
    private Sudoku evilSudoku;

    @BeforeMethod
    public void buildSimpleSudoku()
    {

        simpleSudoku = new Sudoku(new int[][]{
                {7, 8, 0, 0, 0, 6, 0, 0, 0},
                {1, 4, 9, 0, 0, 7, 3, 0, 5},
                {2, 3, 0, 0, 0, 1, 0, 0, 9},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {9, 1, 2, 0, 0, 3, 8, 4, 0},
                {0, 0, 4, 0, 8, 2, 0, 0, 6},
                {0, 5, 7, 0, 6, 0, 9, 1, 0},
                {0, 0, 0, 0, 3, 0, 2, 5, 0},
                {4, 0, 0, 0, 0, 5, 6, 0, 0}
        });
    }

    @BeforeMethod
    public void buildMediumSudoku()
    {
        mediumSudoku = new Sudoku(new int[][]{
                {5, 3, 0, 0, 7, 0, 0, 0, 0},
                {6, 0, 0, 1, 9, 5, 0, 0, 0},
                {0, 9, 8, 0, 0, 0, 0, 6, 0},
                {8, 0, 0, 0, 6, 0, 0, 0, 3},
                {4, 0, 0, 8, 0, 3, 0, 0, 1},
                {7, 0, 0, 0, 2, 0, 0, 0, 6},
                {0, 6, 0, 0, 0, 0, 2, 8, 0},
                {0, 0, 0, 4, 1, 9, 0, 0, 5},
                {0, 0, 0, 0, 8, 0, 0, 7, 0},
        });
    }

    @BeforeMethod
    public void buildHardSudoku()
    {
        hardSudoku = new Sudoku(new int[][]{
                {0, 3, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 9, 5, 0, 0, 0},
                {0, 0, 8, 0, 0, 0, 0, 6, 0},
                {8, 0, 0, 0, 6, 0, 0, 0, 0},
                {4, 0, 0, 8, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 2, 0, 0, 0, 0},
                {0, 6, 0, 0, 0, 0, 2, 8, 0},
                {0, 0, 0, 4, 1, 9, 0, 0, 5},
                {0, 0, 0, 0, 0, 0, 0, 7, 0},
        });
    }

    @BeforeMethod
    public void buildEvilSudoku()
    {
        evilSudoku = new Sudoku(new int[][]{
                {6, 0, 0, 2, 0, 0, 0, 0, 0},
                {0, 5, 0, 0, 6, 0, 1, 0, 0},
                {0, 9, 0, 0, 0, 4, 0, 0, 0},
                {0, 0, 0, 3, 0, 0, 9, 0, 8},
                {9, 0, 2, 0, 7, 0, 3, 0, 4},
                {7, 0, 4, 0, 0, 8, 0, 0, 0},
                {0, 0, 0, 8, 0, 0, 0, 6, 0},
                {0, 0, 1, 0, 3, 0, 0, 4, 0},
                {0, 0, 0, 0, 0, 9, 0, 0, 5},
        });
    }

    @Test
    public void testSudoku()
    {
        Sudoku sudoku = new Sudoku();
        sudoku.fix(5, 0, 0)
                .fix(3, 0, 1)
                .fix(4, 0, 2)
                .fix(6, 0, 3)
                .fix(7, 0, 4)
                .fix(8, 0, 5)
                .fix(9, 0, 6)
                .fix(1, 0, 7)
                .fix(2, 0, 8)
                .fix(6, 1, 0)
                .fix(7, 1, 1)
                .fix(2, 1, 2)
                .fix(1, 1, 3)
                .fix(9, 1, 4)
                .fix(5, 1, 5)
                .fix(3, 1, 6)
                .fix(4, 1, 7)
                .fix(8, 1, 8)
                .fix(1, 2, 0)
                .fix(9, 2, 1)
                .fix(8, 2, 2)
                .fix(3, 2, 3)
                .fix(4, 2, 4)
                .fix(2, 2, 5)
                .fix(5, 2, 6)
                .fix(6, 2, 7)
                .fix(7, 2, 8)
                .fix(8, 3, 0)
                .fix(5, 3, 1)
                .fix(9, 3, 2)
                .fix(7, 3, 3)
                .fix(6, 3, 4)
                .fix(1, 3, 5)
                .fix(4, 3, 6)
                .fix(2, 3, 7)
                .fix(3, 3, 8)
                .fix(4, 4, 0)
                .fix(2, 4, 1)
                .fix(6, 4, 2)
                .fix(8, 4, 3)
                .fix(5, 4, 4)
                .fix(3, 4, 5)
                .fix(7, 4, 6)
                .fix(9, 4, 7)
                .fix(1, 4, 8)
                .fix(7, 5, 0)
                .fix(1, 5, 1)
                .fix(3, 5, 2)
                .fix(9, 5, 3)
                .fix(2, 5, 4)
                .fix(4, 5, 5)
                .fix(8, 5, 6)
                .fix(5, 5, 7)
                .fix(6, 5, 8)
                .fix(9, 6, 0)
                .fix(6, 6, 1)
                .fix(1, 6, 2)
                .fix(5, 6, 3)
                .fix(3, 6, 4)
                .fix(7, 6, 5)
                .fix(2, 6, 6)
                .fix(8, 6, 7)
                .fix(4, 6, 8)
                .fix(2, 7, 0)
                .fix(8, 7, 1)
                .fix(7, 7, 2)
                .fix(4, 7, 3)
                .fix(1, 7, 4)
                .fix(9, 7, 5)
                .fix(6, 7, 6)
                .fix(3, 7, 7)
                .fix(5, 7, 8)
                .fix(3, 8, 0)
                .fix(4, 8, 1)
                .fix(5, 8, 2)
                .fix(2, 8, 3)
                .fix(8, 8, 4)
                .fix(6, 8, 5)
                .fix(1, 8, 6)
                .fix(7, 8, 7)
                .fix(9, 8, 8)
        ;
        Assert.assertTrue(sudoku.isValid());
    }

    @Test(dependsOnMethods = {"testSudoku"})
    public void testSimpleSudokuSolver()
    {
        Solver solver = new Solver();
        solver.solve(simpleSudoku);
        LOG.info(System.lineSeparator() + simpleSudoku.toString());
        Assert.assertTrue(simpleSudoku.isSolved());
    }

    @Test(dependsOnMethods = {"testSudoku"})
    public void testSimpleSudokuSolverWithStrategies()
    {
        Solver solver = new Solver(new ExamineStrategy(), new NakedOneStrategy());
        solver.solve(simpleSudoku);
        LOG.info(System.lineSeparator() + simpleSudoku.toString());
        Assert.assertTrue(simpleSudoku.isSolved());
    }

    @Test(dependsOnMethods = {"testSudoku"})
    public void testMediumSudokuSolverrWithStrategies()
    {
        Solver solver = new Solver(new ExamineStrategy(), new NakedOneStrategy());
        solver.solve(mediumSudoku);
        LOG.info(System.lineSeparator() + mediumSudoku.toString());
        Assert.assertTrue(mediumSudoku.isSolved());
    }

    @Test(dependsOnMethods = {"testSudoku"})
    public void testHardSudokuSolverrWithStrategies()
    {
        Solver solver = new Solver(new NakedOneStrategy(), new ExamineStrategy());
        solver.solve(hardSudoku);
        LOG.info(System.lineSeparator() + hardSudoku.toString());
        Assert.assertTrue(hardSudoku.isSolved());
    }

    @Test(dependsOnMethods = {"testSudoku"})
    public void testEvilSudokuSolverrWithStrategies()
    {
        Solver solver = new Solver(new NakedOneStrategy(), new ExamineStrategy());
        solver.solve(evilSudoku);
        LOG.info(System.lineSeparator() + evilSudoku.toString());
        Assert.assertTrue(evilSudoku.isSolved());
    }

    @Test(dependsOnMethods = {"testSudoku"})
    public void testEmptySudoku()
    {
        Sudoku sudoku = new Sudoku();
        Solver solver = new Solver(new NakedOneStrategy());
        solver.solve(sudoku);
        LOG.info(System.lineSeparator() + sudoku.toString());
        Assert.assertTrue(sudoku.isSolved());
    }
}
