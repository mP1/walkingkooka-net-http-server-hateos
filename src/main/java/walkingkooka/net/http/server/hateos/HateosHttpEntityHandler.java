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

import walkingkooka.collect.Range;
import walkingkooka.net.http.HttpEntity;

import java.util.Objects;
import java.util.Set;

/**
 * Handles a HATEOS request for a one or more {@link HateosResource} as a single {@link walkingkooka.net.http.HttpEntity}.
 */
public interface HateosHttpEntityHandler<I extends Comparable<I>> {

    /**
     * Handles a request for a resource.
     * <pre>
     * /resource/*
     * </pre>>
     */
    HttpEntity handleAll(final HttpEntity entity);

    /**
     * Handles a collection of resources identified by the given Ids
     * <pre>
     * /resource/1,20,300
     * </pre>>
     */
    HttpEntity handleMany(final Set<I> ids,
                          final HttpEntity entity);

    /**
     * Handles a resources identified by the given id
     * <pre>
     * /resource/123
     * </pre>>
     */
    HttpEntity handleOne(final I id,
                         final HttpEntity entity);

    /**
     * Handles a collection of resources without any Id
     * <pre>
     * /resource
     * </pre>>
     */
    HttpEntity handleNone(final HttpEntity entity);

    /**
     * Handles a collection of resources identified by the given Ids
     * <pre>
     * /resource/12-34
     * </pre>>
     */
    HttpEntity handleRange(final Range<I> range,
                           final HttpEntity entity);

    // parameter checkers...............................................................................................

    /**
     * Complains if the id is null.
     */
    static <I extends Comparable<I>> I checkId(final I id) {
        return Objects.requireNonNull(id, "id");
    }

    /**
     * Complains if the {@link Set} is null.
     */
    static <I extends Comparable<I>> Set<I> checkManyIds(final Set<I> ids) {
        return Objects.requireNonNull(ids, "ids");
    }

    /**
     * Complains if the {@link Range} is null.
     */
    static <I extends Comparable<I>> Range<I> checkIdRange(final Range<I> range) {
        return Objects.requireNonNull(range, "range");
    }

    /**
     * Complains if the {@link HttpEntity} is null.
     */
    static HttpEntity checkHttpEntity(final HttpEntity entity) {
        return Objects.requireNonNull(entity, "entity");
    }
}
