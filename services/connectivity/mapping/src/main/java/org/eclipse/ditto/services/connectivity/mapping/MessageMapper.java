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

import java.util.Map;
import java.util.Optional;

import org.eclipse.ditto.model.connectivity.ExternalMessage;
import org.eclipse.ditto.model.connectivity.MessageMapperConfigurationInvalidException;
import org.eclipse.ditto.protocoladapter.Adaptable;

/**
 * Defines a message mapper which maps a {@link org.eclipse.ditto.model.connectivity.ExternalMessage} to a
 * {@link org.eclipse.ditto.protocoladapter.Adaptable} and vice versa.
 * <p>
 * Usually a mapper is bound to a content type.
 * </p>
 * A message mapper is considered to be dynamically instantiated at runtime, it therefore can only be configured at
 * runtime.
 */
public interface MessageMapper {

    /**
     * Returns the content type of this mapper. This can be used as a hint for mapper selection.
     *
     * @return the content type
     */
    String getContentType();

    /**
     * Applies configuration for this MessageMapper by first validating for required configuration items.
     * <p>
     * Overwrite this method if you want to apply further configuration validation but think about calling
     * super.configureWithValidation() as this one validates the existence of the Content-Type which is a mandatory
     * configuration property.
     * </p>
     *
     * @param configuration the configuration to apply and validate
     * @throws MessageMapperConfigurationInvalidException if configuration is invalid
     * @throws org.eclipse.ditto.model.connectivity.MessageMapperConfigurationFailedException if the configuration failed
     * for a mapper specific reason
     */
    default void configureWithValidation(final MessageMapperConfiguration configuration) {
        if (!configuration.findContentType().isPresent()) {
            throw MessageMapperConfigurationInvalidException
                    .newBuilder(ExternalMessage.CONTENT_TYPE_HEADER)
                    .build();
        }
        configure(configuration);
    }

    /**
     * Applies configuration for this MessageMapper.
     *
     * @param configuration the configuration to apply
     * @throws MessageMapperConfigurationInvalidException if configuration is invalid
     * @throws org.eclipse.ditto.model.connectivity.MessageMapperConfigurationFailedException if the configuration failed
     * for a mapper specific reason
     */
    void configure(MessageMapperConfiguration configuration);

    /**
     * Maps an {@link org.eclipse.ditto.model.connectivity.ExternalMessage} to an {@link org.eclipse.ditto.protocoladapter.Adaptable}
     *
     * @param message the ExternalMessage to map
     * @return the mapped Adaptable or an empty Optional if the ExternalMessage should not be mapped after all
     * @throws org.eclipse.ditto.model.connectivity.MessageMappingFailedException if the given message can not be mapped
     * @throws org.eclipse.ditto.model.base.exceptions.DittoRuntimeException if anything during Ditto Adaptable creation
     * went wrong
     */
    Optional<Adaptable> map(ExternalMessage message);

    /**
     * Maps an {@link org.eclipse.ditto.protocoladapter.Adaptable} to an {@link org.eclipse.ditto.model.connectivity.ExternalMessage}
     *
     * @param adaptable the Adaptable to map
     * @return the ExternalMessage or an empty Optional if the Adaptable should not be mapped after all
     * @throws org.eclipse.ditto.model.connectivity.MessageMappingFailedException if the given adaptable can not be mapped
     */
    Optional<ExternalMessage> map(Adaptable adaptable);

    /**
     * Finds the content-type header from the passed ExternalMessage.
     *
     * @param externalMessage the ExternalMessage to look for the content-type header in
     * @return the optional content-type value
     */
    static Optional<String> findContentType(final ExternalMessage externalMessage) {
        checkNotNull(externalMessage);
        return externalMessage.findHeaderIgnoreCase(ExternalMessage.CONTENT_TYPE_HEADER);
    }

    /**
     * Finds the content-type header from the passed Adaptable.
     *
     * @param adaptable the Adaptable to look for the content-type header in
     * @return the optional content-type value
     */
    static Optional<String> findContentType(final Adaptable adaptable) {
        checkNotNull(adaptable);
        return adaptable.getHeaders().map(h -> h.entrySet().stream()
                .filter(e -> ExternalMessage.CONTENT_TYPE_HEADER.equalsIgnoreCase(e.getKey()))
                .findFirst()
                .map(Map.Entry::getValue).orElse(null));
    }
}
