package com.zyj.demo.math;


import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * 生成随机数
 *
 * @author zyj
 */
public class RandomTest {

    public static void main(String[] args) {
        int numPoints = 40000;
        try (FileWriter writer = new FileWriter("./scatter-plot-data.js")) {
            writer.write("var scatterData = [\n");
            Random random = new Random();
            for (int i = 0; i < numPoints; i++) {
                double x = Math.round(Math.random() * 1000);
                double y = Math.round(Math.random() * 1000);
                writer.write(String.format("  [%.2f, %.2f]", x, y));
                if (i < numPoints - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }
            writer.write("];");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
