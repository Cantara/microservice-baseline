package no.cantara.commands.util;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class StringConv {

    private static final Logger log = LoggerFactory.getLogger(StringConv.class);

    public static String UTF8(byte[] bytes) {
        try {
            if (bytes != null) {
                return new String(bytes, "UTF8");
            } else {
                return "";
            }

        } catch (UnsupportedEncodingException e) {
            log.warn("UnsupportedEncodingException - returning empty String", e);
        }
        return "";
    }

    public static String UTF8(String str) {
        return new String(str);
    }

    public static byte[] getBytes(String str) {
        try {
            return str.getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            log.warn("UnsupportedEncodingException - returning ArrayUtils.EMPTY_BYTE_ARRAY", e);
        }
        return ArrayUtils.EMPTY_BYTE_ARRAY;
    }
}