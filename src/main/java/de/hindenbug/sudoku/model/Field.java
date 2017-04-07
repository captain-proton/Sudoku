package de.hindenbug.sudoku.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A <code>Field</code> is one value of a {@link Sudoku} that may contain a number that this field is bound to.
 * Otherwise the value of the field must be calculated through a solving process. Once a field is fixed to a
 * number it can not be changed again. To try different values during a solve of a sudoku the candidates of this
 * field can be saved and restored.
 *
 * @see Sudoku#buildCandidates()
 * @see #setNextCandidate()
 * @see #reset()
 */
public class Field
{
    private final int row;
    private final int column;
    private int number;
    private boolean isFix;

    private int idxCandidate;
    private List<Integer> candidates;

    public Field(int row, int column)
    {
        this.row = row;
        this.column = column;
        this.candidates = new ArrayList<>();
    }

    public int getNumber()
    {
        return number;
    }

    public boolean isFix()
    {
        return isFix;
    }

    public void addCandidates(int[] candidates)
    {
        for (int i = 0; i < candidates.length; i++)
        {
            this.candidates.add(candidates[i]);
        }
    }

    public void removeCandidate(Integer candidate)
    {
        candidates.remove(candidate);
    }

    public void clearCandidates()
    {
        candidates.clear();
    }

    public int getCandidateCount()
    {
        return candidates.size();
    }

    /**
     * Applies any value of this {@linkplain #candidates} and sets it to this
     * {@linkplain #number}. If this field has a fixed value nothing is set and <code>true</code> will be returned.
     *
     * @return <code>true</code> if a candidate could be set, <code>false</code> otherwise
     */
    public void setNextCandidate()
    {
        if (isFix())
            return;

        Integer nextCandidate = idxCandidate < candidates.size()
                                ? candidates.get(idxCandidate)
                                : null;
        idxCandidate++;
        if (nextCandidate != null)
        {
            this.number = nextCandidate;
        }
    }


    /**
     * Resets this field to its original number (0) and candidates. Fixed field can not be reset!
     */
    public void reset()
    {
        if (!isFix)
        {
            this.number = 0;
            this.idxCandidate = 0;
        }
    }

    /**
     * Fixes target number to this field. The field can not be changed afterwards.
     */
    public void fix(int number)
    {
        if (this.isFix)
            throw new IllegalArgumentException("field already fix");

        this.isFix = true;
        this.number = number;
        this.candidates.clear();
    }

    public int getNextCandidate()
    {
        return idxCandidate >= 0 && idxCandidate < candidates.size()
               ? candidates.get(idxCandidate)
               : 0;
    }

    public int getRow()
    {
        return row;
    }

    public int getColumn()
    {
        return column;
    }

    /**
     * Returns <code>true</code> if this empty field has candidates that can be tried as this {@linkplain #number}.
     *
     * @return <code>true</code> is candidates are left, <code>false</code> otherwise
     */
    public boolean containsCandidates()
    {
        return !isFix && idxCandidate < candidates.size();
    }

    public boolean containsCandidate(int number)
    {
        return !isFix() && candidates.contains(number);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("number", number)
                .append("row", row)
                .append("column", column)
                .append("isFix", isFix)
                .toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return row == field.row &&
                column == field.column &&
                getNumber() == field.getNumber() &&
                isFix() == field.isFix();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(row, column, getNumber(), isFix());
    }

}