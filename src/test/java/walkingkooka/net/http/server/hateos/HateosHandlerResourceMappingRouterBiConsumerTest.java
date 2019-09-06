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
import walkingkooka.tree.json.marshall.FromJsonNodeContexts;
import walkingkooka.tree.json.marshall.ToJsonNodeContexts;

import java.util.function.BiConsumer;

public final class HateosHandlerResourceMappingRouterBiConsumerTest extends HateosHandlerResourceMappingTestCase2<HateosHandlerResourceMappingRouterBiConsumer> {

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final HateosHandlerResourceMappingRouter router = HateosHandlerResourceMappingRouter.with(Url.parseAbsolute("http://example.com"),
                HateosContentType.json(FromJsonNodeContexts.fake(), ToJsonNodeContexts.fake()),
                Sets.empty());

        this.toStringAndCheck(HateosHandlerResourceMappingRouterBiConsumer.with(router),
                router.toString());
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<HateosHandlerResourceMappingRouterBiConsumer> type() {
        return HateosHandlerResourceMappingRouterBiConsumer.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosHandlerResourceMapping.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return BiConsumer.class.getSimpleName();
    }
}
