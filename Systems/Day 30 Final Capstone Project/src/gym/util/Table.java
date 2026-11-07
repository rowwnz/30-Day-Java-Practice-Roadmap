package gym.util;

import java.util.ArrayList;
import java.util.List;

/** Prints rows as a bordered text table, sizing each column to its widest cell. */
public class Table {
    private final String[] headers;
    private final List<String[]> rows = new ArrayList<>();

    public Table(String... headers) {
        this.headers = headers;
    }

    public Table row(Object... cells) {
        String[] row = new String[headers.length];
        for (int i = 0; i < headers.length; i++) {
            row[i] = i < cells.length && cells[i] != null ? String.valueOf(cells[i]) : "";
        }
        rows.add(row);
        return this;
    }

    public void print() {
        if (rows.isEmpty()) {
            System.out.println("  (no records found)");
            return;
        }
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) widths[i] = headers[i].length();
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) widths[i] = Math.max(widths[i], row[i].length());
        }

        String border = border(widths);
        System.out.println(border);
        System.out.println(line(headers, widths));
        System.out.println(border);
        for (String[] row : rows) System.out.println(line(row, widths));
        System.out.println(border);
    }

    private static String border(int[] widths) {
        StringBuilder sb = new StringBuilder("  +");
        for (int w : widths) sb.append("-".repeat(w + 2)).append('+');
        return sb.toString();
    }

    private static String line(String[] cells, int[] widths) {
        StringBuilder sb = new StringBuilder("  |");
        for (int i = 0; i < widths.length; i++) {
            sb.append(' ').append(cells[i]).append(" ".repeat(widths[i] - cells[i].length())).append(" |");
        }
        return sb.toString();
    }
}
