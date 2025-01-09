package com.consilium.vcg.configs;

import com.pachira.vcgclient.bean.ViewData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PsttRecognizeConfig {

    private static String psttIP;

    private static Integer psttPort;

    private static String psttKid = "3";

    @Value("${pstt.config.ip}")
    public void setPsttIP(String ip) {
        PsttRecognizeConfig.psttIP = ip;
    }


    @Value("${pstt.config.port}")
    public void setPsttPort(Integer port) {
        PsttRecognizeConfig.psttPort = port;
    }

    public static ViewData getViewData() {
        return new ViewData(psttIP, psttPort, null, "23", psttKid);
    }

}
