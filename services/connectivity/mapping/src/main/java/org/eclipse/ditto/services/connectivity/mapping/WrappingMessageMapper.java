/*
 * Copyright (c) 2017 Bosch Software Innovations GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/index.php
 *
 * Contributors:
 *    Bosch Software Innovations GmbH - initial contribution
 */
package org.eclipse.ditto.services.connectivity.mapping;

import static org.eclipse.ditto.model.base.common.ConditionChecker.checkNotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.model.base.headers.DittoHeadersBuilder;
import org.eclipse.ditto.model.connectivity.ConnectivityModelFactory;
import org.eclipse.ditto.model.connectivity.ExternalMessage;
import org.eclipse.ditto.model.connectivity.ExternalMessageBuilder;
import org.eclipse.ditto.model.connectivity.MessageMappingFailedException;
import org.eclipse.ditto.protocoladapter.Adaptable;
import org.eclipse.ditto.protocoladapter.ProtocolFactory;

/**
 * Does wrap any {@link MessageMapper} and adds content type checks
 * to mapping methods. It allows to override the mappers content type by any content type.
 * <p>
 * Also adds headers to ExternalMessage and Adaptable in mappings even when the wrapped {@link MessageMapper} does
 * forget to do so by himself.
 * </p>
 */
final class WrappingMessageMapper implements MessageMapper {


    private final MessageMapper delegate;
    @Nullable
    private final String contentTypeOverride;
    private final boolean enforceMatchingContentType;

    private WrappingMessageMapper(final MessageMapper delegate, final boolean enforceMatchingContentType,
            @Nullable final String contentTypeOverride) {
        this.delegate = checkNotNull(delegate);
        this.enforceMatchingContentType = enforceMatchingContentType;
        this.contentTypeOverride = contentTypeOverride;
    }

    /**
     * Enforces content type checking for the mapper
     *
     * @param enforceMatchingContentType whether to only let pass ExternalMessages having a to {@link #getContentType()}
     * matching contentType
     * @param mapper the mapper
     * @return the wrapped mapper
     */
    public static MessageMapper wrap(final MessageMapper mapper, final boolean enforceMatchingContentType) {
        return new WrappingMessageMapper(mapper, enforceMatchingContentType, null);
    }

    /**
     * Enforces content type checking for the mapper and overrides the content type.
     *
     * @param mapper the mapper
     * @param contentTypeOverride the content type substitution
     * @return the wrapped mapper
     */
    public static MessageMapper wrap(final MessageMapper mapper, final String contentTypeOverride) {
        return new WrappingMessageMapper(mapper, true, contentTypeOverride);
    }

    @Override
    public String getContentType() {
        return Objects.nonNull(contentTypeOverride) ? contentTypeOverride : delegate.getContentType();
    }

    @Override
    public void configure(final MessageMapperConfiguration configuration) {
        delegate.configure(configuration);
    }

    @Override
    public Optional<Adaptable> map(final ExternalMessage message) {
        final DittoHeaders dittoHeaders = DittoHeaders.of(message.getHeaders());
        final DittoHeaders dittoHeadersEnhanced;

        // if no correlation-id was provided in the ExternalMessage, generate one here:
        if (!dittoHeaders.getCorrelationId().isPresent()) {
            // generate correlation-id if none was provided
            dittoHeadersEnhanced = dittoHeaders.toBuilder().correlationId(UUID.randomUUID().toString()).build();
        } else {
            dittoHeadersEnhanced = dittoHeaders;
        }

        final String actualContentType = MessageMapper.findContentType(message)
                .orElseThrow(MessageMappingFailedException.newBuilder(MessageMapper.findContentType(message).orElse(""))
                        .dittoHeaders(dittoHeadersEnhanced)::build);

        requireMatchingContentType(actualContentType, dittoHeadersEnhanced);
        final Optional<Adaptable> mappedOpt = delegate.map(message);

        return mappedOpt.map(mapped -> {
            final DittoHeadersBuilder headersBuilder = dittoHeadersEnhanced.toBuilder();

            final Optional<DittoHeaders> headersOpt = mapped.getHeaders();
            headersOpt.ifPresent(headersBuilder::putHeaders); // overwrite with mapped headers (if any)

            return ProtocolFactory.newAdaptableBuilder(mapped)
                    .withHeaders(headersBuilder.build())
                    .build();
        });
    }

    @Override
    public Optional<ExternalMessage> map(final Adaptable adaptable) {
        final DittoHeaders dittoHeaders = adaptable.getHeaders().orElseGet(DittoHeaders::empty);

        final String actualContentType = MessageMapper.findContentType(adaptable)
                .orElseThrow(() -> MessageMappingFailedException.newBuilder(MessageMapper.findContentType(adaptable).orElse(""))
                        .dittoHeaders(dittoHeaders)
                        .build());

        requireMatchingContentType(actualContentType, dittoHeaders);
        final Optional<ExternalMessage> mappedOpt = delegate.map(adaptable);

        return mappedOpt.map(mapped -> {

            final ExternalMessageBuilder messageBuilder = ConnectivityModelFactory.newExternalMessageBuilder(mapped);
            adaptable.getHeaders().ifPresent(adaptableHeaders -> {
                messageBuilder.withAdditionalHeaders(adaptableHeaders);
                messageBuilder.withAdditionalHeaders(mapped.getHeaders());
            });
            messageBuilder.withAdditionalHeaders(ExternalMessage.CONTENT_TYPE_HEADER, getContentType());

            messageBuilder.asResponse(adaptable.getPayload().getStatus().isPresent());

            return messageBuilder.build();
        });
    }

    private void requireMatchingContentType(final String actualContentType,
            final DittoHeaders dittoHeaders) {

        if (enforceMatchingContentType) {
            if (!getContentType().equalsIgnoreCase(actualContentType)) {
                throw MessageMappingFailedException.newBuilder(actualContentType).dittoHeaders(dittoHeaders).build();
            }
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WrappingMessageMapper that = (WrappingMessageMapper) o;
        return Objects.equals(delegate, that.delegate) &&
                Objects.equals(contentTypeOverride, that.contentTypeOverride);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate, contentTypeOverride);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "delegate=" + delegate +
                ", contentTypeOverride=" + contentTypeOverride +
                "]";
    }
}
