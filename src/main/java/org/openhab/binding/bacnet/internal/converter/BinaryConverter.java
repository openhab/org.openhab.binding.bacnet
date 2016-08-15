package org.openhab.binding.bacnet.internal.converter;

import org.openhab.core.library.types.OpenClosedType;

import com.serotonin.bacnet4j.type.enumerated.BinaryPV;
import com.serotonin.bacnet4j.type.primitive.Boolean;

public class BinaryConverter {

    public class OpenClosedTypeConverter
            implements Converter<com.serotonin.bacnet4j.type.primitive.Boolean, OpenClosedType> {

        @Override
        public Boolean fromHub(OpenClosedType type) {
            return OpenClosedType.OPEN == type ? Boolean.TRUE : Boolean.FALSE;
        }

        @Override
        public OpenClosedType toHub(Boolean bacnet) {
            return bacnet.booleanValue() ? OpenClosedType.OPEN : OpenClosedType.CLOSED;
        }
    }

    public class BinaryPVOpenClosedTypeConverter implements Converter<BinaryPV, OpenClosedType> {

        @Override
        public BinaryPV fromHub(OpenClosedType type) {
            return OpenClosedType.OPEN == type ? BinaryPV.active : BinaryPV.inactive;
        }

        @Override
        public OpenClosedType toHub(BinaryPV bacnet) {
            return BinaryPV.active.equals(bacnet) ? OpenClosedType.OPEN : OpenClosedType.CLOSED;
        }

    }
}
