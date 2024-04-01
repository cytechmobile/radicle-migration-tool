package network.radicle.tools.migrate.services.github;

import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import network.radicle.tools.migrate.core.Timeline;
import network.radicle.tools.migrate.core.github.GitHubComment;
import network.radicle.tools.migrate.core.github.GitHubEvent;
import network.radicle.tools.migrate.core.github.GitHubIssue;
import network.radicle.tools.migrate.services.MarkdownService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.stream.Collectors;

@ApplicationScoped
public class GitHubMarkdownService extends MarkdownService {

    public String getMetadata(GitHubEvent event) {
        var body = new ArrayList<String>();

        //actor profile link
        if (!GitHubEvent.Type.CROSS_REFERENCED.value.equals(event.event)) {
            body.add(this.link(this.bold(event.actor.login), event.actor.htmlUrl));
        }

        switch (GitHubEvent.Type.from(event.event)) {
            case ASSIGNED, UNASSIGNED -> {
                body.add(event.event);
                body.add(this.link(this.bold(event.assignee.login), event.assignee.htmlUrl));
            }
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
                if (!Strings.isNullOrEmpty(event.stateReason)) {
                    body.add("as " + event.stateReason);
                }
                if (event.commit != null) {
                    var message = event.commit.metadata.message.split("\n")[0];
                    body.add("in");
                    body.add(event.commit.sha);
                    body.add(this.link(this.bold(message), event.commit.htmlUrl));
                }
            }
            case REOPENED -> body.add("reopened this");
            case RENAMED -> {
                body.add("changed the title");
                body.add(this.strikethrough(event.rename.from));
                body.add(this.bold(event.rename.to));
            }
            case CROSS_REFERENCED -> {
                body.add("This was referenced by");
                body.add(this.link(this.bold(event.source.issue.title + " #" + event.source.issue.number),
                        event.source.issue.htmlUrl));
            }
            case MENTIONED -> body.add("was mentioned");
            case REFERENCED -> {
                body.add("added the commit");
                body.add(event.commit.sha);
                var message = event.commit.metadata.message.split("\n")[0];
                body.add(this.link(this.bold(message), event.commit.htmlUrl));
                body.add("that referenced this issue");
            }
            default -> body.add(event.event);
        }

        //escape : to properly get displayed in radicle web app
        body.add("on " + this.escape(MarkdownService.DTF.format(event.createdAt)));
        return String.join(" ", body);
    }

    public String getMetadata(GitHubComment comment) {
        var header = new ArrayList<String>();
        header.add("Number");
        header.add("Created On");
        header.add("Created By");

        var rows = new ArrayList<String>();
        rows.add(this.link(String.valueOf(comment.id), comment.htmlUrl));
        rows.add(this.escape(MarkdownService.DTF.format(comment.createdAt)));
        rows.add(this.link(comment.user.login, comment.user.htmlUrl));

        return new MarkdownService.MetadataBuilder()
                .summary(GitHubIssue.METADATA_TITLE)
                .createTable(header.toArray(new String[0]))
                .addTableRows(rows.toArray(new String[0]))
                .build();
    }

    public String getMetadata(GitHubIssue issue) {
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
        rows.add(this.link("#" + issue.number, issue.htmlUrl));
        rows.add(this.escape(MarkdownService.DTF.format(issue.createdAt)));
        rows.add(this.link(issue.user.login, issue.user.htmlUrl));
        if (hasAssignees) {
            rows.add(issue.assignees.stream()
                    .map(a -> this.link(a.login, a.htmlUrl))
                    .collect(Collectors.joining(", ")));
        }
        if (hasMilestone) {
            rows.add(this.link(issue.milestone.title, issue.milestone.htmlUrl));
            if (issue.milestone.dueOn != null) {
                rows.add(this.escape(MarkdownService.DTF.format(Instant.parse(issue.milestone.dueOn))));
            } else {
                rows.add("-");
            }
        }

        return new MarkdownService.MetadataBuilder()
                .summary(GitHubIssue.METADATA_TITLE)
                .createTable(headers.toArray(new String[0]))
                .addTableRows(rows.toArray(new String[0]))
                .build();
    }

    public String getMetadata(Timeline timeline) {
        if (timeline instanceof GitHubComment) {
            return this.getMetadata((GitHubComment) timeline);
        } else if (timeline instanceof GitHubEvent) {
            return this.getMetadata((GitHubEvent) timeline);
        }
        return "";
    }
}
