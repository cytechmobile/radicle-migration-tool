package network.radicle.tools.migrate.services.gitlab;

import jakarta.enterprise.context.ApplicationScoped;
import network.radicle.tools.migrate.core.Timeline;
import network.radicle.tools.migrate.core.gitlab.GitLabComment;
import network.radicle.tools.migrate.core.gitlab.GitLabEvent;
import network.radicle.tools.migrate.core.gitlab.GitLabIssue;
import network.radicle.tools.migrate.services.MarkdownService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.stream.Collectors;

@ApplicationScoped
public class GitLabMarkdownService extends MarkdownService {

    public String getMetadata(GitLabEvent event) {
        var body = new ArrayList<String>();

        //actor profile link
        body.add(this.link(this.bold(event.user.username), event.user.webUrl));

        switch (GitLabEvent.Type.from(event.getType())) {
            case LABELED -> {
                body.add("added the");
                body.add(this.bold(event.label.name));
                body.add("label");
            }
            case UNLABELED -> {
                body.add("removed the");
                body.add(this.bold(event.label.name));
                body.add("label");
            }
            case MILESTONED -> {
                body.add("added this to the");
                body.add(this.bold(event.milestone.title));
                body.add("milestone");
            }
            case DEMILESTONED -> {
                body.add("removed this from the");
                body.add(this.bold(event.milestone.title));
                body.add("milestone");
            }
            case CLOSED -> {
                body.add("closed this");
            }
            case OPENED -> body.add("opened this");
            default -> body.add(event.getType());
        }

        //escape : to properly get displayed in radicle web app
        body.add("on " + this.escape(MarkdownService.DTF.format(event.createdAt)));
        return String.join(" ", body);
    }

    public String getMetadata(GitLabComment comment) {
        var header = new ArrayList<String>();
        header.add("Number");
        header.add("Created On");
        header.add("Created By");

        var rows = new ArrayList<String>();
        rows.add(String.valueOf(comment.id));
        rows.add(this.escape(MarkdownService.DTF.format(comment.createdAt)));
        rows.add(this.link(comment.author.username, comment.author.webUrl));

        return new MarkdownService.MetadataBuilder()
                .summary(GitLabIssue.METADATA_TITLE)
                .createTable(header.toArray(new String[0]))
                .addTableRows(rows.toArray(new String[0]))
                .build();
    }

    public String getMetadata(GitLabIssue issue) {
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
        rows.add(this.link("#" + issue.iid, issue.webUrl));
        rows.add(this.escape(MarkdownService.DTF.format(issue.createdAt)));
        rows.add(this.link(issue.author.username, issue.author.webUrl));
        if (hasAssignees) {
            rows.add(issue.assignees.stream()
                    .map(a -> this.link(a.username, a.webUrl))
                    .collect(Collectors.joining(", ")));
        }
        if (hasMilestone) {
            rows.add(this.link(issue.milestone.title, issue.milestone.webUrl));
            if (issue.milestone.dueDate != null) {
                rows.add(this.escape(MarkdownService.DTF.format(Instant.parse(issue.milestone.dueDate + "T00:00:00Z"))));
            } else {
                rows.add("-");
            }
        }

        return new MarkdownService.MetadataBuilder()
                .summary(GitLabIssue.METADATA_TITLE)
                .createTable(headers.toArray(new String[0]))
                .addTableRows(rows.toArray(new String[0]))
                .build();
    }

    public String getMetadata(Timeline timeline) {
        if (timeline instanceof GitLabComment) {
            return this.getMetadata((GitLabComment) timeline);
        } else if (timeline instanceof GitLabEvent) {
            return this.getMetadata((GitLabEvent) timeline);
        }
        return "";
    }
}
