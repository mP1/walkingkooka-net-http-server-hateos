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
import walkingkooka.collect.set.SetTesting2;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface HateosResourceSetTesting<S extends Set<H>, H extends HateosResource<I>, I> extends SetTesting2<S, H>,
        JsonNodeMarshallingTesting<S> {

    @Test
    default void testReadOnly() {
        final S set = this.createSet();
        this.isEmptyAndCheck(set, false);

        assertThrows(
                UnsupportedOperationException.class,
                () -> set.clear()
        );
    }
}
