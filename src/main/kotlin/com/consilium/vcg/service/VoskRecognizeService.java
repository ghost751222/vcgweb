package com.consilium.vcg.service;

import com.consilium.vcg.util.JacksonUtils;
import com.consilium.vcg.vo.PsttResponseDataVo;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service("VoskRecognizeService")
@ConditionalOnProperty(name = "asr.type", havingValue = "Vosk")

public class VoskRecognizeService implements SttInterface {


    private static final Logger logger = LogManager.getLogger(VoskRecognizeService.class);

    private final TaskExecutor executor;

    private final SimpMessagingTemplate webSocket;
    //  private CountDownLatch receiveLatch;
    private final int countDown = 1;

    @Autowired
    public VoskRecognizeService(@Qualifier("taskExecutor") TaskExecutor executor, SimpMessagingTemplate webSocket) {
        this.executor = executor;
        this.webSocket = webSocket;
    }


    @Override
    public String sendDataToStt(List<Path> paths, String userName) {


        paths.forEach(p -> {
            executor.execute(() -> {

                try {
                    CountDownLatch receiveLatch = null;
                    //WebSocket ws = getWebSocket(userName, p);


                    WebSocket ws = getWebSocket();
                    int totalFramesRead = 0;
                    File fileIn = p.toFile();
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileIn);
                    AudioFormat format = audioInputStream.getFormat();
                    int bytesPerFrame = format.getFrameSize();
                    if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
                        bytesPerFrame = 1;
                    }
                    int numBytes = 1024 * bytesPerFrame;
                    byte[] audioBytes = new byte[numBytes];

                    // Let Vosk server now the sample rate of sound file
                    ws.sendText("{ \"config\" : { \"sample_rate\" : " + (int) format.getSampleRate() + " } }");

                    int numBytesRead = 0;
                    int numFramesRead = 0;
                    // Try to read numBytes bytes from the file.
                    while ((numBytesRead = audioInputStream.read(audioBytes)) != -1) {
                        // Calculate the number of frames actually read.
                        numFramesRead = numBytesRead / bytesPerFrame;
                        totalFramesRead += numFramesRead;
                        receiveLatch = new CountDownLatch(countDown);


                        WebSocketAdapter webSocketAdapter = getWebSocketAdapter(userName, p, receiveLatch);
                        ws.addListener(webSocketAdapter);
                        ws.sendBinary(audioBytes);
                        receiveLatch.await();
                        ws.removeListener(webSocketAdapter);

                    }
                    //receiveLatch = new CountDownLatch(countDown);
                    ws.sendText("{\"eof\" : 1}");
                    // receiveLatch.await();
                    ws.disconnect();

                    PsttResponseDataVo rVo = new PsttResponseDataVo();
                    rVo.setUserName(userName);
                    rVo.setFileName(FilenameUtils.getName(p.toString()));
                    rVo.setStatusText(PsttRecognizedResultTaskService.ServerStatus.ServerTransComplete.value());
                    webSocket.convertAndSendToUser(rVo.getUserName(), "/queue/" + rVo.getUserName(), rVo);

                } catch (Exception e) {
                    logger.error(e);
                }


            });
        });


        return "ok";
    }

    @NotNull
    private WebSocketAdapter getWebSocketAdapter(String userName, Path p, CountDownLatch receiveLatch) {
        CountDownLatch finalReceiveLatch = receiveLatch;
        return new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String message) throws IOException {
                JsonNode jsonNode = JacksonUtils.toClass(message, JsonNode.class);
                if (jsonNode.has("result")) {
                    String result = jsonNode.get("text").asText().replace(" ", Strings.EMPTY);
                    result = ZhConverterUtil.toTraditional(result);
                    PsttResponseDataVo rVo = new PsttResponseDataVo();
                    rVo.setUserName(userName);
                    rVo.setFileName(FilenameUtils.getName(p.toString()));
                    rVo.setResult(result);
                    rVo.setStatusText(PsttRecognizedResultTaskService.ServerStatus.ServerTrans.value());
                    webSocket.convertAndSendToUser(rVo.getUserName(), "/queue/" + rVo.getUserName(), rVo);

                }
                finalReceiveLatch.countDown();
            }
        };
    }

    @NotNull
    private WebSocket getWebSocket() throws IOException, WebSocketException {
        WebSocketFactory factory = new WebSocketFactory();
        WebSocket ws = factory.createSocket("ws://10.12.0.32:2700");
        //ws.addListener(webSocketAdapter);
//        ws.addListener(new WebSocketAdapter() {
//            @Override
//            public void onTextMessage(WebSocket websocket, String message) throws IOException {
//                //logger.info("file Name ={} message ={}", p.toString(), message);
//                JsonNode jsonNode = JacksonUtils.toClass(message, JsonNode.class);
//                if (jsonNode.has("result")) {
//                    String result = jsonNode.get("text").asText();
//                    result = ZhConverterUtil.toTraditional(result);
//                    PsttResponseDataVo rVo = new PsttResponseDataVo();
//                    rVo.setUserName(userName);
//                    rVo.setFileName(FilenameUtils.getName(p.toString()));
//                    rVo.setResult(result);
//                    rVo.setStatusText(PsttRecognizedResultTaskService.ServerStatus.ServerTrans.value());
//                    webSocket.convertAndSendToUser(rVo.getUserName(), "/queue/" + rVo.getUserName(), rVo);
//
//                }
//                receiveLatch.countDown();
//            }
//        });
        ws.connect();
        return ws;
    }
}
