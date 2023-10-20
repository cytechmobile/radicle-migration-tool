package network.radicle.tools.github.services;

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
import jakarta.enterprise.context.ApplicationScoped;
import network.radicle.tools.github.core.github.Comment;
import network.radicle.tools.github.core.github.Event;
import network.radicle.tools.github.core.github.Issue;
import network.radicle.tools.github.core.github.Timeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static network.radicle.tools.github.core.github.Issue.METADATA_TITLE;

@ApplicationScoped
public class MarkdownService {
    public static final DateTimeFormatter DTF =
            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss 'UTC'").withZone(ZoneId.of("UTC"));

    private static final Logger logger = LoggerFactory.getLogger(MarkdownService.class);

    public String getMetadata(Event event) {
        var body = new ArrayList<String>();

        //actor profile link
        if (!Event.Type.CROSS_REFERENCED.value.equals(event.event)) {
            body.add(link(bold(event.actor.login), event.actor.htmlUrl));
        }

        switch (Event.Type.from(event.event)) {
            case ASSIGNED, UNASSIGNED -> {
                body.add(event.event);
                body.add(link(bold(event.assignee.login), event.assignee.htmlUrl));
            }
            case LABELED -> {
                body.add("added the");
                body.add(bold(event.label.name));
                body.add("label");
            }
            case UNLABELED -> {
                body.add("removed the");
                body.add(bold(event.label.name));
                body.add("label");
            }
            case MILESTONED -> {
                body.add("added this to the");
                body.add(bold(event.milestone.title));
                body.add("milestone");
            }
            case DEMILESTONED -> {
                body.add("removed this from the");
                body.add(bold(event.milestone.title));
                body.add("milestone");
            }
            case CLOSED -> {
                body.add("closed this");
                if (!Strings.isNullOrEmpty(event.stateReason)) {
                    body.add("as " + event.stateReason);
                }
                if (event.commit != null) {
                    var message = event.commit.metadata.message.split("\n")[0];
                    body.add("in");
                    body.add(event.commit.sha);
                    body.add(link(bold(message), event.commit.htmlUrl));
                }
            }
            case REOPENED -> body.add("reopened this");
            case RENAMED -> {
                body.add("changed the title");
                body.add(strikethrough(event.rename.from));
                body.add(bold(event.rename.to));
            }
            case CROSS_REFERENCED -> {
                body.add("This was referenced by");
                body.add(link(bold(event.source.issue.title + " #" + event.source.issue.number),
                        event.source.issue.htmlUrl));
            }
            case MENTIONED -> body.add("was mentioned");
            case REFERENCED -> {
                body.add("added the commit");
                body.add(event.commit.sha);
                var message = event.commit.metadata.message.split("\n")[0];
                body.add(link(bold(message), event.commit.htmlUrl));
                body.add("that referenced this issue");
            }
            default -> body.add(event.event);
        }

        //escape : to properly get displayed in radicle web app
        body.add("on " + escape(DTF.format(event.createdAt)));
        return String.join(" ", body);
    }

    public String getMetadata(Comment comment) {
        var header = new ArrayList<String>();
        header.add("Number");
        header.add("Created On");
        header.add("Created By");

        var rows = new ArrayList<String>();
        rows.add(link(String.valueOf(comment.id), comment.htmlUrl));
        rows.add(escape(DTF.format(comment.createdAt)));
        rows.add(link(comment.user.login, comment.user.htmlUrl));

        return new MetadataBuilder()
                .summary(METADATA_TITLE)
                .createTable(header.toArray(new String[0]))
                .addTableRows(rows.toArray(new String[0]))
                .build();
    }

    public String getMetadata(Issue issue) {
        var headers = new ArrayList<String>();
        headers.add("Number");
        headers.add("Created On");
        headers.add("Created By");

        var hasAssignees = issue.assignees != null && !issue.assignees.isEmpty();
        if (hasAssignees) {
            headers.add("Assignees");
        }
        var hasMilestone = issue.milestone != null;
        if (hasMilestone) {
            headers.add("Milestone");
            headers.add("Due By");
        }

        var rows = new ArrayList<String>();
        rows.add(link("#" + issue.number, issue.htmlUrl));
        rows.add(escape(DTF.format(issue.createdAt)));
        rows.add(link(issue.user.login, issue.user.htmlUrl));
        if (hasAssignees) {
            rows.add(issue.assignees.stream()
                    .map(a -> link(a.login, a.htmlUrl))
                    .collect(Collectors.joining(", ")));
        }
        if (hasMilestone) {
            rows.add(link(issue.milestone.title, issue.milestone.htmlUrl));
            if (issue.milestone.dueOn != null) {
                rows.add(escape(DTF.format(Instant.parse(issue.milestone.dueOn))));
            } else {
                rows.add("-");
            }
        }

        return new MetadataBuilder()
                .summary(METADATA_TITLE)
                .createTable(headers.toArray(new String[0]))
                .addTableRows(rows.toArray(new String[0]))
                .build();
    }

    public String getMetadata(Timeline timeline) {
        return Event.Type.COMMENT.value.equalsIgnoreCase(timeline.getType()) ?
                getMetadata((Comment) timeline) : getMetadata((Event) timeline);
    }

    public String getBodyWithMetadata(Timeline timeline) {
        var metadata = getMetadata(timeline);
        return metadata != null ? metadata + "<br/>" + "\n\n" + timeline.getBody() : timeline.getBody();
    }

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
