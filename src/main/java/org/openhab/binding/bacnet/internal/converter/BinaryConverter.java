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
