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

import walkingkooka.compare.Range;

import java.util.function.Function;

/**
 * Implemented by {@link HateosResource} that know how to make the id in link form.
 */
public interface HasHateosLinkId {

    /**
     * Accepts a {@link Range} and converts it into a {@link String}.
     */
    static <I extends Comparable<I>> String rangeHateosLinkId(final Range<I> range,
                                                              final Function<I, String> hateosLinkId) {
        return HasHateosLinkIdRangeVisitor.hateosLinkId(range, hateosLinkId);
    }

    /**
     * This character should be used to separate values within a {@link Range}.
     */
    char HATEOS_LINK_RANGE_SEPARATOR = '-';

    /**
     * Formats the id ready to appear within a hateos link. If a {@link Range} it should use the separator
     * character given. This also assumes that special characters are escaped as necessary.
     */
    String hateosLinkId();
}
