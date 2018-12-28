/*
 * (C) Copyright 2016-2018 openHAB and respective copyright holders.
 *
 * bacnet4j-wrapper is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 *     https://www.gnu.org/licenses/gpl-3.0.txt
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this binding; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
            BacNetBindingConfig config = BacNetBindingConfigParser.parseBindingConfig(item.getName(), item.getClass(),
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
    public BacNetBindingConfig configForProperty(int networkNumber, int deviceId, Type type, int id) {
        for (BindingConfig bindingConfig : bindingConfigs.values()) {
            BacNetBindingConfig config = (BacNetBindingConfig) bindingConfig;
            if (config.devicePointer.equals(new DevicePointer(networkNumber, deviceId)) && config.type == type
                    && config.id == id) {
                return config;
            }
        }
        return null;
    }

}
