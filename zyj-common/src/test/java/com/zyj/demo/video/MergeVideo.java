package com.zyj.demo.video;

import java.io.IOException;

/**
 * 类的描述
 *
 * @author zyj
 * @version 1.0.0
 */
public class MergeVideo {


    public static void main(String[] args) {
        String[] inputFiles = {
                "D:/var/1.mp4",
                "D:/var/11.mp4"
        };
        String outputFile = "D:\\var\\mm.mp4";

        mergeVideos(inputFiles, outputFile);
    }

    public static void mergeVideos(String[] inputFiles, String outputFile) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("D:\\GreenSoft\\ffmpeg-7.0.2-full_build\\bin\\ffmpeg",
                "-i", inputFiles[0],
                "-i", inputFiles[1],
                "-filter_complex",
                "[0:v:0][0:a:0][1:v:0][1:a:0][2:v:0][2:a:0]concat=n=3:v=1:a=1[outv][outa]",
                "-map", "[outv]",
                "-map", "[outa]",
                outputFile);

        try {
            System.out.println("Starting to merge videos!");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Videos merged successfully!");
            } else {
                System.err.println("Failed to merge videos." + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Done!");
    }

}
