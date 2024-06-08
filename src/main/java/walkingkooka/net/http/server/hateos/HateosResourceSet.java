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

import walkingkooka.collect.set.Sets;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A read-only {@link Set} of {@link HateosResource}.
 */
public interface HateosResourceSet<H extends HateosResource<I>, I> extends Set<H> {

    /**
     * Getter that returns all non null ids.
     */
    default Set<I> ids() {
        return Sets.readOnly(
                this.stream()
                        .map(h -> h.id().orElse(null)) // flatMap gives errors in INTELLIJ
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(Sets::sorted))
        );
    }
}
