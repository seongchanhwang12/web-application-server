package webserver;

import java.io.*;
import java.net.Socket;
import static util.HttpRequestUtils.*;
import static util.IOUtils.readRequestBody;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             OutputStream out = connection.getOutputStream();
              DataOutputStream dos = new DataOutputStream(out)
        ) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            handleRequest(dos, br);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    private void handleRequest(DataOutputStream dos, BufferedReader br ) throws IOException {
        StringBuilder request = readRequest(br);

        String pageUrl = parseRequestedPage(request);

        byte[] body = readRequestBody(pageUrl);
        sendResponse(dos, body);
    }

    private void sendResponse(DataOutputStream dos, byte[] body){
        response200Header(dos, body.length);
        responseBody(dos, body);

    }

    private StringBuilder readRequest(BufferedReader br ) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()){
            sb.append(line).append("\n");
        }
        return sb;
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
