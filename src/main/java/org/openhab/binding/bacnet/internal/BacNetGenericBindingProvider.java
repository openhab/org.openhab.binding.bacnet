/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.bacnet.internal;

import java.util.Collection;

import org.code_house.bacnet4j.wrapper.api.Type;
import org.openhab.binding.bacnet.BacNetBindingProvider;
import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BacNetGenericBindingProvider extends AbstractGenericBindingProvider implements BacNetBindingProvider {
    static final Logger logger = LoggerFactory.getLogger(BacNetGenericBindingProvider.class);

    @Override
    public String getBindingType() {
        return "bacnet";
    }

    @Override
    public void validateItemType(Item item, String bindingConfig) throws BindingConfigParseException {

    }

    @Override
    public void processBindingConfiguration(String context, Item item, String bindingConfig)
            throws BindingConfigParseException {
        super.processBindingConfiguration(context, item, bindingConfig);

        if (bindingConfig != null) {
            BacNetBindingConfig config = BacNetBindingConfig.parseBindingConfig(item.getName(), item.getClass(),
                    bindingConfig);
            addBindingConfig(item, config);
        } else {
            logger.warn("bindingConfig is NULL (item=" + item + ") -> process bindingConfig aborted!");
        }
    }

    @Override
    public BacNetBindingConfig configForItemName(String itemName) {
        return (BacNetBindingConfig) bindingConfigs.get(itemName);
    }

    @Override
    public Collection<BacNetBindingConfig> allConfigs() {
        // TODO Auto-generated method stub
        return (Collection<BacNetBindingConfig>) (Collection<?>) bindingConfigs.values();
    }

    @Override
    public BacNetBindingConfig configForProperty(int deviceId, Type type, int id) {
        for (BindingConfig bindingConfig : bindingConfigs.values()) {
            BacNetBindingConfig config = (BacNetBindingConfig) bindingConfig;
            if (config.deviceId == deviceId && config.type == type && config.id == id) {
                return config;
            }
        }
        return null;
    }

}
