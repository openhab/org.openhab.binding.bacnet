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

/**
 * Utility type to hold device id and network number.
 *
 * @author Łukasz Dywicki
 */
public class DevicePointer {

    public final Integer networkNumber;
    public final Integer deviceId;

    public DevicePointer(Integer networkNumber, Integer deviceId) {
        this.networkNumber = networkNumber;
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "Device[network: " + networkNumber + ", id: " + deviceId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((networkNumber == null) ? 0 : networkNumber.hashCode());
        result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DevicePointer other = (DevicePointer) obj;
        if (networkNumber == null) {
            if (other.networkNumber != null) {
                return false;
            }
        } else if (!networkNumber.equals(other.networkNumber)) {
            return false;
        }
        if (deviceId == null) {
            if (other.deviceId != null) {
                return false;
            }
        } else if (!deviceId.equals(other.deviceId)) {
            return false;
        }
        return true;
    }

}
