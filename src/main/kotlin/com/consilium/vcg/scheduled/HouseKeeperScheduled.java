package com.consilium.vcg.scheduled;


import com.consilium.vcg.consts.AppConst;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class HouseKeeperScheduled {


    @Scheduled(cron = "${task.housekeeper.cron:0 0 0/1 * * *}")
    private void houseKeeperClean() throws IOException {
        FileUtils.deleteDirectory(new File(AppConst.UPLOAD_PATH));
        FileUtils.deleteDirectory(new File(AppConst.OUTPUT_PATH));
    }

}
