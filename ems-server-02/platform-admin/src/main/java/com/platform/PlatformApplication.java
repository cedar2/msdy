package com.platform;

import com.platform.framework.security.annotation.EnableCustomConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 启动程序
 *
 * @author platform
 */
@EnableCaching
@EnableSwagger2
@EnableCustomConfig
@ComponentScan(basePackages={"com.platform.*"})
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class PlatformApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(PlatformApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  后台启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " _______  _____          _     _________  ________    ___   _______     ____    ____ \n" +
                "|_   __ \\|_   _|        / \\   |  _   _  ||_   __  | .'   `.|_   __ \\   |_   \\  /   _|\n" +
                "  | |__) | | |         / _ \\  |_/ | | \\_|  | |_ \\_|/  .-.  \\ | |__) |    |   \\/   |  \n" +
                "  |  ___/  | |   _    / ___ \\     | |      |  _|   | |   | | |  __ /     | |\\  /| |  \n" +
                " _| |_    _| |__/ | _/ /   \\ \\_  _| |_    _| |_    \\  `-'  /_| |  \\ \\_  _| |_\\/_| |_ \n" +
                "|_____|  |________||____| |____||_____|  |_____|    `.___.'|____| |___||_____||_____|");
    }
}
