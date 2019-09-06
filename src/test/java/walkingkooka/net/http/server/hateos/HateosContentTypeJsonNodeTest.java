/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.net.http.server.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.net.header.MediaType;
import walkingkooka.tree.json.marshall.FromJsonNodeContext;
import walkingkooka.tree.json.marshall.FromJsonNodeContexts;
import walkingkooka.tree.json.marshall.ToJsonNodeContext;
import walkingkooka.tree.json.marshall.ToJsonNodeContexts;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HateosContentTypeJsonNodeTest extends HateosContentTypeTestCase<HateosContentTypeJsonNode> {

    @Test
    public void testWithNullFromJsonNodeContextFails() {
        this.withFails(null, this.toJsonNodeContext());
    }

    @Test
    public void testWithNullToJsonNodeContextFails() {
        this.withFails(this.fromJsonNodeContext(), null);
    }

    private void withFails(final FromJsonNodeContext fromJsonNodeContext,
                           final ToJsonNodeContext toJsonNodeContext) {
        assertThrows(NullPointerException.class, () -> {
            HateosContentTypeJsonNode.with(fromJsonNodeContext, toJsonNodeContext);
        });
    }

    @Test
    public void testFromNode() {
        final TestHateosResource resource = TestHateosResource.with(BigInteger.valueOf(123));
        this.fromNodeAndCheck(resource.toJsonNode(this.toJsonNodeContext()).toString(),
                TestHateosResource.class,
                resource);
    }

    @Test
    public void testToText() {
        this.toTextAndCheck(TestHateosResource.with(BigInteger.valueOf(123)),
                "{\n" +
                        "  \"id\": \"123\"\n" +
                        "}");
    }

    @Override
    HateosContentTypeJsonNode hateosContentType() {
        return HateosContentTypeJsonNode.with(this.fromJsonNodeContext(), this.toJsonNodeContext());
    }

    private FromJsonNodeContext fromJsonNodeContext() {
        return FromJsonNodeContexts.basic();
    }

    private ToJsonNodeContext toJsonNodeContext() {
        return ToJsonNodeContexts.basic();
    }

    @Override
    MediaType contentType() {
        return MediaType.parse("application/hal+json");
    }

    @Override
    String expectedToString() {
        return "JSON";
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HateosContentTypeJsonNode> type() {
        return HateosContentTypeJsonNode.class;
    }
}
