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

import walkingkooka.Cast;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.math.BigInteger;
import java.util.Optional;

/**
 * The id type is {@link BigInteger} just to be different from {@link String}.
 */
public final class TestHateosResource2 extends FakeHateosResource<BigInteger> {

    static TestHateosResource2 with(final BigInteger id) {
        return new TestHateosResource2(id);
    }

    private TestHateosResource2(final BigInteger id) {
        super();
        this.id = id;
    }

    @Override
    public Optional<BigInteger> id() {
        return Optional.ofNullable(this.id);
    }

    private final BigInteger id;

    @Override
    public String hateosLinkId() {
        return Integer.toHexString(this.id.intValueExact());
    }

    // JsonNodeContext...................................................................................................

    static TestHateosResource2 unmarshall(final JsonNode node,
                                          final JsonNodeUnmarshallContext context) {
        return with(context.unmarshall(node.objectOrFail().getOrFail(ID), BigInteger.class));
    }

    JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.object()
                .set(ID, context.marshall(this.id));
    }

    private final static JsonPropertyName ID = JsonPropertyName.with("id");

    static {
        JsonNodeContext.register("test-HateosResource3",
                TestHateosResource2::unmarshall,
                TestHateosResource2::marshall,
                TestHateosResource2.class);
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.id().hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof TestHateosResource2 && equals0(Cast.to(other));
    }

    private boolean equals0(final TestHateosResource2 other) {
        return this.id().equals(other.id());
    }

    @Override
    public String toString() {
        return this.id().toString();
    }
}
