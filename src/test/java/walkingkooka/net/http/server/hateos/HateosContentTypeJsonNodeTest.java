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
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.BigInteger;
import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HateosContentTypeJsonNodeTest extends HateosContentTypeTestCase<HateosContentTypeJsonNode> {

    @Test
    public void testWithNullJsonNodeUnmarshallContextFails() {
        this.withFails(null, this.marshallContext());
    }

    @Test
    public void testWithNullJsonNodeMarshallContextFails() {
        this.withFails(this.unmarshallContext(), null);
    }

    private void withFails(final JsonNodeUnmarshallContext unmarshallContext,
                           final JsonNodeMarshallContext marshallContext) {
        assertThrows(NullPointerException.class, () -> {
            HateosContentTypeJsonNode.with(unmarshallContext, marshallContext);
        });
    }

    @Test
    public void testFromNode() {
        final TestHateosResource resource = TestHateosResource.with(BigInteger.valueOf(123));
        this.fromNodeAndCheck(resource.marshall(this.marshallContext()).toString(),
                TestHateosResource.class,
                resource);
    }

    @Test
    public void testToTextWithIndentation() {
        this.toTextAndCheck(
                TestHateosResource.with(BigInteger.valueOf(123)),
                "{\n" +
                        "  \"id\": \"123\"\n" +
                        "}"
        );
    }

    @Test
    public void testToTextWithoutIndentationLineEndingNone() {
        this.toTextAndCheck(
                TestHateosResource.with(BigInteger.valueOf(123)),
                Indentation.EMPTY,
                LineEnding.NONE,
                "{" +
                        "\"id\": \"123\"" +
                        "}"
        );
    }

    @Override
    HateosContentTypeJsonNode hateosContentType() {
        return HateosContentTypeJsonNode.with(this.unmarshallContext(), this.marshallContext());
    }

    private JsonNodeUnmarshallContext unmarshallContext() {
        return JsonNodeUnmarshallContexts.basic(
                ExpressionNumberKind.DEFAULT,
                MathContext.DECIMAL32
        );
    }

    private JsonNodeMarshallContext marshallContext() {
        return JsonNodeMarshallContexts.basic();
    }

    @Override
    MediaType contentType() {
        return MediaType.parse("application/json");
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
