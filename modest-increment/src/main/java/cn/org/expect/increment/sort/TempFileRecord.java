package cn.org.expect.increment.sort;

import cn.org.expect.io.TextTableLine;
import cn.org.expect.util.StringUtils;

/**
 * 文件记录类
 */
public class TempFileRecord implements TextTableLine {
    protected String line;

    protected String lineSeparator;

    protected String[] fields;

    protected int column;

    protected long lineNumber;

    public TempFileRecord(TextTableLine line) {
        this.column = line.getColumn();
        this.line = line.getContent();
        this.lineSeparator = line.getLineSeparator();
        this.fields = new String[this.column + 1];
        for (int i = 1; i <= this.column; i++) {
            this.fields[i] = line.getColumn(i);
        }
    }

    public TempFileRecord(TextTableLine line, long lineNumber) {
        this(line);
        this.lineNumber = lineNumber;
    }

    public String getContent() {
        return this.line;
    }

    public String getColumn(int index) {
        return this.fields[index];
    }

    public String getLineSeparator() {
        return this.lineSeparator;
    }

    public String toString() {
        return this.line;
    }

    public boolean isColumnBlank(int position) {
        return StringUtils.isBlank(this.fields[position]);
    }

    public void setColumn(int position, String value) {
        throw new UnsupportedOperationException();
    }

    public int getColumn() {
        return this.column;
    }

    public void setContext(String line) {
        throw new UnsupportedOperationException();
    }

    public long getLineNumber() {
        return this.lineNumber;
    }
}
