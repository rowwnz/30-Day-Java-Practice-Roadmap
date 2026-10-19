package sms.util;

import java.util.ArrayList;
import java.util.List;

/** Builds a plain-text table whose column widths fit the content. */
public class TablePrinter {
    private final String[] headers;
    private final List<String[]> rows = new ArrayList<>();

    public TablePrinter(String... headers) {
        this.headers = headers;
    }

    public TablePrinter addRow(Object... values) {
        String[] row = new String[headers.length];
        for (int i = 0; i < headers.length; i++) {
            Object value = i < values.length ? values[i] : null;
            row[i] = value == null ? "-" : String.valueOf(value);
        }
        rows.add(row);
        return this;
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public String render() {
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
        }
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                widths[i] = Math.max(widths[i], row[i].length());
            }
        }

        String separator = separator(widths);
        StringBuilder sb = new StringBuilder();
        sb.append(separator).append(line(headers, widths)).append(separator);
        for (String[] row : rows) {
            sb.append(line(row, widths));
        }
        if (!rows.isEmpty()) {
            sb.append(separator);
        }
        return sb.toString();
    }

    public void print() {
        System.out.print(render());
    }

    private static String line(String[] cells, int[] widths) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < cells.length; i++) {
            sb.append(' ').append(cells[i]).append(repeat(' ', widths[i] - cells[i].length())).append(" |");
        }
        return sb.append('\n').toString();
    }

    private static String separator(int[] widths) {
        StringBuilder sb = new StringBuilder("+");
        for (int width : widths) {
            sb.append(repeat('-', width + 2)).append('+');
        }
        return sb.append('\n').toString();
    }

    private static String repeat(char c, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
