package com.opinta.util;

import com.opinta.entity.Address;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class AddressUtil {

    public static boolean isSameTown(Address first, Address second) {
        if (first == null || second == null ||
                isEmpty(first.getRegion()) || isEmpty(second.getRegion()) ||
                isEmpty(first.getCity()) || isEmpty(second.getRegion())) {
            return false;
        }
        return first.getRegion().trim().equalsIgnoreCase(second.getRegion().trim()) &&
                first.getCity().trim().equalsIgnoreCase(second.getCity().trim());
    }

    public static boolean isSameRegion(Address first, Address second) {
        if (first == null || second == null ||
                isEmpty(first.getRegion()) || isEmpty(second.getRegion())) {
            return false;
        }
        return first.getRegion().trim().equalsIgnoreCase(second.getRegion().trim());
    }
}
