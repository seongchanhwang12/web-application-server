package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Map;

import static util.HttpRequestUtils.*;
import static util.IOUtils.readRequestBody;

import model.User;
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
              DataOutputStream dos = new DataOutputStream(connection.getOutputStream())
        ) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            handleRequest(dos, br);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    private void handleRequest(DataOutputStream dos, BufferedReader br ) throws IOException {

        StringBuilder sb = readRequest(br);
        log.debug("http message : {} ",sb);
        log.debug("=======================");


        String[] httpMessages = sb.toString().split("\n");
        log.debug("message[0] - {}",httpMessages[0]);

        String url = HttpRequestUtils.getUrl(httpMessages[0]);
        Map<String, String> parts = parseUrl(url);
        String queryString = parts.get("queryString");
        Map<String, String> params = parseQueryString(queryString);

        User user = new User(params.get("userId "), params.get("password"), params.get("name"), params.get("email"));

        byte[] body = readRequestBody(parts.get("path"));
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
