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
import walkingkooka.net.http.server.HttpRequestAttribute;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A selection which may be nothing, a single item, a range or list.
 */
public abstract class HateosResourceSelection<I extends Comparable<I>> {

    /**
     * {@see HateosResourceSelectionAll}
     */
    public static <I extends Comparable<I>> HateosResourceSelection<I> all() {
        return HateosResourceSelectionAll.instance();
    }

    /**
     * {@see HateosResourceSelectionList}
     */
    public static <I extends Comparable<I>> HateosResourceSelection<I> list(final List<I> value) {
        return HateosResourceSelectionList.with(value);
    }

    /**
     * {@see HateosResourceSelectionNone}
     */
    public static <I extends Comparable<I>> HateosResourceSelection<I> none() {
        return HateosResourceSelectionNone.instance();
    }

    /**
     * {@see HateosResourceSelectionOne}
     */
    public static <I extends Comparable<I>> HateosResourceSelection<I> one(final I value) {
        return HateosResourceSelectionOne.with(value);
    }

    /**
     * {@see HateosResourceSelectionRange}
     */
    public static <I extends Comparable<I>> HateosResourceSelection<I> range(final Range<I> value) {
        return HateosResourceSelectionRange.with(value);
    }

    /**
     * Package private to limit sub classing.
     */
    HateosResourceSelection() {
        super();
    }

    /**
     * Only {@link HateosResourceSelectionAll} returns true.
     */
    public final boolean isAll() {
        return this instanceof HateosResourceSelectionAll;
    }

    /**
     * Only {@link HateosResourceSelectionList} returns true.
     */
    public final boolean isList() {
        return this instanceof HateosResourceSelectionList;
    }

    /**
     * Only {@link HateosResourceSelectionNone} returns true.
     */
    public final boolean isNone() {
        return this instanceof HateosResourceSelectionNone;
    }

    /**
     * Only {@link HateosResourceSelectionOne} returns true.
     */
    public final boolean isOne() {
        return this instanceof HateosResourceSelectionOne;
    }

    /**
     * Only {@link HateosResourceSelectionRange} returns true.
     */
    public final boolean isRange() {
        return this instanceof HateosResourceSelectionRange;
    }

    /**
     * Returns the {@link Class type} for this resource.
     */
    final Class<?> resourceType(final HateosResourceMapping<?, ?, ?, ?> mapping) {
        return this.isNone() || this.isOne() ?
                mapping.valueType :
                mapping.collectionType;
    }

    abstract Optional<?> dispatch(final HateosHandler<I, ?, ?> handler,
                                  final Optional<?> resource,
                                  final Map<HttpRequestAttribute<?>, Object> parameters);

    @Override
    abstract public String toString();
}
