package com.demo.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class LSB {
    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println("***************LSB图像隐写编码程序****************");
        System.out.println("*********请选择想要使用的功能(输入数字)*************");
        System.out.println();
        System.out.println("******************1.LSB编码********************");
        System.out.println("******************2.LSB解码********************");
        System.out.println();
        System.out.print("请输入你想选择的功能:");
        String choice = scan.next();
        switch(choice){
            case "1":
                System.out.print("请输入需要加密的文件的路径:");
                String textPath = scan.next();
                System.out.print("请输入png图像辅助文件的路径:");
                String imagePath = scan.next();
                System.out.print("最后再输入一下生成的png图片的保存路径:");
                String  imageOutputPath= scan.next();
                LSBEncoder(textPath,imagePath,imageOutputPath);
                scan.close();
                break;
            case "2":
                System.out.print("请输入待解码的png图片的路径:");
                String imageInputPath = scan.next();
                System.out.print("请输入解码后，存放数据的文件名称");
                String textFilePath = scan.next();
                LSBDecoder(imageInputPath,textFilePath);
                scan.close();
                break;
            default:
                System.out.print("谁叫你乱按键盘的啊！！！活该不能执行，哼😕！");
                scan.close();
                break;
        }
    }
    public static void LSBEncoder(String textPath, String imagePath,String imageOutputPath ) throws IOException {

        //读取png图像
        BufferedImage image = ImageIO.read(new File(imagePath));
        int width = image.getWidth();
        int height = image.getHeight();
        int[][][] rgb = new int[width][height][3];
        //将图像每个点的像素(R,G,B)存储在数组中
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                int pixel = image.getRGB(w, h);//读取的是一个24位的数据
                //数据三个字节分别代表R、B、G
                rgb[w][h][0] = (pixel & 0xff0000) >> 16;//R
                rgb[w][h][1] = (pixel & 0xff00) >> 8;//B
                rgb[w][h][2] = (pixel & 0xff);//G
            }
        }

        //导入待加密机密文件
        FileInputStream fis = new FileInputStream(textPath);
        int byteLen = fis.available();
        byte[] buf = new byte[byteLen];
        fis.read(buf);


        //我用两个字节(16位)表示数据部分的长度，也就是说，图像转像素点后，前16个像素点的R对应的字节的最低有效位都是默认表示数据字节数组的长度
        //规定，数据的长度最大为2^16-1
        int[] bufLen = new int[2];
        bufLen[0] = (byteLen & 0xff00 ) >> 8;
        bufLen[1] = (byteLen & 0xff);
        for (int i = 0; i < 2; i++) {
            for (int j = 7; j >= 0; j--) {
                int h =(i * 8 +(7 - j)) / width;
                int w = (i * 8 + (7-j)) % width;
                //只取每个像素点的R，的字节的最低位
                if((bufLen[i] >>j & 1) == 1 ){
                    rgb[w][h][0] = rgb[w][h][0] | 1;
                } else {
//                    rgb[w][h][0] = rgb[w][h][0] & 0xfe;
                    rgb[w][h][0] = rgb[w][h][0] & 0xe;
                }
            }
        }

        //按照规则将数据的二进制序列全部放到每一个像素点的第一个字节的最后一位上
        for (int i = 2; i < byteLen + 2; i++) {
            for (int j = 7; j >= 0; j--) {
                //高
                int h = (i * 8 + (7 - j)) / width ;
                //宽
                int w = (i * 8 + (7 - j)) % width;
                if ((buf[i-2] >> j & 1) == 1) {
                    rgb[w][h][0] = rgb[w][h][0] | 1;//变1
                } else {
//                    rgb[w][h][0] =rgb[w][h][0] & 0xfe;
                    rgb[w][h][0] =rgb[w][h][0] & 0xe;
                }
            }
        }
        //        构建通过编码生成的png图片的类型
        BufferedImage imageOutput = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                int[] color = new int[3];
                color[0] = rgb[w][h][0] << 16;
                color[1] = rgb[w][h][1] << 8;
                color[2] = rgb[w][h][2];
                int pixel = 0xff000000 | color[0] | color[1] | color[2];
                imageOutput.setRGB(w, h, pixel);
            }
        }
        ImageIO.write(imageOutput, "png", new File(imageOutputPath));
    }

    public static void LSBDecoder(String imageInputPath,String textFilePath) throws IOException {
        BufferedImage imageInput = ImageIO.read(new File(imageInputPath));
        int width = imageInput.getWidth();
        int height = imageInput.getHeight();
        int[] bufLen=  new int[2];
        //将图像每个点的像素(R,G,B)存储在数组中
        for (int i = 0; i < 2; i++) {
            int[] bits = new int[8];
            for (int j = 7; j >= 0; j--) {
                int h =(i * 8 +(7 - j)) / width;
                int w = (i * 8 + (7-j)) % width;
                int pixel = imageInput.getRGB(w,h);
                int r = (pixel & 0xff0000) >> 16;
                bits[j] = (r & 1) << j;
            }
            bufLen[i] =  bits[7] | bits[6] | bits[5] | bits[4] | bits[3] | bits[2] | bits[1] | bits[0];
        }
        int byteLen =  ( (bufLen[0] << 8) |bufLen[1]);
//        System.out.println(byteLen);
        byte[] buf = new byte[byteLen];
        for (int i = 2; i < byteLen + 2; i++) {
            int[] bits = new int[8];
            for (int j = 7; j >= 0; j--) {
                int h = (i * 8 + (7 - j)) / width;
                int w = (i * 8 + (7 - j)) % width;
                int pixel = imageInput.getRGB(w, h);
                int r = (pixel & 0xff0000) >> 16;
                bits[j] = (r & 0x1) << j;
            }
            buf[i-2] = (byte)(bits[7] | bits[6] | bits[5] | bits[4] | bits[3] | bits[2] | bits[1] | bits[0]);
        }
//        System.out.println(new String(buf));
        FileOutputStream fos = new FileOutputStream(textFilePath);
        fos.write(buf);
        fos.close();
    }
}

