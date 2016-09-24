package org.owasp.validator.html.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * @author Kristian Rosenvold
 */
public class TagTest {
    Attribute attribute1 = new Attribute("foo", Arrays.asList(Pattern.compile("abc")), Arrays.asList("v1"), "valid", "desc1");
    Attribute attribute2 = new Attribute("goo", Arrays.asList(Pattern.compile("bbc")), Arrays.asList("vb1"), "bvalid", "bdesc1");

    @Test
    public void testSimpleRegularExpression() throws Exception {
        Map<String, Attribute> attrs = new HashMap<String, Attribute>();
        attrs.put("a1", attribute1);
        Tag tag = new Tag("foo", attrs, "fud");
        assertEquals("<(\\s)*foo(\\s)*(foo(\\s)*=(\\s)*\"(v1|abc)\"(\\s)*)*(\\s)*>", tag.getRegularExpression());

    }

    @Test
    public void testGetRegularExpression() throws Exception {
        Map<String, Attribute> attrs = new HashMap<String, Attribute>();
        attrs.put("a1", attribute1);
        attrs.put("a2", attribute2);
        Tag tag = new Tag("foo", attrs, "fud");
        assertEquals("<(\\s)*foo(\\s)*(foo(\\s)*=(\\s)*\"(v1|abc)\"(\\s)*|goo(\\s)*=(\\s)*\"(vb1|bbc)\"(\\s)*)*(\\s)*>",
                tag.getRegularExpression());

    }
}
