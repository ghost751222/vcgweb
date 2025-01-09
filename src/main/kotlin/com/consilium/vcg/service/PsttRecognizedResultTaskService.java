package com.consilium.vcg.service;



import com.consilium.vcg.util.JacksonUtils;
import com.consilium.vcg.vo.PsttRequestDataVo;
import com.consilium.vcg.vo.PsttResponseDataVo;
import com.pachira.vcgclient.bean.ViewData;
import com.pachira.vcgclient.service.send.SendStreamByte;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Service
public class PsttRecognizedResultTaskService {

    public enum ServerStatus {
        ServerError("服務器錯誤"),
        ServerTrans("轉譯中"),
        ServerTransComplete("轉譯完成");


        private String value;

        private ServerStatus(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    private static final Logger logger = LogManager.getLogger(PsttRecognizedResultTaskService.class);

    private TaskExecutor executor;

    private SimpMessagingTemplate webSocket;

    @Autowired
    public PsttRecognizedResultTaskService(@Qualifier("taskExecutor") TaskExecutor executor, SimpMessagingTemplate webSocket) {
        this.executor = executor;
        this.webSocket = webSocket;
    }


    @Scheduled(fixedDelay = 30000)
    public void showTaskExecutorInfo() {
        ThreadPoolTaskExecutor task = ((ThreadPoolTaskExecutor) executor);
        logger.info("當前活動線程數={} ,線程處理隊列長度={}", task.getActiveCount(), task.getThreadPoolExecutor().getQueue().size());
    }

    public void executeRecognizedResultTask(byte[] data, ViewData viewData) {

        executor.execute(() -> {
            try {
                ViewData _viewData = viewData;
                final PsttRequestDataVo pVo = JacksonUtils.toClass(_viewData.getExtraParams().get("reqid"), PsttRequestDataVo.class);
                SendStreamByte sendStreamByte = new SendStreamByte(viewData, result -> {
                    try {


                        PsttResponseDataVo rVo = new PsttResponseDataVo();
                        rVo.setUserName(pVo.getUserName());
                        rVo.setFileName(pVo.getFileName());
                        if ("-1".equals(result)) {
                            rVo.setStatusText(ServerStatus.ServerError.value());
                            logger.error(_viewData.getExtraParams().get("reqid"));
                        } else if ("0".equals(result)) {
                            rVo.setStatusText(ServerStatus.ServerTransComplete.value());
                        } else {
                            final DocumentBuilderFactory[] documentBuilderFactory = {DocumentBuilderFactory.newInstance()};
                            DocumentBuilder documentBuilder = documentBuilderFactory[0].newDocumentBuilder();
                            Document document = documentBuilder.parse(new InputSource(new StringReader(result)));
                            String value = document.getElementsByTagName("value").item(0).getTextContent();
                            String weight = document.getElementsByTagName("weight").item(0).getTextContent();
                            String sessionid = document.getElementsByTagName("sessionid").item(0).getTextContent();
                            if (weight.equals("100")) {
                                rVo.setResult(value);
                            }

//                            if ("0".equals(sessionid))
//                                rVo.setStatusText(ServerStatus.ServerError.value());
//                            else
                            rVo.setStatusText(ServerStatus.ServerTrans.value());
                        }
                        webSocket.convertAndSendToUser(rVo.getUserName(), "/queue/" + rVo.getUserName(), rVo);

                    } catch (Exception e) {
                        logger.error("executor.execute Error = {} ", e);
                    }
                });

                //SendStreamByte sendStreamByte = new SendStreamByte(viewData, this::recognizedResult);
                if (!sendStreamByte.isAlive()) sendStreamByte.init();
                sendStreamByte.senddata(data);
                sendStreamByte.senddata(new byte[0]);
            } catch (Exception e) {
                logger.error(e);
            }


        });
    }

    private void recognizedResult(String result) {
        try {
            //1701170126
            logger.info(result);
            if ("0".equals(result)) return;
            final DocumentBuilderFactory[] documentBuilderFactory = {DocumentBuilderFactory.newInstance()};
            DocumentBuilder documentBuilder = documentBuilderFactory[0].newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(result)));
            String value = document.getElementsByTagName("value").item(0).getTextContent();
            String weight = document.getElementsByTagName("weight").item(0).getTextContent();
            String requestId = document.getElementsByTagName("requestid").item(0).getTextContent();
            PsttRequestDataVo vo = JacksonUtils.toClass(requestId, PsttRequestDataVo.class);
            PsttResponseDataVo rVo = new PsttResponseDataVo();
            rVo.setUserName(vo.getUserName());
            rVo.setFileName(vo.getFileName());
            rVo.setResult(value);
            webSocket.convertAndSendToUser(vo.getUserName(), "/queue/" + vo.getUserName(), rVo);
        } catch (Exception e) {
            logger.error("recognizedResult  error={}", e);
        }

    }
}
