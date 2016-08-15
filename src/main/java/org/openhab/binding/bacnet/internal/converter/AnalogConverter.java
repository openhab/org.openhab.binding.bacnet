package org.openhab.binding.bacnet.internal.converter;

import org.openhab.core.library.types.DecimalType;

import com.serotonin.bacnet4j.type.primitive.Real;

public class AnalogConverter {

    public static class DecimalTypeConverter implements Converter<Real, DecimalType> {
        @Override
        public Real fromHub(DecimalType type) {
            return new Real(type.floatValue());
        }

        @Override
        public DecimalType toHub(Real bacnet) {
            return new DecimalType(bacnet.floatValue());
        }
    }

}
