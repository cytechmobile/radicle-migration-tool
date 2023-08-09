package network.radicle.tools.github.core.radicle.actions;

import network.radicle.tools.github.core.github.IssueTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class CommentActionTest {
    private static final Logger logger = LoggerFactory.getLogger(CommentActionTest.class);

    @Test
    public void testSerializationOfSingleCommentAction() throws Exception {
        var action = generateRadicleCommentAction();
        var json = IssueTest.MAPPER.writeValueAsString(action);
        var a = IssueTest.MAPPER.readValue(json, CommentAction.class);

        assertThat(a).isNotNull().usingRecursiveComparison().isEqualTo(action);
    }

    public static CommentAction generateRadicleCommentAction() {
        try {
            var seed = System.currentTimeMillis();
            var file = new File("src/test/resources/radicle/actions/comment.json");
            var action = IssueTest.MAPPER.readValue(file, CommentAction.class);
            action.body =  "" + seed;
            return action;
        } catch (Exception ex) {
            logger.error("caught error", ex);
            return null;
        }
    }

}