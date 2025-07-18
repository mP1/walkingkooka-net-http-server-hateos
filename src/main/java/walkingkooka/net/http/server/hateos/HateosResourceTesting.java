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


import walkingkooka.reflect.ClassTesting2;

import java.util.Optional;

/**
 * Mixin interface for testing {@link HateosResource}
 */
public interface HateosResourceTesting<H extends HateosResource<I>, I> extends ClassTesting2<H> {

    H createHateosResource();

    default void hateosLinkIdAndCheck(final String expected) {
        this.hateosLinkIdAndCheck(this.createHateosResource(), expected);
    }

    default void hateosLinkIdAndCheck(final HateosResource<?> resource, final String expected) {
        this.checkEquals(expected,
            resource.hateosLinkId(),
            () -> resource + " hateosLinkId");
    }

    default void idAndCheck(final Optional<I> expected) {
        this.idAndCheck(
            this.createHateosResource(),
            expected
        );
    }

    default void idAndCheck(final HateosResource<?> resource,
                            final Optional<I> expected) {
        this.checkEquals(
            expected,
            resource.id(),
            () -> resource + " id"
        );
    }
}
