package com.tangkf.metrics.util;

import org.junit.Test;

public class ZipUtils2Test {
    //要压缩的图片文件所在所存放位置
    public static String JPG_FILE_PATH = "D:\\media\\图片\\Camera\\IMG_20151121_125225.jpg";

    //zip压缩包所存放的位置
    public static String ZIP_FILE = "D:/hu.zip";

    @Test
    public void zipFilePip() {

    }

    @Test
    public void zipFilePip1() {
    }

    @Test
    public void zipFilePip2() {
        ZipUtils2.zipFilePip(ZIP_FILE, JPG_FILE_PATH);
    }
}