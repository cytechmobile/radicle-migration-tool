package network.radicle.tools.migrate.services;

import com.google.common.base.Strings;
import com.vladsch.flexmark.ast.AutoLink;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.Reference;
import com.vladsch.flexmark.ext.media.tags.AudioLink;
import com.vladsch.flexmark.ext.media.tags.EmbedLink;
import com.vladsch.flexmark.ext.media.tags.PictureLink;
import com.vladsch.flexmark.ext.media.tags.VideoLink;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.data.MutableDataSet;
import network.radicle.tools.migrate.core.Timeline;
import network.radicle.tools.migrate.core.gitlab.GitLabComment;
import network.radicle.tools.migrate.core.gitlab.GitLabEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class MarkdownService {
    public static final DateTimeFormatter DTF =
            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss 'UTC'").withZone(ZoneId.of("UTC"));

    private static final Logger logger = LoggerFactory.getLogger(MarkdownService.class);


    public String getBody(Timeline timeline) {
        var body = Strings.nullToEmpty(timeline.getBody());

        //GitLab comments include also some events. Let's format them properly.
        if (timeline instanceof GitLabComment comment) {
            if (body.startsWith(GitLabEvent.Type.ASSIGNED.value) ||
                    body.startsWith(GitLabEvent.Type.UNASSIGNED.value) ||
                    body.startsWith(GitLabEvent.Type.CHANGED_DUE_DATE.value) ||
                    body.startsWith(GitLabEvent.Type.CHANGED_TYPE.value) ||
                    body.startsWith(GitLabEvent.Type.RENAMED.value)) {

                var event = new ArrayList<String>();
                event.add(link(bold(comment.author.username), comment.author.webUrl));
                event.add(comment.body);
                event.add("on " + escape(DTF.format(comment.createdAt)));
                return String.join(" ", event);
            }
        }
        return body;
    }

    public String getBodyWithMetadata(Timeline timeline) {
        var metadata = getMetadata(timeline);
        var body = getBody(timeline);
        return metadata != null ? metadata + "<br/>" + "\n\n" + body : body;
    }

    public abstract String getMetadata(Timeline timeline);

    public  List<MarkdownLink> extractUrls(String markdown) {
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

    public String link(String text, String url) {
        var title = Strings.isNullOrEmpty(text) ? url : text;
        return "[" + title + " ](" + url + ")";
    }

    public String bold(String text) {
        return "**" + text + "**";
    }

    public String strikethrough(String text) {
        return "~~" + text + "~~";
    }

    public String escape(String markdown) {
        return Strings.nullToEmpty(markdown).replace(":", "\\:");
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

    public static class MetadataBuilder {
        private String summary;
        private final StringBuilder table;

        public MetadataBuilder() {
            this.table = new StringBuilder();
            this.summary = "";
        }

        public MetadataBuilder summary(String title) {
            this.summary = title;
            return this;
        }

        public MetadataBuilder createTable(String... headers) {
            table.append("|");
            for (var header : headers) {
                table.append(" ").append(header).append(" |");
            }
            table.append("\n|");
            for (var ignored : headers) {
                table.append(" --- |");
            }
            table.append("\n");
            return this;
        }

        public MetadataBuilder addTableRows(String... row) {
            table.append("|");
            for (String value : row) {
                table.append(" ").append(value).append(" |");
            }
            table.append("\n");
            return this;
        }

        public String build() {
            var tableMarkdown = table.toString();
            var options = new MutableDataSet().set(Parser.EXTENSIONS, List.of(TablesExtension.create()));
            var parser = Parser.builder(options).build();
            var document = parser.parse(tableMarkdown);

            return "<details><summary>" + summary + "</summary>\n\n" +
                    Formatter.builder(options).build().render(document) +
                    "\n\n</details>";
        }
    }
}
