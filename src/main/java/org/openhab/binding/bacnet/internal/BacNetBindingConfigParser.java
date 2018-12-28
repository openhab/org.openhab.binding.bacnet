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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openhab.core.items.Item;
import org.openhab.model.item.binding.BindingConfigParseException;

class BacNetBindingConfigParser {

    private static final Pattern CONFIG_PATTERN = Pattern.compile("^([A-z]+=[^,]+(,|$))+");

    static BacNetBindingConfig parseBindingConfig(String itemName, Class<? extends Item> itemType, String configString)
            throws BindingConfigParseException {
        Matcher matcher = CONFIG_PATTERN.matcher(configString);
        if (!matcher.matches()) {
            throw new BindingConfigParseException(
                    "Invalid BacNet config: '" + configString + "'. Expected key1=value1,key2=value2");
        }

        Map<String, String> values = new HashMap<String, String>();
        for (String item : configString.split(",")) {
            String[] parts = item.split("=");
            if (parts.length != 2) {
                throw new BindingConfigParseException("Expected key=value in BacNet config");
            }
            values.put(parts[0].trim(), parts[1].trim());
        }
        return new BacNetBindingConfig(itemName, itemType, values);
    }

}
