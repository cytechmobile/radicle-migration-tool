package network.radicle.tools.github.utils;

import com.google.common.base.Strings;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.emphasis.BoldText;
import net.steppschuh.markdowngenerator.text.emphasis.StrikeThroughText;

public class Markdown {
    private final StringBuilder builder;
    private final Table.Builder tableBuilder;

    public Markdown() {
        this.builder = new StringBuilder();
        this.tableBuilder = new Table.Builder().withAlignments(Table.ALIGN_CENTER, Table.ALIGN_CENTER);
    }

    public Markdown openDropDown(String summary) {
        builder.append("<details>");
        builder.append("\n");
        builder.append("<summary>").append(summary).append("</summary>");
        builder.append("\n");
        return this;
    }

    public Markdown closeDropDown() {
        builder.append("\n");
        builder.append("</details>");
        return this;
    }

    public Markdown openTable(Object... headers) {
        tableBuilder.addRow(headers);
        return this;
    }

    public Markdown addTableRow(Object... row) {
        tableBuilder.addRow(row);
        return this;
    }

    public Markdown closeTable() {
        builder.append("\n\n");
        builder.append(tableBuilder.build().toString());
        return this;
    }

    public static String link(String text, String url) {
        return "[" + text + " ](" + url + ")";
    }

    public static String bold(String text) {
        return new BoldText(text).toString();
    }

    public static String strikethrough(String text) {
        return new StrikeThroughText(text).toString();
    }

    public static String escape(String markdown) {
        return Strings.nullToEmpty(markdown).replace(":", "\\:");
    }

    public String build() {
        return builder.toString();
    }

}
