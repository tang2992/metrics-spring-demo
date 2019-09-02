package com.tangkf.metrics.util;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class ZipUtils {
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
     * @param zipFile zip文件
     * @param files 文件列表
     */
    public static void zipFilePip(File zipFile,List<File> files) {

        try(WritableByteChannel out = Channels.newChannel(new FileOutputStream(zipFile))) {
            Pipe pipe = Pipe.open();
            //异步任务
            CompletableFuture.runAsync(()->runTask(pipe,files));

            //获取读通道
            ReadableByteChannel readableByteChannel = pipe.source();
            ByteBuffer buffer = ByteBuffer.allocate(TEMP_SIZE);
            while (readableByteChannel.read(buffer)>= 0) {
                buffer.flip();
                out.write(buffer);
                buffer.clear();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void runTask(Pipe pipe, List<File> files) {

        try(ZipOutputStream zos = new ZipOutputStream(Channels.newOutputStream(pipe.sink()));
            WritableByteChannel out = Channels.newChannel(zos)) {
            for (File file : files) {
                taskFunction(zos,file,out,"");
            }
        }catch (Exception e){
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

            FileChannel fileChannel = new FileInputStream(new File(file.getAbsolutePath())).getChannel();

            fileChannel.transferTo(0, fileChannel.size(), out);

            fileChannel.close();
        }

    }
}
