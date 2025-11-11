package com.platform.ems.device.api;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Map;
import java.util.TreeMap;

public class SignUtil {

    public static String sign(String key, TreeMap<String, String> sortedParams) throws Exception {
        // Create a StringBuilder to append the parameters
        StringBuilder sb = new StringBuilder();

        // Iterate over the sorted parameters and append them with &
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        // Remove the last & character
        sb.deleteCharAt(sb.length() - 1);

        // Get the string to sign
        String stringToSign = sb.toString();

        // Use HmacSha1Util to generate the signature with the key and string to sign

        return hmacSha1ThenBase64(stringToSign, key);
    }

    public static String hmacSha1ThenBase64(String data, String key) throws Exception {
        // Create a Mac object with the HMAC-SHA1 algorithm
        Mac mac = Mac.getInstance("HmacSHA1");

        // Initialize the Mac object with the secret key
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        mac.init(secretKeySpec);

        // Compute the HMAC on input data bytes
        byte[] rawHmac = mac.doFinal(data.getBytes());

        // Convert raw bytes to Base64
        byte[] base64Bytes = Base64.encodeBase64(rawHmac);

        // Return Base64-encoded result
        return new String(base64Bytes);
    }
}
