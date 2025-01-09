package com.consilium.vcg.service;


import com.consilium.vcg.configs.PsttRecognizeConfig;
import com.consilium.vcg.util.JacksonUtils;
import com.consilium.vcg.vo.PsttRequestDataVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.pachira.vcgclient.bean.ViewData;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("PsttRecognizeService")
@ConditionalOnProperty(name = "asr.type",havingValue = "Pstt")
public class PsttRecognizeService implements SttInterface {

    private static final Logger logger = LogManager.getLogger(PsttRecognizeService.class);


    private PsttRecognizedResultTaskService psttRecognizedResultTaskService;


    @Autowired
    public PsttRecognizeService(PsttRecognizedResultTaskService psttRecognizedResultTaskService) {
        this.psttRecognizedResultTaskService = psttRecognizedResultTaskService;
    }


    public String sendDataToStt(List<Path> paths, String userName) {

        for (Path path : paths) {

            try {
                PsttRequestDataVo vo = new PsttRequestDataVo();
                vo.setData(Files.readAllBytes(path));
                vo.setFileName(FilenameUtils.getName(path.toString()));
                vo.setUserName(userName);
                Thread.sleep(1000);
                this.sendDataToPstt(vo);
            } catch (Exception e) {
                logger.error(" sendDataToPstt Error = {}", e.getMessage());
            }
        }
        return "ok";
    }


    public String sendDataToPstt(MultipartFile[] files, String userName) {

        for (MultipartFile f : files) {
            try {
                PsttRequestDataVo vo = new PsttRequestDataVo();
                vo.setData(f.getBytes());
                vo.setFileName(f.getOriginalFilename());
                vo.setUserName(userName);
                Thread.sleep(1000);
                this.sendDataToPstt(vo);
            } catch (Exception e) {
                logger.error(" sendDataToPstt Error = {}", e.getMessage());
            }
        }
        return "ok";
    }


    private void sendDataToPstt(PsttRequestDataVo vo) throws JsonProcessingException {
        ViewData viewData = PsttRecognizeConfig.getViewData();
        Map<String, String> params = new HashMap<>();
        params.put("reqid", JacksonUtils.toJsonString(vo));
        params.put(ViewData.IS_ADD_PUNCT, ViewData.IS_ADD_PUNCT_ON);
        params.put(ViewData.IS_TRANS_DIGIT, ViewData.IS_TRANS_DIGIT_ON);
        viewData.setExtraParams(params);
        psttRecognizedResultTaskService.executeRecognizedResultTask(vo.getData(), viewData);
    }

}
