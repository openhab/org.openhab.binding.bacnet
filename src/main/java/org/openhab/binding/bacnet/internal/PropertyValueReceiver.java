package org.openhab.binding.bacnet.internal;

import org.code_house.bacnet4j.wrapper.api.Property;

/**
 *
 *
 * @param <T> Base type of received values.
 */
public interface PropertyValueReceiver<T> {

    void receiveProperty(Property property, T value);

}
