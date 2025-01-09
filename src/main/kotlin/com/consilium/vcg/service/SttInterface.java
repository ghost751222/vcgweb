package com.consilium.vcg.service;

import java.nio.file.Path;
import java.util.List;

public interface SttInterface {

    public String sendDataToStt(List<Path> paths, String userName);
}