package util;

import com.google.common.base.Strings;
import exception.PageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public class IOUtils {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    /**
     * @param BufferedReader 는 Request Body를 시작하는 시점이어야
     * @param contentLength 는 request Header의 Content-Length 값이다.
     * @return
     * @throws IOException
     */
    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }

    public static byte [] readRequestBody(String path) throws IOException {
        if(Strings.isNullOrEmpty(path)){
            throw new IllegalArgumentException("path is null or Empty" + path );
        }

        File file = new File("./webapp" + path);

        if(file.exists()){
            return Files.readAllBytes(file.toPath());
        }

        throw new PageNotFoundException("page not found : " + path  );

    }

}
