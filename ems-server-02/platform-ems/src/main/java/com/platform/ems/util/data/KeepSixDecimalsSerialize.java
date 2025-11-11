package com.platform.ems.util.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

public class KeepSixDecimalsSerialize extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (value != null && !"".equals(value)) {
            gen.writeString(value.setScale(6, BigDecimal.ROUND_HALF_UP) + "");

        } else {
            gen.writeString(value + "");

        }

    }
}
