package de.hindenbug.sudoku.model;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nils on 10.04.17.
 */
public class TestSudoku
{
    private Sudoku sudoku;

    @BeforeMethod
    public void setupSudoku()
    {
        sudoku = new Sudoku(new int[][]{
                {4, 0, 0, 0, 5, 0, 0, 0, 1},
                {7, 0, 1, 2, 0, 4, 8, 0, 0},
                {0, 2, 3, 8, 0, 0, 0, 0, 0},
                {9, 0, 7, 0, 8, 0, 5, 0, 2},
                {0, 3, 0, 0, 0, 0, 0, 4, 0},
                {2, 0, 6, 0, 4, 0, 9, 0, 3},
                {0, 0, 0, 0, 0, 6, 2, 1, 0},
                {0, 0, 9, 1, 0, 3, 4, 0, 8},
                {1, 0, 0, 0, 9, 0, 0, 0, 6}
        });
    }

    @Test
    public void testUnsolvedValid()
    {
        Assert.assertTrue(sudoku.isValid());
        Assert.assertFalse(sudoku.isSolved());
    }

    @Test
    public void testFixFieldCandidates()
    {
        sudoku.buildCandidates();
        Field f = sudoku.getField(0, 0);
        Assert.assertEquals(f.getCandidateCount(), 0);
    }

    @Test
    public void testCandidates()
    {
        sudoku.buildCandidates();
        testCandidates(0, 1, 6, 8, 9);
        testCandidates(1, 4, 3, 6);
        testCandidates(2, 8, 4, 5, 7, 9);
        testCandidates(4, 0, 5, 8);
        testCandidates(7, 4, 2, 7);
    }

    private void testCandidates(int row, int column, int...expected)
    {

        Field f = sudoku.getField(row, column);
        List<Integer> candidates = new ArrayList<>();
        Arrays.stream(expected).forEach(i -> candidates.add(i));

        Assert.assertEquals(f.getCandidateCount(), candidates.size());
        for (int i = 0; i < candidates.size(); i++)
        {
            int candidate = f.getCandidate();
            Assert.assertTrue(candidates.contains(candidate));
            f.setNextCandidate();
        }
    }
}
