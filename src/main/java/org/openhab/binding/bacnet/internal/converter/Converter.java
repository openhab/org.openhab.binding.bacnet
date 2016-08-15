package org.openhab.binding.bacnet.internal.converter;

import org.openhab.core.types.Type;

import com.serotonin.bacnet4j.type.Encodable;

public interface Converter<T extends Encodable, X extends Type> {

    T fromHub(X type);

    X toHub(T bacnet);

}
