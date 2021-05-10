//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package de.tum.bgu.msm.common.datafile;

import de.tum.bgu.msm.common.util.IndexSort;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

public class TableDataSet implements DataTypes, Serializable {
    protected static transient Logger logger = Logger.getLogger("com.pb.common.datafile");
    private boolean columnLabelsPresent = false;
    private boolean dirty = false;
    private ArrayList columnLabels = new ArrayList();
    private ArrayList columnData = new ArrayList();
    private int[] columnIndex = null;
    private int[] columnType = null;
    private boolean[] indexColumns = null;
    private int nRows;
    private int nCols;
    private DecimalFormat valueFormat = new DecimalFormat("#.#");
    private Set changeListeners = null;
    private String name;
    private ArrayList myWatchers = null;
    public boolean use1sAnd0sForTrueFalse = false;
    private Map<String, Integer> stringIndex = null;
    private int stringIndexColumn = -1;
    private boolean stringIndexDirty;

    public void addChangeListener(TableDataSet.ChangeListener letMeKnow) {
        if (this.changeListeners == null) {
            this.changeListeners = new HashSet();
        }

        this.changeListeners.add(letMeKnow);
    }

    void fireIndexValuesChanged() {
        if (this.changeListeners != null) {
            Iterator it = this.changeListeners.iterator();

            while(it.hasNext()) {
                ((TableDataSet.ChangeListener)it.next()).indexValuesChanged();
            }
        }

    }

    public TableDataSet() {
    }

    public boolean isColumnLabelsPresent() {
        return this.columnLabelsPresent;
    }

    public int getRowCount() {
        return this.nRows;
    }

    public int getColumnCount() {
        return this.nCols;
    }

    public String[] getColumnLabels() {
        String[] a = new String[1];
        return (String[])((String[])this.columnLabels.toArray(a));
    }

    public int[] getColumnType() {
        return this.columnType;
    }

    public void setColumnLabels(String[] titles) {
        this.columnLabels = new ArrayList(titles.length);

        for(int i = 0; i < titles.length; ++i) {
            this.columnLabels.add(titles[i]);
        }

        this.columnLabelsPresent = true;
        this.fireIndexValuesChanged();
        this.setDirty(true);
    }

    public float getColumnTotal(int column) {
        this.checkColumnNumber(column, 3);
        --column;
        float[] f = (float[])((float[])this.columnData.get(column));
        float total = 0.0F;

        for(int i = 0; i < f.length; ++i) {
            total += f[i];
        }

        return total;
    }

    public int[] getColumnAsInt(String columnName) {
        return this.getColumnAsInt(this.getColumnPosition(columnName), 0);
    }

    public int[] getColumnAsInt(int column) {
        return this.getColumnAsInt(column, 0);
    }

    public int[] getColumnAsInt(int column, int startPosition) {
        this.checkColumnNumber(column, 3);
        --column;
        float[] f = (float[])((float[])this.columnData.get(column));
        int[] values = new int[this.nRows + startPosition];

        for(int i = 0; i < this.nRows; ++i) {
            values[i + startPosition] = (int)f[i];
        }

        return values;
    }

    public long[] getColumnAsLong(int column) {
        --column;
        int startPosition = 0;
        long[] values = new long[this.nRows + startPosition];
        int i;
        if (this.columnType[column] == 3) {
            float[] f = (float[])((float[])this.columnData.get(column));

            for(i = 0; i < this.nRows; ++i) {
                values[i + startPosition] = (long)((int)f[i]);
            }
        } else {
            if (this.columnType[column] != 2) {
                throw new RuntimeException("Can't convert column " + this.columnLabels.get(column) + " to long");
            }

            String[] s = (String[])((String[])this.columnData.get(column));

            for(i = 0; i < this.nRows; ++i) {
                values[i + startPosition] = Long.valueOf(s[i]);
            }
        }

        return values;
    }

    public float[] getColumnAsFloat(int column) {
        this.checkColumnNumber(column, 3);
        --column;
        int startPosition = 0;
        float[] f = (float[])((float[])this.columnData.get(column));
        float[] values = new float[this.nRows + startPosition];

        for(int i = 0; i < this.nRows; ++i) {
            values[i + startPosition] = f[i];
        }

        return values;
    }

    public float[] getColumnAsFloat(String columnName) {
        int columnPosition = this.getColumnPosition(columnName);
        return this.getColumnAsFloat(columnPosition);
    }

    public double[] getColumnAsDouble(int column) {
        this.checkColumnNumber(column, 3);
        --column;
        int startPosition = 0;
        float[] f = (float[])((float[])this.columnData.get(column));
        double[] values = new double[this.nRows + startPosition];

        for(int i = 0; i < this.nRows; ++i) {
            values[i + startPosition] = (double)f[i];
        }

        return values;
    }

    public double[] getColumnAsDouble(String columnName) {
        int columnPosition = this.getColumnPosition(columnName);
        return this.getColumnAsDouble(columnPosition);
    }

    public double[] getColumnAsDoubleFromDouble(String columnName) {
        int columnPosition = this.getColumnPosition(columnName);
        return this.getColumnAsDoubleFromDouble(columnPosition);
    }

    public double[] getColumnAsDoubleFromDouble(int column) {
        this.checkColumnNumber(column, 4);
        --column;
        return (double[])((double[])this.columnData.get(column));
    }

    public boolean[] getColumnAsBoolean(int column) {
        this.checkColumnNumber(column, 2);
        --column;
        int startPosition = 0;
        String[] f = (String[])((String[])this.columnData.get(column));
        boolean[] values = new boolean[this.nRows + startPosition];

        for(int i = 0; i < this.nRows; ++i) {
            values[i + startPosition] = f[i].equalsIgnoreCase("true");
        }

        return values;
    }

    public boolean[] getColumnAsBoolean(String columnName) {
        int columnPosition = this.getColumnPosition(columnName);
        return this.getColumnAsBoolean(columnPosition);
    }

    public String[] getColumnAsString(int column) {
        this.checkColumnNumber(column, 2);
        --column;
        int startPosition = 0;
        String[] s = (String[])((String[])this.columnData.get(column));
        String[] values = new String[s.length + startPosition];

        for(int i = 0; i < s.length; ++i) {
            values[i + startPosition] = s[i];
        }

        return values;
    }

    public String[] getColumnAsString(String columnName) {
        int columnPosition = this.getColumnPosition(columnName);
        return this.getColumnAsString(columnPosition);
    }

    ArrayList getColumnData() {
        return this.columnData;
    }

    private void checkColumnNumber(int column, int type) {
        --column;
        if (column >= 0 && column <= this.nCols) {
            if (this.columnType[column] != type) {
                throw new RuntimeException("column " + column + " is type " + this.columnType[column] + " not type " + type);
            }
        } else {
            String msg = "Column number out of range: " + column;
            msg = msg + ", number of columns: " + this.nCols;
            throw new RuntimeException(msg);
        }
    }

    public String getColumnLabel(int column) {
        --column;
        return this.columnLabels != null && column >= 0 && column < this.columnLabels.size() ? (String)this.columnLabels.get(column) : "";
    }

    public int getColumnPosition(String columnName) {
        int position = -1;

        for(int col = 0; col < this.columnLabels.size(); ++col) {
            String currentColumn = (String)this.columnLabels.get(col);
            if (currentColumn.equalsIgnoreCase(columnName)) {
                position = col + 1;
                break;
            }
        }

        return position;
    }

    public int checkColumnPosition(String columnName) throws RuntimeException {
        int position = this.getColumnPosition(columnName);
        if (position < 0) {
            throw new RuntimeException("Column " + columnName + " does not exist in TableDataSet " + this.getName());
        } else {
            return position;
        }
    }

    public boolean containsColumn(String columnName) {
        int position = this.getColumnPosition(columnName);
        return position >= 0;
    }

    public float[] getRowValues(int row) {
        --row;
        int startPosition = 0;
        float[] rowValues = new float[this.nCols + startPosition];

        for(int c = 0; c < this.nCols; ++c) {
            if (this.columnType[c] == 2) {
                throw new RuntimeException("column " + c + 1 + " is of type STRING");
            }

            float[] f = (float[])((float[])this.columnData.get(c));
            rowValues[c + startPosition] = f[row];
        }

        return rowValues;
    }

    public float[] getIndexedRowValuesAt(int row) {
        if (this.columnIndex == null) {
            throw new RuntimeException("No index defined.");
        } else {
            row = this.columnIndex[row] + 1;
            return this.getRowValues(row);
        }
    }

    public int getIndexedRowNumber(int index) {
        if (this.columnIndex == null) {
            throw new RuntimeException("No index defined.");
        } else {
            return this.columnIndex[index] + 1;
        }
    }

    public String[] getRowValuesAsString(int row) {
        --row;
        int startPosition = 0;
        String[] rowValues = new String[this.nCols + startPosition];

        for(int c = 0; c < this.nCols; ++c) {
            switch(this.columnType[c]) {
                case 2:
                    String[] s = (String[])((String[])this.columnData.get(c));
                    rowValues[c + startPosition] = s[row];
                    break;
                case 3:
                    float[] f = (float[])((float[])this.columnData.get(c));
                    rowValues[c + startPosition] = this.valueFormat.format((double)f[row]);
            }
        }

        return rowValues;
    }

    public float[][] getValues() {
        float[][] tableValues = new float[this.nRows][this.nCols];

        for(int c = 0; c < this.nCols; ++c) {
            if (this.columnType[c] == 2) {
                throw new RuntimeException("column " + c + 1 + " is of type STRING");
            }

            float[] f = (float[])((float[])this.columnData.get(c));

            for(int r = 0; r < this.nRows; ++r) {
                tableValues[r][c] = f[r];
            }
        }

        return tableValues;
    }

    public float getValueAt(int row, int column) {
        --row;
        --column;
        Object var3 = null;

        float[] f;
        try {
            f = (float[])((float[])this.columnData.get(column));
        } catch (ClassCastException var5) {
            throw new RuntimeException("Column " + column + " in TableDataSet is not float values", var5);
        }

        return f[row];
    }

    public float getValueAt(int row, String columnName) {
        int columnNumber = this.getColumnPosition(columnName);
        if (columnNumber <= 0) {
            logger.error("no column named " + columnName + " in TableDataSet");
            throw new RuntimeException("no column named " + columnName + " in TableDataSet");
        } else {
            return this.getValueAt(row, columnNumber);
        }
    }

    public String getStringValueAt(int row, int column) {
        --row;
        --column;
        String value;
        if (this.columnType[column] == 3) {
            float[] f = (float[])((float[])this.columnData.get(column));
            value = this.valueFormat.format((double)f[row]);
        } else {
            String[] s = (String[])((String[])this.columnData.get(column));
            value = s[row];
        }

        return value;
    }

    public boolean getBooleanValueAt(int row, int column) {
        String boolString = this.getStringValueAt(row, column);
        if (boolString == null) {
            throw new RuntimeException("Boolean value in TableDataSet " + this.name + " is blank (null)");
        } else {
            if (this.use1sAnd0sForTrueFalse) {
                if (boolString.equals("0")) {
                    return false;
                }

                if (boolString.equals("1")) {
                    return true;
                }
            }

            if (boolString.equalsIgnoreCase("true")) {
                return true;
            } else if (boolString.equalsIgnoreCase("false")) {
                return false;
            } else {
                throw new RuntimeException("Boolean value in table dataset " + this.name + " column " + column + " is neither true nor false, but " + boolString);
            }
        }
    }

    public boolean getBooleanValueAt(int row, String columnName) {
        return this.getBooleanValueAt(row, this.checkColumnPosition(columnName));
    }

    public void setBooleanValueAt(int row, String columnName, boolean value) {
        this.setBooleanValueAt(row, this.checkColumnPosition(columnName), value);
    }

    public void setBooleanValueAt(int row, int column, boolean value) {
        if (value) {
            this.setStringValueAt(row, column, "true");
        } else {
            this.setStringValueAt(row, column, "false");
        }

    }

    public String getStringValueAt(int row, String columnName) {
        --row;
        int columnNumber = this.getColumnPosition(columnName);
        if (columnNumber <= 0) {
            logger.error("no column named " + columnName + " in TableDataSet");
            throw new RuntimeException("no column named " + columnName + " in TableDataSet");
        } else {
            return this.getStringValueAt(row + 1, columnNumber);
        }
    }

    public void setValueAt(int row, String colName, float newValue) {
        int col = this.getColumnPosition(colName);
        this.setValueAt(row, col, newValue);
    }

    public void setValueAt(int row, int column, float newValue) {
        --row;
        --column;
        float[] f = (float[])((float[])this.columnData.get(column));
        f[row] = newValue;
        if (this.indexColumns[column]) {
            this.fireIndexValuesChanged();
        }

        this.setDirty(true);
    }

    public void setColumnAsInt(int column, int[] newValues) {
        --column;
        float[] f = new float[newValues.length];

        for(int i = 0; i < newValues.length; ++i) {
            f[i] = (float)newValues[i];
        }

        this.columnData.set(column, f);
        if (this.indexColumns[column]) {
            this.fireIndexValuesChanged();
        }

        this.setDirty(true);
    }

    public void setColumnAsFloat(int column, float[] newValues) {
        --column;
        this.columnData.set(column, newValues);
        if (this.indexColumns[column]) {
            this.fireIndexValuesChanged();
        }

        this.setDirty(true);
    }

    public void setColumnAsDouble(int column, double[] newValues) {
        --column;
        float[] f = new float[newValues.length];

        for(int i = 0; i < newValues.length; ++i) {
            f[i] = (float)newValues[i];
        }

        this.columnData.set(column, f);
        if (this.indexColumns[column]) {
            this.fireIndexValuesChanged();
        }

        this.setDirty(true);
    }

    public void setIndexedValueAt(int row, String colName, float newValue) {
        if (this.columnIndex == null) {
            throw new RuntimeException("No index defined.");
        } else {
            row = this.columnIndex[row] + 1;
            int col = this.getColumnPosition(colName);
            this.setValueAt(row, col, newValue);
        }
    }

    public void setIndexedValueAt(int row, int column, float newValue) {
        if (this.columnIndex == null) {
            throw new RuntimeException("No index defined.");
        } else {
            row = this.columnIndex[row] + 1;
            this.setValueAt(row, column, newValue);
        }
    }

    public void setStringValueAt(int row, int column, String newValue) {
        --row;
        --column;
        String[] s = (String[])((String[])this.columnData.get(column));
        s[row] = newValue;
        this.setDirty(true);
        if (this.indexColumns[column]) {
            this.fireIndexValuesChanged();
        }

        if (column == this.stringIndexColumn) {
            this.stringIndexDirty = true;
        }

    }

    public void setStringValueAt(int row, String column, String value) {
        this.setStringValueAt(row, this.checkColumnPosition(column), value);
    }

    public void setIndexColumnNames(String[] indexColumnNames) {
        for(int i = 0; i < indexColumnNames.length; ++i) {
            int column = this.getColumnPosition(indexColumnNames[i]);
            if (column == -1) {
                throw new RuntimeException("Can't find column name " + indexColumnNames[i] + " in TableDataSet for indexing");
            }

            this.indexColumns[column - 1] = true;
        }

    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void setDirty(boolean dirtyParam) {
        if (dirtyParam && !this.dirty) {
            this.dirty = dirtyParam;
            if (this.myWatchers != null) {
                for(int w = 0; w < this.myWatchers.size(); ++w) {
                    TableDataSet.TableDataSetWatcher t = (TableDataSet.TableDataSetWatcher)this.myWatchers.get(w);
                    t.isDirty(this);
                }
            }
        }

        if (!dirtyParam && this.dirty && logger.isDebugEnabled()) {
            logger.debug("Making TableDataset " + this.name + " into a clean one");
        }

        this.dirty = dirtyParam;
    }

    public void buildIndex(int columnNumber) {
        --columnNumber;
        float[] column = (float[])((float[])this.columnData.get(columnNumber));
        int highestNumber = 0;

        int r;
        for(r = 0; r < this.nRows; ++r) {
            highestNumber = (int)Math.max((float)highestNumber, column[r]);
        }

        this.columnIndex = new int[highestNumber + 1];
        Arrays.fill(this.columnIndex, -1);

        for(r = 0; r < this.nRows; this.columnIndex[(int)column[r]] = r++) {
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Indexed column: " + columnNumber);
            logger.debug("Highest Number in indexed column: " + highestNumber);
        }

    }

    public float getIndexedValueAt(int value, int col) {
        if (this.columnIndex == null) {
            throw new RuntimeException("No index defined for column: " + col);
        } else {
            value = this.columnIndex[value];
            --col;
            float[] column = (float[])((float[])this.columnData.get(col));
            return column[value];
        }
    }

    public float getIndexedValueAt(int value, String columnName) {
        int columnNumber = this.getColumnPosition(columnName);
        if (columnNumber <= 0) {
            logger.error("no column named " + columnName + " in TableDataSet");
            throw new RuntimeException("no column named " + columnName + " in TableDataSet");
        } else {
            return this.getIndexedValueAt(value, columnNumber);
        }
    }

    public void appendColumn(Object newColumn, String newColumnLabel) {
        int[] oldColumnType;
        byte newType;
        int rows;
        if (newColumn instanceof float[]) {
            newType = 3;
            rows = ((float[])((float[])newColumn)).length;
            this.columnData.add((float[])((float[])newColumn));
        } else {
            float[] tempFloatArray;
            int i;
            if (newColumn instanceof int[]) {
                newType = 3;
                rows = ((int[])((int[])newColumn)).length;
                oldColumnType = (int[])((int[])newColumn);
                tempFloatArray = new float[rows];

                for(i = 0; i < rows; ++i) {
                    tempFloatArray[i] = (float)oldColumnType[i];
                }

                this.columnData.add(tempFloatArray);
            } else if (newColumn instanceof double[]) {
                newType = 3;
                rows = ((double[])((double[])newColumn)).length;
                double[] tempDoubleArray = (double[])((double[])newColumn);
                tempFloatArray = new float[rows];

                for(i = 0; i < rows; ++i) {
                    tempFloatArray[i] = (float)tempDoubleArray[i];
                }

                this.columnData.add(tempFloatArray);
            } else {
                if (!(newColumn instanceof String[])) {
                    throw new RuntimeException("invalid column type - cannot add to table");
                }

                newType = 2;
                rows = ((String[])((String[])newColumn)).length;
                this.columnData.add((String[])((String[])newColumn));
            }
        }

        if (this.nRows == 0) {
            this.nRows = rows;
        }

        if (rows != this.nRows) {
            String msg = "could not append column with " + rows + " elements to data set with " + this.nRows + " rows.";
            logger.error(msg);
            throw new RuntimeException(msg);
        } else {
            if (this.columnType == null) {
                this.columnType = new int[1];
                this.columnType[0] = newType;
            } else {
                oldColumnType = this.columnType;
                this.columnType = new int[oldColumnType.length + 1];
                System.arraycopy(oldColumnType, 0, this.columnType, 0, oldColumnType.length);
                this.columnType[this.columnData.size() - 1] = newType;
            }

            if (this.indexColumns == null) {
                this.indexColumns = new boolean[1];
                this.indexColumns[0] = false;
            } else {
                boolean[] oldIndexColumns = this.indexColumns;
                this.indexColumns = new boolean[oldIndexColumns.length + 1];
                System.arraycopy(oldIndexColumns, 0, this.indexColumns, 0, oldIndexColumns.length);
                this.indexColumns[this.columnData.size() - 1] = false;
            }

            this.nCols = this.columnData.size();
            if (newColumnLabel == null || newColumnLabel.length() == 0) {
                newColumnLabel = "column_" + this.nCols;
            }

            this.columnLabels.add(newColumnLabel);
            this.columnLabelsPresent = true;
        }
    }

    public void appendColumnAsDouble(Object newColumn, String newColumnLabel) {
        byte newType;
        int rows;
        if (newColumn instanceof double[]) {
            newType = 4;
            rows = ((double[])((double[])newColumn)).length;
            double[] tempDoubleArray = (double[])((double[])newColumn);
            this.columnData.add(tempDoubleArray);
        } else {
            if (!(newColumn instanceof String[])) {
                throw new RuntimeException("invalid column type - cannot add to table");
            }

            newType = 2;
            rows = ((String[])((String[])newColumn)).length;
            this.columnData.add((String[])((String[])newColumn));
        }

        if (this.nRows == 0) {
            this.nRows = rows;
        }

        if (rows != this.nRows) {
            String msg = "could not append column with " + rows + " elements to data set with " + this.nRows + " rows.";
            logger.error(msg);
            throw new RuntimeException(msg);
        } else {
            if (this.columnType == null) {
                this.columnType = new int[1];
                this.columnType[0] = newType;
            } else {
                int[] oldColumnType = this.columnType;
                this.columnType = new int[oldColumnType.length + 1];
                System.arraycopy(oldColumnType, 0, this.columnType, 0, oldColumnType.length);
                this.columnType[this.columnData.size() - 1] = newType;
            }

            if (this.indexColumns == null) {
                this.indexColumns = new boolean[1];
                this.indexColumns[0] = false;
            } else {
                boolean[] oldIndexColumns = this.indexColumns;
                this.indexColumns = new boolean[oldIndexColumns.length + 1];
                System.arraycopy(oldIndexColumns, 0, this.indexColumns, 0, oldIndexColumns.length);
                this.indexColumns[this.columnData.size() - 1] = false;
            }

            this.nCols = this.columnData.size();
            if (newColumnLabel == null || newColumnLabel.length() == 0) {
                newColumnLabel = "column_" + this.nCols;
            }

            this.columnLabels.add(newColumnLabel);
            this.columnLabelsPresent = true;
        }
    }

    public static TableDataSet create(float[][] tableData) {
        int rows = tableData.length;
        int cols = tableData[0].length;
        TableDataSet newTable = new TableDataSet();

        for(int c = 0; c < cols; ++c) {
            float[] newColumn = new float[rows];

            for(int r = 0; r < rows; ++r) {
                newColumn[r] = tableData[r][c];
            }

            newTable.appendColumn(newColumn, "column_" + (c + 1));
        }

        return newTable;
    }

    public static TableDataSet create(float[][] tableData, ArrayList tableHeadings) {
        int rows = tableData.length;
        int cols = tableData[0].length;
        TableDataSet newTable = new TableDataSet();

        for(int c = 0; c < cols; ++c) {
            float[] newColumn = new float[rows];

            for(int r = 0; r < rows; ++r) {
                newColumn[r] = tableData[r][c];
            }

            newTable.appendColumn(newColumn, (String)tableHeadings.get(c));
        }

        return newTable;
    }

    public static TableDataSet create(float[][] tableData, String[] tableHeadings) {
        int rows = tableData.length;
        int cols = tableData[0].length;
        TableDataSet newTable = new TableDataSet();

        for(int c = 0; c < cols; ++c) {
            float[] newColumn = new float[rows];

            for(int r = 0; r < rows; ++r) {
                newColumn[r] = tableData[r][c];
            }

            newTable.appendColumn(newColumn, tableHeadings[c]);
        }

        return newTable;
    }

    public static TableDataSet create(String[][] tableData, ArrayList tableHeadings) {
        int rows = tableData.length;
        int cols = tableData[0].length;
        TableDataSet newTable = new TableDataSet();

        for(int c = 0; c < cols; ++c) {
            String[] newColumn = new String[rows];

            for(int r = 0; r < rows; ++r) {
                newColumn[r] = tableData[r][c];
            }

            newTable.appendColumn(newColumn, (String)tableHeadings.get(c));
        }

        return newTable;
    }

    public static TableDataSet create(String[][] tableData, String[] tableHeadings) {
        int rows = tableData.length;
        int cols = tableData[0].length;
        TableDataSet newTable = new TableDataSet();

        for(int c = 0; c < cols; ++c) {
            String[] newColumn = new String[rows];

            for(int r = 0; r < rows; ++r) {
                newColumn[r] = tableData[r][c];
            }

            newTable.appendColumn(newColumn, tableHeadings[c]);
        }

        return newTable;
    }

    public static TableDataSet readFile(String fileName) {
        logger.info("Begin reading the data in file " + fileName);

        TableDataSet data;
        try {
            OLD_CSVFileReader csvFile = new OLD_CSVFileReader();
            data = csvFile.readFile(new File(fileName));
        } catch (IOException var3) {
            throw new RuntimeException(var3);
        }

        logger.info("End reading the data in file " + fileName);
        return data;
    }

    public static void writeFile(String fileName, TableDataSet data) {
        logger.info("Begin writing the data in file " + fileName);

        try {
            CSVFileWriter csvFile = new CSVFileWriter();
            csvFile.writeFile(data, new File(fileName));
        } catch (IOException var3) {
            throw new RuntimeException(var3);
        }

        logger.info("End writing the data in file " + fileName);
    }

    public void merge(TableDataSet tdsIn) {
        String[] tdsInHeadings = tdsIn.getColumnLabels();
        int[] tdsInTypes = tdsIn.getColumnType();

        for(int c = 0; c < tdsIn.getColumnCount(); ++c) {
            if (this.getColumnPosition(tdsInHeadings[c]) < 0) {
                logger.info("Adding column " + tdsInHeadings[c] + " to TableDataSet due to merge");
                int r;
                if (tdsInTypes[c] == 3) {
                    float[] newColumn = new float[this.nRows];

                    for(r = 0; r < this.nRows; ++r) {
                        newColumn[r] = tdsIn.getValueAt(r + 1, c + 1);
                    }

                    this.appendColumn(newColumn, tdsInHeadings[c]);
                } else if (tdsInTypes[c] == 2) {
                    String[] newColumn = new String[this.nRows];

                    for(r = 0; r < this.nRows; ++r) {
                        newColumn[r] = tdsIn.getStringValueAt(r + 1, c + 1);
                    }

                    this.appendColumn(newColumn, tdsInHeadings[c]);
                }
            }
        }

        this.setDirty(true);
    }

    public static void logColumnFreqReport(String tableName, TableDataSet tds, int columnPosition) {
        if (tds.getRowCount() == 0) {
            logger.info(tableName + " Table is empty - no data to summarize");
        } else {
            float[] columnData = new float[tds.getRowCount()];
            int[] sortValues = new int[tds.getRowCount()];

            for(int r = 1; r <= tds.getRowCount(); ++r) {
                columnData[r - 1] = tds.getValueAt(r, columnPosition);
                sortValues[r - 1] = (int)(columnData[r - 1] * 10000.0F);
            }

            int[] index = IndexSort.indexSort(sortValues);
            ArrayList bucketValues = new ArrayList();
            ArrayList bucketSizes = new ArrayList();
            float oldValue = columnData[index[0]];
            int count = 1;

            int total;
            for(total = 1; total < tds.getRowCount(); ++total) {
                if (columnData[index[total]] > oldValue) {
                    bucketValues.add(Float.toString(oldValue));
                    bucketSizes.add(Integer.toString(count));
                    count = 0;
                    oldValue = columnData[index[total]];
                }

                ++count;
            }

            bucketValues.add(Float.toString(oldValue));
            bucketSizes.add(Integer.toString(count));
            logger.info("Frequency Report table: " + tableName);
            logger.info("Frequency for column " + columnPosition + ": " + tds.getColumnLabel(columnPosition));
            logger.info(String.format("%8s", "Value") + String.format("%11s", "Frequency"));
            total = 0;

            for(int i = 0; i < bucketValues.size(); ++i) {
                float value = Float.parseFloat((String)((String)bucketValues.get(i)));
                logger.info(String.format("%8.0f", value) + String.format("%11d", Integer.parseInt((String)((String)bucketSizes.get(i)))));
                total += Integer.parseInt((String)((String)bucketSizes.get(i)));
            }

            logger.info(String.format("%8s", "Total") + String.format("%11d\n\n\n", total));
        }
    }

    public static void logColumnFreqReport(String tableName, TableDataSet tds, int columnPosition, String[] descriptions) {
        if (tds.getRowCount() == 0) {
            logger.info(tableName + " Table is empty - no data to summarize");
        } else {
            float[] columnData = new float[tds.getRowCount()];
            int[] sortValues = new int[tds.getRowCount()];

            for(int r = 1; r <= tds.getRowCount(); ++r) {
                columnData[r - 1] = tds.getValueAt(r, columnPosition);
                sortValues[r - 1] = (int)(columnData[r - 1] * 10000.0F);
            }

            int[] index = IndexSort.indexSort(sortValues);
            ArrayList bucketValues = new ArrayList();
            ArrayList bucketSizes = new ArrayList();
            float oldValue = columnData[index[0]];
            int count = 1;

            int total;
            for(total = 1; total < tds.getRowCount(); ++total) {
                if (columnData[index[total]] > oldValue) {
                    bucketValues.add(Float.toString(oldValue));
                    bucketSizes.add(Integer.toString(count));
                    count = 0;
                    oldValue = columnData[index[total]];
                }

                ++count;
            }

            bucketValues.add(Float.toString(oldValue));
            bucketSizes.add(Integer.toString(count));
            logger.info("Frequency Report table: " + tableName);
            logger.info("Frequency for column " + columnPosition + ": " + tds.getColumnLabel(columnPosition));
            logger.info(String.format("%8s", "Value") + String.format("%13s", "Description") + String.format("%11s", "Frequency"));
            if (descriptions != null && bucketValues.size() != descriptions.length) {
                logger.fatal("The number of descriptions does not match the number of values in your data");
            }

            total = 0;

            for(int i = 0; i < bucketValues.size(); ++i) {
                float value = Float.parseFloat((String)((String)bucketValues.get(i)));
                String description = "";
                if (descriptions != null) {
                    description = descriptions[i];
                }

                logger.info(String.format("%8.0f", value) + "  " + String.format("%-11s", description) + String.format("%11d", Integer.parseInt((String)((String)bucketSizes.get(i)))));
                total += Integer.parseInt((String)((String)bucketSizes.get(i)));
            }

            logger.info(String.format("%23s", "Total") + String.format("%9d\n\n\n", total));
        }
    }

    public void removeChangeListener(TableDataSet.ChangeListener index) {
        this.changeListeners.remove(index);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return "TableDataSet " + this.name;
    }

    public void addFinalizingListener(TableDataSet.TableDataSetWatcher watcher) {
        if (this.myWatchers == null) {
            this.myWatchers = new ArrayList();
        }

        this.myWatchers.add(watcher);
    }

    void tellWatchersImBeingForgotten() {
        if (this.myWatchers != null) {
            for(int w = 0; w < this.myWatchers.size(); ++w) {
                ((TableDataSet.TableDataSetWatcher)this.myWatchers.get(w)).isBeingForgotten(this);
            }
        }

        this.columnData = null;
    }

    public void buildStringIndex(int column) {
        this.checkColumnNumber(column, 2);
        --column;
        String[] columnValues = (String[])((String[])this.columnData.get(column));
        this.stringIndex = new HashMap(columnValues.length);
        Set<String> repeatedValues = new HashSet();

        for(int i = 0; i < columnValues.length; ++i) {
            if (this.stringIndex.put(columnValues[i], i + 1) != null) {
                repeatedValues.add(columnValues[i]);
            }
        }

        if (repeatedValues.size() > 0) {
            this.stringIndex = null;
            this.stringIndexColumn = -1;
            throw new IllegalStateException("String index cannot be built on a column with non-unique values.The following values have been repeated: " + Arrays.toString(repeatedValues.toArray(new String[repeatedValues.size()])));
        } else {
            this.stringIndexColumn = column;
            this.stringIndexDirty = false;
        }
    }

    private void checkStringIndexValue(String index) {
        if (this.stringIndex == null) {
            throw new IllegalStateException("No string index exists for this table.");
        } else if (this.stringIndexDirty) {
            throw new IllegalStateException("String index column changed, must be rebuilt.");
        } else if (!this.stringIndex.containsKey(index)) {
            throw new IllegalArgumentException("String value not found in index: " + index);
        }
    }

    public int getStringIndexedRowNumber(String index) {
        this.checkStringIndexValue(index);
        return (Integer)this.stringIndex.get(index) - 1;
    }

    public float getStringIndexedValueAt(String index, int column) {
        this.checkStringIndexValue(index);
        return this.getValueAt((Integer)this.stringIndex.get(index), column);
    }

    public float getStringIndexedValueAt(String index, String column) {
        this.checkStringIndexValue(index);
        return this.getValueAt((Integer)this.stringIndex.get(index), column);
    }

    public boolean getStringIndexedBooleanValueAt(String index, int column) {
        this.checkStringIndexValue(index);
        return this.getBooleanValueAt((Integer)this.stringIndex.get(index), column);
    }

    public boolean getStringIndexedBooleanValueAt(String index, String column) {
        this.checkStringIndexValue(index);
        return this.getBooleanValueAt((Integer)this.stringIndex.get(index), column);
    }

    public String getStringIndexedStringValueAt(String index, int column) {
        this.checkStringIndexValue(index);
        return this.getStringValueAt((Integer)this.stringIndex.get(index), column);
    }

    public String getStringIndexedStringValueAt(String index, String column) {
        this.checkStringIndexValue(index);
        return this.getStringValueAt((Integer)this.stringIndex.get(index), column);
    }

    public void setStringIndexedValueAt(String index, int column, float value) {
        this.checkStringIndexValue(index);
        this.setValueAt((Integer)this.stringIndex.get(index), column, value);
    }

    public void setStringIndexedValueAt(String index, String column, float value) {
        this.checkStringIndexValue(index);
        this.setValueAt((Integer)this.stringIndex.get(index), column, value);
    }

    public void setStringIndexedBooleanValueAt(String index, int column, boolean value) {
        this.checkStringIndexValue(index);
        this.setBooleanValueAt((Integer)this.stringIndex.get(index), column, value);
    }

    public void setStringIndexedBooleanValueAt(String index, String column, boolean value) {
        this.checkStringIndexValue(index);
        this.setBooleanValueAt((Integer)this.stringIndex.get(index), column, value);
    }

    public void setStringIndexedStringValueAt(String index, int column, String value) {
        this.checkStringIndexValue(index);
        this.setStringValueAt((Integer)this.stringIndex.get(index), column, value);
    }

    public void setStringIndexedStringValueAt(String index, String column, String value) {
        this.checkStringIndexValue(index);
        this.setStringValueAt((Integer)this.stringIndex.get(index), column, value);
    }

    public void appendRow(HashMap rowData) {
        String[] headers = this.getColumnLabels();
        int columnNum = 1;
        String[] var5 = headers;
        int var6 = headers.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            String header = var5[var7];
            System.out.println("Header Value to Append: " + header);
            int type = this.getColumnType()[this.getColumnPosition(header) - 1];
            if (type == 3) {
                float[] col = this.getColumnAsFloat(header);
                float[] newCol = new float[col.length + 1];
                System.arraycopy(col, 0, newCol, 0, col.length);
                newCol[newCol.length - 1] = (Float)rowData.get(header);
                this.replaceFloatColumn(columnNum, newCol);
            } else if (type == 2) {
                String[] col = this.getColumnAsString(header);
                String[] newCol = new String[col.length + 1];
                System.arraycopy(col, 0, newCol, 0, col.length);
                newCol[newCol.length - 1] = (String)rowData.get(header);
                this.replaceStringColumn(columnNum, newCol);
            }

            ++columnNum;
        }

        ++this.nRows;
    }

    public void replaceFloatColumn(int colNumber, float[] newData) {
        this.columnData.remove(colNumber - 1);
        this.columnData.add(colNumber - 1, newData);
    }

    public void replaceStringColumn(int colNumber, String[] newData) {
        this.columnData.remove(colNumber - 1);
        this.columnData.add(colNumber - 1, newData);
    }

    public String getIndexedStringValueAt(int value, int col) {
        if (this.columnIndex == null) {
            throw new RuntimeException("No index defined for column: " + col);
        } else {
            value = this.columnIndex[value];
            --col;
            String[] column = (String[])((String[])this.columnData.get(col));
            return column[value];
        }
    }

    public String getIndexedStringValueAt(int value, String columnName) {
        int columnNumber = this.getColumnPosition(columnName);
        if (columnNumber <= 0) {
            logger.error("no column named " + columnName + " in TableDataSet");
            throw new RuntimeException("no column named " + columnName + " in TableDataSet");
        } else {
            return this.getIndexedStringValueAt(value, columnNumber);
        }
    }

    public interface TableDataSetWatcher {
        void isBeingForgotten(TableDataSet var1);

        void isDirty(TableDataSet var1);
    }

    public interface ChangeListener {
        void indexValuesChanged();
    }
}
