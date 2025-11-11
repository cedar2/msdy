package com.platform.common.core.experimental.auto;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(
        value = "enable-experimental-feature",
        havingValue = "true",
        matchIfMissing = true
)
@Component
public class ExperimentalAutoConfiguration {

}
