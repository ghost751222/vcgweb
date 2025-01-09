package com.consilium.vcg.controller;


import com.consilium.vcg.service.SttInterface;
import com.consilium.vcg.util.FileUtils;
import com.consilium.vcg.util.WavUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

import static com.consilium.vcg.consts.AppConst.OUTPUT_PATH;
import static com.consilium.vcg.consts.AppConst.UPLOAD_PATH;

@Controller
@RequestMapping(value = "/recognize")
public class RecognizeController {

    BeanFactory beanFactory;

    @Autowired
    private SttInterface sttService;


    @RequestMapping(value = "", method = RequestMethod.GET)
    public String page() {
        return "recognize";
    }


    @RequestMapping(value = "/recognizeUpload", headers = "Content-Type=multipart/form-data", method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile[] files, @RequestParam("userName") String userName) throws Exception {


        List<Path> upload_paths = FileUtils.saveMultipartFiles(UPLOAD_PATH, files);
        List<Path> output_paths = WavUtils.convertAudioToWav(OUTPUT_PATH, upload_paths);
        return sttService.sendDataToStt(output_paths, userName);
        //return psttService.sendDataToPstt(files,userName);

    }


}
