package util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;


public class IOUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(IOUtilsTest.class);

    @Test
    public void readData() throws Exception {
        String data = "abcd123";
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);

        logger.debug("parse body : {}", IOUtils.readData(br, data.length()));
    }

    @Test
    public void readHtml() throws IOException {
        String url = "/index.html";

        File file = new File("./webapp" + url);
        // 절대경로 File file = new File("D:\\study\\web-application-server\\webapp");
        assertTrue("파일이 존재하지 않습니다:" + file.getAbsolutePath(), file.exists());

        // 파일 내용 읽기
        byte[] bytes = Files.readAllBytes(file.toPath());
        String content = new String(bytes, StandardCharsets.UTF_8);

    assertFalse("파일 내용이 비어있습니다.", content.isEmpty());
}
}
