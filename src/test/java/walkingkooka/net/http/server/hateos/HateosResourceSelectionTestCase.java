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

import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.IsMethodTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.function.Predicate;

public abstract class HateosResourceSelectionTestCase<S extends HateosResourceSelection<I>, I extends Comparable<I>> implements ClassTesting<S>,
        IsMethodTesting<S>,
        ToStringTesting<S> {

    HateosResourceSelectionTestCase() {
        super();
    }

    abstract S createHateosResourceSelection();

    // ClassTesting......................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // IsMethodTesting..................................................................................................

    @Override
    public final S createIsMethodObject() {
        return this.createHateosResourceSelection();
    }

    @Override
    public final Predicate<String> isMethodIgnoreMethodFilter() {
        return (m) -> true;
    }

    @Override
    public final String toIsMethodName(final String typeName) {
        return this.toIsMethodNameWithPrefixSuffix(
                typeName,
                HateosResourceSelection.class.getSimpleName(), // drop-prefix
                "" // drop-suffix
        );
    }
}
