package com.platform.ems.util.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

public class KeepTwoDecimalsSerialize extends JsonSerializer<BigDecimal> {

//    @Override
//    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
//        if (value != null) {
//            // 保留2位小数，四舍五入
//            BigDecimal number = value.setScale(2, RoundingMode.HALF_UP);
//            gen.writeNumber(number);
//        } else {
//            gen.writeNumber(value);
//        }
//    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (value != null && !"".equals(value)) {
            gen.writeString(value.setScale(2, BigDecimal.ROUND_HALF_UP) + "");

        } else {
            gen.writeString(value + "");

        }

    }
}
