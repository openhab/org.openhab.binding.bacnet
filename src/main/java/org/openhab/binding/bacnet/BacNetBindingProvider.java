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
package org.openhab.binding.bacnet;

import java.util.Collection;

import org.code_house.bacnet4j.wrapper.api.Type;
import org.openhab.binding.bacnet.internal.BacNetBindingConfig;
import org.openhab.core.binding.BindingProvider;

public interface BacNetBindingProvider extends BindingProvider {
    public BacNetBindingConfig configForItemName(String itemName);

    public BacNetBindingConfig configForProperty(int deviceId, Type type, int id);

    public Collection<BacNetBindingConfig> allConfigs();

}
