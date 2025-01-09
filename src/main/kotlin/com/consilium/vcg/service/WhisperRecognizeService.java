package com.consilium.vcg.service;

import com.consilium.vcg.vo.PsttResponseDataVo;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service("WhisperRecognizeService")
@ConditionalOnProperty(name = "asr.type",havingValue = "Whisper")
public class WhisperRecognizeService implements SttInterface {

    private static final Logger logger = LogManager.getLogger(WhisperRecognizeService.class);

    private final TaskExecutor executor;

    private final SimpMessagingTemplate webSocket;


    @Autowired
    public WhisperRecognizeService(@Qualifier("taskExecutor") TaskExecutor executor, SimpMessagingTemplate webSocket) {
        this.executor = executor;
        this.webSocket = webSocket;
    }


    @Override
    public String sendDataToStt(List<Path> paths, String userName) {


        paths.forEach(p -> {
            executor.execute(() -> {
                String result = "test";
                PsttResponseDataVo rVo = new PsttResponseDataVo();
                rVo.setUserName(userName);
                rVo.setFileName(FilenameUtils.getName(p.toString()));
                rVo.setResult(result);
                //rVo.setStatusText(PsttRecognizedResultTaskService.ServerStatus.ServerTrans.value());
                webSocket.convertAndSendToUser(rVo.getUserName(), "/queue/" + rVo.getUserName(), rVo);
            });
        });


        return "ok";
    }
}
