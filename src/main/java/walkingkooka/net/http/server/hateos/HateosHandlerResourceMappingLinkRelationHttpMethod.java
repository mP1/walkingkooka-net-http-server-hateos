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

import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.test.HashCodeEqualsDefined;

import java.util.Objects;

/**
 * Used as a key to locate mappings using a {@link LinkRelation} and a {@link HttpMethod}.
 */
final class HateosHandlerResourceMappingLinkRelationHttpMethod implements HashCodeEqualsDefined, Comparable<HateosHandlerResourceMappingLinkRelationHttpMethod> {

    static HateosHandlerResourceMappingLinkRelationHttpMethod with(final LinkRelation<?> relation,
                                                                   final HttpMethod method) {
        Objects.requireNonNull(relation, "relation");
        Objects.requireNonNull(method, "method");

        return new HateosHandlerResourceMappingLinkRelationHttpMethod(relation, method);
    }

    private HateosHandlerResourceMappingLinkRelationHttpMethod(final LinkRelation<?> relation,
                                                               final HttpMethod method) {
        super();
        this.relation = relation;
        this.method = method;
    }

    final LinkRelation<?> relation;
    final HttpMethod method;

    // HashCodeEqualsDefined ...........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.method, this.relation);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof HateosHandlerResourceMappingLinkRelationHttpMethod && this.equals0((HateosHandlerResourceMappingLinkRelationHttpMethod) other);
    }

    private boolean equals0(final HateosHandlerResourceMappingLinkRelationHttpMethod other) {
        return this.compareTo(other) == 0;
    }

    /**
     * Dumps the resource name and link relation
     */
    @Override
    public String toString() {
        return this.relation + " " + this.method;
    }

    // Comparable........................................................................................................

    @Override
    public int compareTo(final HateosHandlerResourceMappingLinkRelationHttpMethod other) {
        int result = this.method.compareTo(other.method);
        if (0 == result) {
            result = this.relation.compareTo(other.relation);
        }
        return result;
    }
}
