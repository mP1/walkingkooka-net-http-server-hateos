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

package test;

import walkingkooka.Cast;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

/**
 * A simple container for the actual {@link HateosResource}.
 */
public final class TestResource2 {

    static TestResource2 with(final Object value) {
        return new TestResource2(value);
    }

    private TestResource2(final Object value) {
        super();
        this.value = value;
    }

    final Object value;

    // JsonNodeContext...................................................................................................

    static TestResource2 unmarshall(final JsonNode node,
                                    final JsonNodeUnmarshallContext context) {
        return with(context.unmarshallWithType(node));
    }

    JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallWithType(this.value);
    }

    static {
        JsonNodeContext.register("testResource2",
                TestResource2::unmarshall,
                TestResource2::marshall,
                TestResource2.class);
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof TestResource2 && equals0(Cast.to(other));
    }

    private boolean equals0(final TestResource2 other) {
        return this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
