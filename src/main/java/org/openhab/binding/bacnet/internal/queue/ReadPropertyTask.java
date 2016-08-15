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
