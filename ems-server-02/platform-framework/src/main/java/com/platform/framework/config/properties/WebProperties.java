package com.platform.framework.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@Validated
@Component
public class WebProperties {

    /**
     * ems包下统一前缀
     */
    Api ems = new Api("ems", "**.platform.ems.**.controller.**");

    /**
     * system包下统一前缀
     */
    Api system = new Api("system", "**.platform.system.controller.**");

    /**
     * file包下统一前缀
     */
    Api file = new Api("file", "**.platform.file.controller.**");

    /**
     * flow包下统一前缀
     */
    Api flow = new Api("flow", "**.platform.flowable.controller.**");

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Api {

        @NotEmpty
        private String prefix;

        @NotEmpty
        private String controllerPath;
    }
}

