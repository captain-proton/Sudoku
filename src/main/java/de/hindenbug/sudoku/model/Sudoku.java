package de.hindenbug.sudoku.model;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A <code>Sudoku</code>  is a logic-based, combinatorial number-placement puzzle. The objective is to fill
 * a 9×9 grid with digits so that each column, each row, and each of the nine 3×3 subgrids that compose the
 * grid (also called "boxes", "blocks", "regions", or "subsquares") contains all of the digits from 1 to 9.
 * The puzzle setter provides a partially completed grid, which for a well-posed puzzle has a unique solution.
 */
public class Sudoku implements Iterable<Field>
{
    private static final int DEFAULT_SIZE = 9;

    /**
     * returns <code>true</code> if count to test is less or equal to one
     */
    private static final Predicate<Integer> VALID_FOUND_COUNT = count -> count <= 1;
    private static final String ROW_NUMBER_DIVIDER = " | ";
    private static final Function<Integer, String> NUMBER_FMT = c -> "%" + c + "s";

    /**
     * Contains the fields that this <code>Sudoku</code> consists of.
     */
    private Field[][] fields;

    /**
     * Create a new sudoku with the default size {@linkplain #DEFAULT_SIZE}.
     */
    public Sudoku()
    {
        initFields(DEFAULT_SIZE);
    }

    private void initFields(int size)
    {
        fields = new Field[size][size];
        for (int row = 0; row < size; row++)
        {
            fields[row] = new Field[size];
            for (int col = 0; col < size; col++)
            {
                Field f = new Field(row, col);
                fields[row][col] = f;
            }
        }
    }

    /**
     * Binds a {@link Field} to a specific number, so it can not be changed.
     *
     * @param number contains the number, that the field should contain
     * @param row    row of the field
     * @param col    column of the field
     * @return this sudoku
     * @see Field#fix(int)
     */
    public Sudoku fix(int number, int row, int col)
    {
        fields[row][col].fix(number);
        return this;
    }

    /**
     * A sudoku is valid if each number that this sudoku uses, typically 1 - 9, is unique for its row, column and
     * block. Other values set inside the field are ignored.
     *
     * @return <code>true</code> if this sudoku is valid, <code>false</code> otherwise
     */
    public boolean isValid()
    {
        for (int row = 0; row < fields.length; row++)
        {
            for (int col = 0; col < fields[row].length; col++)
            {
                int number = fields[row][col].getNumber();
                boolean isTarget = number > 0;
                boolean isUnique = isSingleInRow(number, row)
                        && isSingleInColumn(number, col)
                        && isSingleInBlock(number, row, col);
                if (isTarget && !isUnique)
                    return false;
            }
        }
        return true;
    }

    /**
     * Returns <code>true</code> if this sudoku is valid and all fields are set to a valid value.
     *
     * @return <code>true</code> if this sudoku is solved, <code>false</code> otherwise
     * @see #isValid()
     */
    public boolean isSolved()
    {
        int zeros = 0;
        for (Field f : this)
            zeros += f.getNumber() == 0
                     ? 1
                     : 0;
        return isValid() && zeros == 0;
    }

    /**
     * Builds and sets all possible numbers of all fields that may be used to solve this sudoku. This is usually used
     * after some fields are set to specific values.
     *
     * @see #fix(int, int, int)
     */
    public void buildCandidates()
    {
        List<Set<Integer>> rows = new ArrayList<>();
        List<Set<Integer>> columns = new ArrayList<>();
        List<Set<Integer>> blocks = new ArrayList<>();
        int blockSize = getBlockSize();

        for (int i = 0; i < fields.length; i++)
        {
            rows.add(getNumbersInRow(i));
            columns.add(getNumbersInColumn(i));
        }
        for (int i = 0; i < fields.length; i += blockSize)
        {
            for (int j = 0; j < fields.length; j += blockSize)
            {
                blocks.add(getNumbersInBlock(i, j));
            }
        }

        for (int row = 0; row < fields.length; row++)
        {
            Set<Integer> rowNumbers = rows.get(row);
            for (int col = 0; col < fields[row].length; col++)
            {
                Field field = fields[row][col];
                if (!field.isFix())
                {
                    Set<Integer> columnNumbers = columns.get(col);

                    int blockNum = (row / blockSize) * blockSize + col / blockSize;
                    Set<Integer> blockNumbers = blocks.get(blockNum);

                    int[] candidates = IntStream.rangeClosed(1, fields.length)
                            .filter(i -> !rowNumbers.contains(i))
                            .filter(i -> !columnNumbers.contains(i))
                            .filter(i -> !blockNumbers.contains(i))
                            .toArray();
                    field.addCandidates(candidates);
                    field.saveCandidates();
                }

            }
        }
    }

    public Field getField(int row, int column)
    {
        if (row < 0
                || column < 0
                || row >= fields.length
                || column >= fields.length)
            throw new IllegalArgumentException("row " + row + " and/or column " +
                    column + " outside if field range " + fields.length);
        return fields[row][column];
    }

    /**
     * Returns the next field from left to right and top to bottom of target field, that is not fixed.
     *
     * @param field {@link Field} which successor should be returned
     * @return the successor of target field or <code>null</code> if it does not exist
     */
    public Field getSuccessor(Field field)
    {
        for (int row = 0; row < fields.length; row++)
        {
            for (int col = 0; col < fields.length; col++)
            {
                if (fields[row][col] == field)
                {
                    /*
                    return the first field of the next row if iteration is
                    at the end of the row, the field of the next column
                    otherwise.
                     */
                    Field successor = col == fields.length - 1 && row < fields.length - 1
                                      ? fields[row + 1][0]
                                      : col == fields.length - 1 && row == fields.length - 1
                                        ? null
                                        : fields[row][col + 1];
                    return successor != null && !successor.isFix()
                           ? successor
                           : successor != null && successor.isFix()
                             ? getSuccessor(successor)
                             : null;
                }
            }
        }
        return null;
    }

    /**
     * Returns the previous field from right to left and bottom to top of target field, that is not fixed.
     *
     * @param field {@link Field} which predecessor should be returned
     * @return the predecessor of target field or <code>null</code> if it does not exist
     */
    public Field getPredecessor(Field field)
    {
        for (int row = 0; row < fields.length; row++)
        {
            for (int col = 0; col < fields.length; col++)
            {
                if (fields[row][col] == field)
                {
                    /*
                    return the last field of the previous row if iteration is
                    at the start of the row, the field of the previous column
                    otherwise.
                     */
                    Field predecessor = col == 0 && row > 0
                                        ? fields[row - 1][fields.length - 1]
                                        : col == 0 && row == 0
                                          ? null
                                          : fields[row][col - 1];

                    return predecessor != null && !predecessor.isFix()
                           ? predecessor
                           : predecessor != null && predecessor.isFix()
                             ? getPredecessor(predecessor)
                             : null;
                }
            }
        }
        return null;
    }

    public Set<Field> getRow(int row)
    {
        Field[] fields = this.fields[row];
        Set<Field> result = new HashSet<>(fields.length);
        for (int column = 0; column < fields.length; column++)
        {
            result.add(fields[column]);
        }
        return result;
    }

    public Set<Field> getColumn(int column)
    {
        Set<Field> result = new HashSet<>(fields.length);
        for (int row = 0; row < fields.length; row++)
        {
            Field field = fields[row][column];
            result.add(field);
        }
        return result;
    }

    /**
     * Returns all fields, that are in target row and column. A block has the size of square root in width and length
     * of this sudokus {@linkplain #size()}
     *
     * @param row    row of the block
     * @param column column of the block
     * @return the set of fields
     */
    public Set<Field> getBlock(int row, int column)
    {
        int blockSize = (int) Math.sqrt(fields.length);
        Set<Field> block = new HashSet<>();

        int rowStart = row - row % blockSize;
        int colStart = column - column % blockSize;

        int rowEnd = rowStart + blockSize;
        int colEnd = colStart + blockSize;

        for (int i = rowStart; i < rowEnd; i++)
        {
            for (int j = colStart; j < colEnd; j++)
            {
                block.add(fields[i][j]);
            }
        }
        return block;
    }

    private Set<Integer> getNumbersInRow(int row)
    {
        Set<Field> fields = getRow(row);
        Set<Integer> numbers = new HashSet<>(fields.size());
        for (Field field : fields)
            numbers.add(field.getNumber());
        return numbers;
    }

    private Set<Integer> getNumbersInColumn(int column)
    {
        Set<Field> fields = getColumn(column);
        Set<Integer> numbers = new HashSet<>(fields.size());
        for (Field field : fields)
            numbers.add(field.getNumber());
        return numbers;
    }

    private Set<Integer> getNumbersInBlock(int row, int col)
    {
        Set<Field> fields = getBlock(row, col);
        Set<Integer> numbers = new HashSet<>(fields.size());
        for (Field field : fields)
            numbers.add(field.getNumber());
        return numbers;
    }

    private boolean isSingleInRow(int number, int row)
    {
        int count = 0;
        Field[] fields = this.fields[row];
        for (int i = 0; i < fields.length; i++)
        {
            count += fields[i].getNumber() == number
                     ? 1
                     : 0;
        }
        return VALID_FOUND_COUNT.test(count);
    }

    private boolean isSingleInColumn(int number, int col)
    {
        int count = 0;
        for (int row = 0; row < fields.length; row++)
        {
            Field field = fields[row][col];
            count += field.getNumber() == number
                     ? 1
                     : 0;
        }
        return VALID_FOUND_COUNT.test(count);
    }

    private boolean isSingleInBlock(int number, int row, int col)
    {
        int blockSize = getBlockSize();

        int rowStart = row - row % blockSize;
        int colStart = col - col % blockSize;

        int rowEnd = rowStart + blockSize;
        int colEnd = colStart + blockSize;

        int foundCount = 0;
        for (int i = rowStart; i < rowEnd && VALID_FOUND_COUNT.test(foundCount); i++)
        {
            for (int j = colStart; j < colEnd && VALID_FOUND_COUNT.test(foundCount); j++)
            {
                int currentNumber = fields[i][j].getNumber();
                if (currentNumber > 0
                        && currentNumber == number)
                    foundCount++;
            }
        }
        return VALID_FOUND_COUNT.test(foundCount);
    }

    private int getBlockSize()
    {
        return (int) Math.sqrt(fields.length);
    }

    public int size()
    {
        return fields.length;
    }

    /**
     * <p>Returns this sudoku in the form:</p>
     * <pre>
     * ---------------------
     * 7 8 5 | 3 9 6 | 4 2 1
     * 1 4 9 | 8 2 7 | 3 6 5
     * 2 3 6 | 5 4 1 | 7 8 9
     * ---------------------
     * 5 6 8 | 4 7 9 | 1 3 2
     * 9 1 2 | 6 5 3 | 8 4 7
     * 3 7 4 | 1 8 2 | 5 9 6
     * ---------------------
     * 8 5 7 | 2 6 4 | 9 1 3
     * 6 9 1 | 7 3 8 | 2 5 4
     * 4 2 3 | 9 1 5 | 6 7 8
     * ---------------------
     * </pre>
     *
     * @return
     */
    @Override
    public String toString()
    {
        int maxDigits = Integer.toString(size()).length();
        int blockSize = getBlockSize();
        StringBuilder builder = new StringBuilder();
        String divider = ipsum('-', size() * maxDigits
                + ((size() / blockSize) - 1) * ROW_NUMBER_DIVIDER.length()
                + (size() - blockSize));

        for (int row = 0; row < size(); row++)
        {
            if (row % blockSize == 0)
            {
                builder.append(divider);
                builder.append(System.lineSeparator());
            }
            builder.append(rowToString(fields[row], maxDigits));
            builder.append(System.lineSeparator());
        }
        builder.append(divider);
        return builder.toString();
    }

    private String rowToString(Field[] numbers, Integer digits)
    {
        int blockSize = getBlockSize();
        List<String> numberStrings = Arrays.stream(numbers)
                .map(f -> Integer.toString(f.getNumber()))
                .map(n -> String.format(NUMBER_FMT.apply(digits), n))
                .collect(Collectors.toList());
        List<String> blockNumbers = new ArrayList<>();
        for (int i = 0; i < size(); i += blockSize)
        {
            blockNumbers.add(String.join(" ", numberStrings.subList(i, i + blockSize)));
        }
        return String.join(ROW_NUMBER_DIVIDER, blockNumbers);
    }

    private String ipsum(char c, int times)
    {
        char[] ipsum = new char[times];
        for (int i = 0; i < times; i++)
        {
            ipsum[i] = c;
        }
        return new String(ipsum);
    }

    @Override
    public Iterator<Field> iterator()
    {
        return new Iterator<Field>()
        {
            private int row = 0;
            private int col = 0;

            @Override
            public boolean hasNext()
            {
                return row < size() && col < size();
            }

            @Override
            public Field next()
            {
                Field result = fields[row][col];
                if (col == size() - 1)
                {
                    col = 0;
                    row++;
                } else
                {
                    col++;
                }
                return result;
            }
        };
    }
}
