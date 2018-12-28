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
package org.openhab.binding.bacnet.internal.queue;

import org.code_house.bacnet4j.wrapper.api.BacNetClient;
import org.code_house.bacnet4j.wrapper.api.BacNetClientException;
import org.code_house.bacnet4j.wrapper.api.JavaToBacNetConverter;
import org.code_house.bacnet4j.wrapper.api.Priorities;
import org.code_house.bacnet4j.wrapper.api.Priority;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WritePropertyTask<T> implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(WritePropertyTask.class);

    private final BacNetClient client;
    private final Property property;
    private final T value;
    private final JavaToBacNetConverter<T> converter;
    private final Priority priority;

    public WritePropertyTask(BacNetClient client, Property property, T value, JavaToBacNetConverter<T> converter) {
        this(client, property, value, converter, 0);
    }

    public WritePropertyTask(BacNetClient client, Property property, T value, JavaToBacNetConverter<T> converter,
            int priority) {
        this(client, property, value, converter, Priorities.get(priority).orElse(null));
    }

    public WritePropertyTask(BacNetClient client, Property property, T value, JavaToBacNetConverter<T> converter,
            Priority priority) {
        this.client = client;
        this.property = property;
        this.value = value;
        this.converter = converter;
        this.priority = priority;
    }

    @Override
    public void run() {
        try {
            client.setPropertyValue(property, value, converter, priority);
            logger.trace("Property {} was properly set to value {}", property, value);
        } catch (BacNetClientException e) {
            logger.warn("Could not set value {} for property {}", value, property, e);
        } catch (Exception e) {
            logger.error("Unexpected error while setting value {} for property {}", value, property, e);
        }
    }

}
