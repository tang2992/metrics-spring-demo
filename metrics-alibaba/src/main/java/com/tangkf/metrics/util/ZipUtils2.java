package com.tangkf.metrics.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class ZipUtils2 {
    private final static Integer TEMP_SIZE = 2048;

    /**
     * 压缩方法
     * @param zipFile zip文件
     * @param files 文件列表
     */
    public static void zipFilePip(File zipFile, File... files){
        zipFilePip(zipFile, Arrays.asList(files));
    }

    /**
     * 压缩方法
     * @param zipFile zip文件
     * @param files 文件列表
     */
    public static void zipFilePip(String zipFile,String... files){
        List<File> files1 = new ArrayList<>();
        for(String fileStr : files){
            files1.add(new File(fileStr));
        }
        zipFilePip(new File(zipFile),files1);
    }

    /**
     * 压缩方法
     *
     * @param zipFile zip文件
     * @param files   文件列表
     */
    public static void zipFilePip(File zipFile, List<File> files) {

        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
             WritableByteChannel writableByteChannel = Channels.newChannel(zipOut)) {
            for (File file : files) {
                taskFunction(zipOut, file, writableByteChannel, "");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void taskFunction(ZipOutputStream zos, File file, WritableByteChannel out, String base) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();

            base = StringUtils.isEmpty(base) ? file.getName() + "/" : base + "/";

            assert files != null;
            for (File file1 : files) {
                taskFunction(zos, file1, out, base + file1.getName());
            }
        } else {
            base = StringUtils.isEmpty(base) ? file.getName() : base;

            zos.putNextEntry(new ZipEntry(base));

            //内存中的映射文件
            MappedByteBuffer mappedByteBuffer = new RandomAccessFile(file.getAbsolutePath(), "r").getChannel()
                    .map(FileChannel.MapMode.READ_ONLY, 0, file.length());

            out.write(mappedByteBuffer);
        }

    }
}
