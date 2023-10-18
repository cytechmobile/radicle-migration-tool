package network.radicle.tools.github.utils;

import com.google.common.base.Strings;
import com.vladsch.flexmark.ast.AutoLink;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.Reference;
import com.vladsch.flexmark.ext.media.tags.AudioLink;
import com.vladsch.flexmark.ext.media.tags.EmbedLink;
import com.vladsch.flexmark.ext.media.tags.PictureLink;
import com.vladsch.flexmark.ext.media.tags.VideoLink;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.data.MutableDataSet;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.emphasis.BoldText;
import net.steppschuh.markdowngenerator.text.emphasis.StrikeThroughText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Markdown {
    private static final Logger logger = LoggerFactory.getLogger(Markdown.class);

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
        var title = Strings.isNullOrEmpty(text) ? url : text;
        return "[" + title + " ](" + url + ")";
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

    public static List<MarkdownLink> extractUrls(String markdown) {
        var options = new MutableDataSet();
        var parser = Parser.builder(options).build();
        var document = parser.parse(markdown);

        var links = new ArrayList<MarkdownLink>();
        var linkHandler = new VisitHandler<>(Link.class,
                it -> links.add(new MarkdownLink(it.getText().toString(), it.getUrl().toStringOrNull())));
        var imageHandler = new VisitHandler<>(Image.class,
                it -> links.add(new MarkdownLink(it.getText().toString(), it.getUrl().toStringOrNull())));
        var videoLinkHandler = new VisitHandler<>(VideoLink.class,
                it -> links.add(new MarkdownLink(it.getText().toString(), it.getUrl().toStringOrNull())));
        var embeddedLinkHandler = new VisitHandler<>(EmbedLink.class,
                it -> links.add(new MarkdownLink(it.getText().toString(), it.getUrl().toStringOrNull())));
        var pictureLinkHandler = new VisitHandler<>(PictureLink.class,
                it -> links.add(new MarkdownLink(it.getText().toString(), it.getUrl().toStringOrNull())));
        var audioLinkHandler = new VisitHandler<>(AudioLink.class,
                it -> links.add(new MarkdownLink(it.getText().toString(), it.getUrl().toStringOrNull())));
        var autoLinkHandler = new VisitHandler<>(AutoLink.class,
                it -> links.add(new MarkdownLink(it.getText().toString(), it.getUrl().toStringOrNull())));
        var referenceHandler = new VisitHandler<>(Reference.class,
                it -> links.add(new MarkdownLink(it.getReference().toString(), it.getUrl().toStringOrNull())));

        var visitor = new NodeVisitor(linkHandler, imageHandler, videoLinkHandler, embeddedLinkHandler,
                pictureLinkHandler, audioLinkHandler, autoLinkHandler, referenceHandler);
        visitor.visitChildren(document);

        return links;
    }

    public String build() {
        return builder.toString();
    }

    public static class MarkdownLink {
        public String text;
        public String url;
        public String oid;

        public MarkdownLink(String text, String url, String oid) {
            this.text = text;
            this.url = url;
            this.oid = oid;
        }
        public MarkdownLink(String text, String url) {
            this(text, url, null);
        }
    }
}
