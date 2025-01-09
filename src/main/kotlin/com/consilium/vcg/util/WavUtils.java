package com.consilium.vcg.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class WavUtils {

    private static final Logger logger = LoggerFactory.getLogger(WavUtils.class);

    private static int execCmd(String cmd) throws IOException, InterruptedException {

        List<String> cmdList = new ArrayList<>();
        cmdList.add(cmd);
        Process proc = Runtime.getRuntime().exec(String.join(" ", cmdList));

        String line = null;

        InputStream stderr = proc.getErrorStream ();
        InputStreamReader esr = new InputStreamReader (stderr);
        BufferedReader ebr = new BufferedReader (esr);
        System.out.println ("<error>");
        while ( (line = ebr.readLine ()) != null)
            System.out.println(line);
        System.out.println ("</error>");

        InputStream stdout = proc.getInputStream ();
        InputStreamReader osr = new InputStreamReader (stdout);
        BufferedReader obr = new BufferedReader (osr);
        System.out.println ("<output>");
        while ( (line = obr.readLine ()) != null)
            System.out.println(line);
        System.out.println ("</output>");



        int exitVal = proc.waitFor();
        //logger.info("cmd:{} , exitVal:{}", cmd, exitVal);
        return exitVal;
    }


    private static String getFFmpegCommnad(File srcFile, File destFile) {

        try {
            StringBuilder cmd = new StringBuilder();
            String extension = FilenameUtils.getExtension(srcFile.getName()).toLowerCase();
            String osName = System.getProperty("os.name");
            String ffmpeg = "ffmpeg";
            String quot = "'";
            if (osName.equalsIgnoreCase("Linux")) {
                ffmpeg = "./ffmpeg";
                quot = " ";
            }
            switch (extension) {
                case "mp3":
                case "wav":
                    cmd.append(ffmpeg).append(" -i ")
                            .append(quot).append(srcFile.toString()).append(quot)
                            .append(" -acodec pcm_s16le -ac 1 -ar 8000 -y ")
                            .append(" ")
                            .append(quot).append(destFile.toString()).append(quot);
                    break;
                case "vox":
                    cmd.append(ffmpeg).append(" -acodec adpcm_ima_oki -f s16le -ar 8000 -y -i ")
                            .append(quot).append(srcFile.toString()).append(quot)
                            .append(" ")
                            .append(quot).append(destFile.toString()).append(quot);
                    break;
            }

            return cmd.toString();
        } catch (Exception e) {
            logger.error(" get FFmpeg Commnad error={} ", e.getMessage());
            return Strings.EMPTY;
        }
    }

    public static List<Path> convertAudioToWav(String outputPath, List<Path> paths) throws IOException, InterruptedException {
        List<Path> _paths = new ArrayList<>();
        String cmd;
        File _f = new File(outputPath);
        if (!_f.exists()) _f.mkdirs();

        File sourceFile, destinationFile;
        for (Path path : paths) {
            sourceFile = path.toFile();
            if (sourceFile.exists()) {
                String extension = FilenameUtils.getExtension(sourceFile.getName());
                destinationFile = new File(Paths.get(outputPath, sourceFile.getName().replace(extension, "wav")).toString());
                cmd = getFFmpegCommnad(sourceFile, destinationFile);
                if (Strings.isNotEmpty(cmd)) {
                    int val = execCmd(cmd);
                    logger.info(" sourceFile:{} ,exitCode :{}",sourceFile.toString(),val);
                    if (val == 0) _paths.add(destinationFile.toPath());
                }
            }
        }
        return _paths;
    }


//    public static void main(String[] args) {
//
//    }

}
