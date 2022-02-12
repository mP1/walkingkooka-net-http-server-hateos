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
import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.util.function.BiConsumer;

public final class HateosResourceMappingRouterBiConsumerTest extends HateosResourceMappingTestCase2<HateosResourceMappingRouterBiConsumer> {

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final Indentation indentation = Indentation.with("   ");
        final LineEnding lineEnding = LineEnding.NL;

        final HateosResourceMappingRouter router = HateosResourceMappingRouter.with(
                Url.parseAbsolute("http://example.com"),
                HateosContentType.json(JsonNodeUnmarshallContexts.fake(), JsonNodeMarshallContexts.fake()),
                Sets.empty(),
                indentation,
                lineEnding
        );

        this.toStringAndCheck(
                HateosResourceMappingRouterBiConsumer.with(router, indentation, lineEnding),
                router.toString()
        );
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<HateosResourceMappingRouterBiConsumer> type() {
        return HateosResourceMappingRouterBiConsumer.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosResourceMapping.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return BiConsumer.class.getSimpleName();
    }
}
