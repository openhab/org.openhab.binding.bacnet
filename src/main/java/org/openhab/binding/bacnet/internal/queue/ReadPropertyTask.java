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
import org.code_house.bacnet4j.wrapper.api.BypassBacnetConverter;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.openhab.binding.bacnet.internal.PropertyValueReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.type.Encodable;

public class ReadPropertyTask implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(ReadPropertyTask.class);
    private final BacNetClient client;
    private final Property property;
    private final PropertyValueReceiver<Encodable> receiver;

    public ReadPropertyTask(BacNetClient client, Property property, PropertyValueReceiver<Encodable> receiver) {
        this.client = client;
        this.property = property;
        this.receiver = receiver;

    }

    @Override
    public void run() {
        try {
            Encodable value = client.getPropertyValue(property, new BypassBacnetConverter());
            receiver.receiveProperty(property, value);
        } catch (BacNetClientException e) {
            logger.warn("Could not set read property {}", property, e);
        } catch (Exception e) {
            logger.error("Could not set read property {}", property, e);
        }
    }

}
