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
import walkingkooka.net.http.server.HttpRequestAttribute;

import java.util.Map;
import java.util.Set;

public class FakeHateosHttpEntityHandler<I extends Comparable<I>, X extends HateosResourceHandlerContext> implements HateosHttpEntityHandler<I, X> {
    @Override
    public HttpEntity handleAll(final HttpEntity entity,
                                final Map<HttpRequestAttribute<?>, Object> parameters,
                                final X context) {
        HateosHttpEntityHandler.checkHttpEntity(entity);
        HateosHttpEntityHandler.checkParameters(parameters);
        HateosHttpEntityHandler.checkContext(context);

        throw new UnsupportedOperationException();
    }

    @Override
    public HttpEntity handleMany(final Set<I> ids,
                                 final HttpEntity entity,
                                 final Map<HttpRequestAttribute<?>, Object> parameters,
                                 final X context) {
        HateosHttpEntityHandler.checkManyIds(ids);
        HateosHttpEntityHandler.checkHttpEntity(entity);
        HateosHttpEntityHandler.checkParameters(parameters);
        HateosHttpEntityHandler.checkContext(context);

        throw new UnsupportedOperationException();
    }

    @Override
    public HttpEntity handleOne(final I id,
                                final HttpEntity entity,
                                final Map<HttpRequestAttribute<?>, Object> parameters,
                                final X context) {
        HateosHttpEntityHandler.checkId(id);
        HateosHttpEntityHandler.checkHttpEntity(entity);
        HateosHttpEntityHandler.checkParameters(parameters);
        HateosHttpEntityHandler.checkContext(context);

        throw new UnsupportedOperationException();
    }

    @Override
    public HttpEntity handleNone(final HttpEntity entity,
                                 final Map<HttpRequestAttribute<?>, Object> parameters,
                                 final X context) {
        HateosHttpEntityHandler.checkHttpEntity(entity);
        HateosHttpEntityHandler.checkParameters(parameters);
        HateosHttpEntityHandler.checkContext(context);

        throw new UnsupportedOperationException();
    }

    @Override
    public HttpEntity handleRange(final Range<I> range,
                                  final HttpEntity entity,
                                  final Map<HttpRequestAttribute<?>, Object> parameters,
                                  final X context) {
        HateosHttpEntityHandler.checkIdRange(range);
        HateosHttpEntityHandler.checkHttpEntity(entity);
        HateosHttpEntityHandler.checkParameters(parameters);
        HateosHttpEntityHandler.checkContext(context);

        throw new UnsupportedOperationException();
    }
}
