package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static util.HttpRequestUtils.*;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;



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

        } catch (Exception e) {
            log.error(e.getMessage());

        }
    }

    private void handleRequest(DataOutputStream dos, BufferedReader br) throws Exception {
        String line = br.readLine();
        log.debug("================================");
        log.debug("[status line] {} " ,line);
        if (line == null) {
            return;
        }

        String[] requestLine = line.split(" ");
        String method = requestLine[0].trim();
        String url = requestLine[1].trim();
        String mimeType = "";

        int contentLength = 0;
        Map<String, String> cookie = null;
        while (!line.isEmpty()) {
            line = br.readLine();
            log.debug("[request Header] {}" ,line);
            if (line.contains("Content-Length")) {
                contentLength = HttpRequestUtils.getContentLength(line);
            }

            if (line.contains("Cookie")) {
                cookie = HttpRequestUtils.parseCookies(line);
            }

            if(line.contains("text/css")){
                mimeType = getAcceptHeader(line);
            }
        }

        log.debug("================================");


        if(!mimeType.isEmpty()) {
            File file = new File("./webapp"+url);
            byte[] bytes = Files.readAllBytes(file.toPath());
            int fileSize = bytes.length;
            responseCss(dos, fileSize);
            responseBody(dos, bytes);
        }


        log.info("Content-Length : {} ", contentLength);
        // 요청 본문을 읽는다.
        String body = IOUtils.readData(br, contentLength);
        log.info("body : {}", body);

        Map<String, String> params = parseQueryString(body);
        if (url.startsWith("/user/create")) {
            User user = User.createUser(params);
            log.debug("user : {}", user);
            DataBase.addUser(user);

            url = "/index.html";
            byte[] bytes = Files.readAllBytes(new File("./webapp" + url).toPath());
            response302Header(dos, url);
            responseBody(dos, bytes);

        } else if (method.contains("GET") && url.contains("/user/login_failed")) {
            log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            url = "/user/login_failed.html";
            byte[] bytes = Files.readAllBytes(new File("./webapp" + url).toPath());
            response200Header(dos, bytes.length);
            responseBody(dos, bytes);
        }else if (url.equals("/") || url.startsWith("/index.html")) {
            url = "/index.html";
            byte[] bytes = Files.readAllBytes(new File("./webapp" + url).toPath());
            response200Header(dos, bytes.length);
            responseBody(dos, bytes);
        } else if (method.contains("GET") && url.startsWith("/user/login")) {
            url = "/user/login.html";
            byte[] bytes = Files.readAllBytes(new File("./webapp" + url).toPath());
            response200Header(dos, bytes.length);
            responseBody(dos, bytes);
        } else if (method.contains("POST") && url.startsWith("/user/login")) {
            User findedUser = DataBase.findUserById(params.get("userId"));
            log.info("user : {}", findedUser);
            if (!Objects.isNull(findedUser) && findedUser.getPassword().equals(params.get("password"))) {
                log.debug("user login success");
                url = "/index.html";
                byte[] bytes = Files.readAllBytes(new File("./webapp" + url).toPath());
                response302CookieHeader(dos, url, true );
                responseBody(dos, bytes);
            } else {
                log.debug("user login failed");
                url = "/user/login_failed.html";
                byte[] bytes = Files.readAllBytes(new File("./webapp" + url).toPath());
                response302CookieHeader(dos, url, false );
                // response200loginFailedHeader(dos);
                responseBody(dos, bytes);
            }

        } else if(method.contains("GET") && url.startsWith("/user/list")) {

            boolean isLogin = Boolean.parseBoolean(Objects.requireNonNull(cookie).get("Cookie: logined"));
            log.info("isLogin : {}", isLogin);
            if(isLogin) {
                StringBuilder sb = new StringBuilder();
                url = "/user/list.html";
                List<User> userList = new ArrayList<>(DataBase.findAll());
                int rownum = 3;
                String htmlContent = new String (Files.readAllBytes(new File("./webapp" + url).toPath()));

                for (User user : userList) {
                    sb.append("<tr>")
                    .append("<th>").append(rownum++).append("</th>")
                            .append("<td>").append(user.getUserId()).append("</td>")
                            .append("<td>").append(user.getName()).append("</td>")
                            .append("<td>").append(user.getEmail()).append("</td>")
                            .append("<td><a href ='#' class='btn btn-success' role ='button'> ").append("수정").append("</a></td>")
                    .append("</tr>");
                }

                String updatedHtml = htmlContent.replace("</tbody>", sb + "</tbody>");
                log.info("htmlContent = {} ", htmlContent);

                // 수정된 내용을 다시 쓴다.
                byte[] bytes = Files.readAllBytes(Files.write(Paths.get("./webapp" + url), updatedHtml.getBytes()));

                response200Header(dos, bytes.length);
                responseBody(dos, bytes);

            } else {
                url = "/index.html";
                byte[] bytes = Files.readAllBytes(new File("./webapp" + url).toPath());
                response200Header(dos, bytes.length);
                responseBody(dos, bytes);
            }

        }  else {
            byte[] bytes = Files.readAllBytes(new File("./webapp" + url).toPath());
            response200Header(dos, bytes.length);
            responseBody(dos, bytes);
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String body) {
        try {
            log.info("200 OK");
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
            dos.writeBytes(body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    private void response302Header(DataOutputStream dos, String path) throws IOException {
        log.info("302 redirect to {}", path);
        dos.writeBytes("HTTP/1.1 302 Found\r\n");
        dos.writeBytes("Location:" + path + "\r\n");
        dos.writeBytes("Content-Type: text/html; charset=utf-8\r\n");
        dos.writeBytes("\r\n");
    }

    private void responseCss( DataOutputStream dos, int lengthOfBodyContent) throws IOException {
        dos.writeBytes("HTTP 1.1 200 ok \r\n");
        dos.writeBytes("content-type: text/css\r\n");
        dos.writeBytes("content-length: " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
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


    /**
     * Cookie 세팅 응답
     * @param dos
     * @param location
     * @param isLogin
     */
    private void response302CookieHeader(DataOutputStream dos, String location, boolean isLogin) {
        try {
            log.info("302 Found");
            log.info("location: {}", location);
            dos.writeBytes("HTTP/1.1 303 See other \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: logined=" + isLogin + "\r\n");
            dos.writeBytes("Location: "+location+"\r\n");
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
