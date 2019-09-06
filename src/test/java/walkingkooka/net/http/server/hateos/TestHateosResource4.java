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
import walkingkooka.compare.Range;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeName;
import walkingkooka.tree.json.marshall.FromJsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.ToJsonNodeContext;
import walkingkooka.tree.xml.XmlDocument;
import walkingkooka.tree.xml.XmlName;
import walkingkooka.tree.xml.XmlNode;

import javax.xml.parsers.DocumentBuilderFactory;
import java.math.BigInteger;

public final class TestHateosResource4 extends FakeHateosResource<Range<BigInteger>> {

    static TestHateosResource4 with(final Range<BigInteger> id) {
        return new TestHateosResource4(id);
    }

    private TestHateosResource4(final Range<BigInteger> id) {
        super();
        this.id = id;
    }

    @Override
    public Range<BigInteger> id() {
        return this.id;
    }

    private final Range<BigInteger> id;

    @Override
    public String hateosLinkId() {
        return HasHateosLinkId.rangeHateosLinkId(this.id, (v) -> v.toString(16));
    }

    // JsonNodeContext...................................................................................................

    static TestHateosResource4 fromJsonNode(final JsonNode node,
                                            final FromJsonNodeContext context) {
        return with(context.fromJsonNodeWithType(node.objectOrFail().getOrFail(ID)));
    }

    JsonNode toJsonNode(final ToJsonNodeContext context) {
        return JsonNode.object()
                .set(ID, context.toJsonNodeWithType(this.id()));
    }

    private final static JsonNodeName ID = JsonNodeName.with("id");

    static {
        JsonNodeContext.register("test-HateosResource4",
                TestHateosResource4::fromJsonNode,
                TestHateosResource4::toJsonNode,
                TestHateosResource4.class);
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof TestHateosResource4 && equals0(Cast.to(other));
    }

    private boolean equals0(final TestHateosResource4 other) {
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}
