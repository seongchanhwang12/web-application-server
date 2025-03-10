package util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

import model.User;
import org.junit.Test;

import org.junit.jupiter.api.DisplayName;
import util.HttpRequestUtils.Pair;


public class HttpRequestUtilsTest {


    @Test public void getContentLength(){
        // given
        String httpRequest = "Content-Length: 59\n";

        // when
        int contentLength = HttpRequestUtils.getContentLength(httpRequest);

        // then
        assertThat(contentLength, is(59));


    }


    @Test public void parseUrl_null(){
        String url = "/index.html";
        Map<String, String> parts = HttpRequestUtils.parseUrl(url);
        assertThat(parts.get("path"), is("/index.html"));
        assertThat(parts.get("queryString"), is(""));
    }

    @Test public void parseUrl(){
        String url = "/user/create?userId=javajigi&password=password&name=chan";
        Map<String, String> parts = HttpRequestUtils.parseUrl(url);
        assertThat(parts.get("path"), is("/user/create"));
        assertThat(parts.get("queryString"), is("userId=javajigi&password=password&name=chan"));
    }

    @Test public void getUrl_path_null(){
        String firstLine = "GET / HTTP/1.1";
        String url = HttpRequestUtils.getUrl(firstLine);
        assertThat(url , is("/index.html"));
    }

    @Test public void getUrl_null(){
        String firstLine = "";
        String url = HttpRequestUtils.getUrl(firstLine);
        assertThat(url , is("/index.html"));
    }

    @Test public void getUrl(){
        String message = "GET /index.html HTTP/1.1\nHost: localhost:8080\nConnection: keep-alive\nAccept: */*";
        String[] splited = message.split(" ");
        assertThat(splited[1] , is("/index.html"));
    }

    @Test public void getAcceptHeader(){
        //given
        String line = "Accept: text/css, */*;q=0.1";
        String[] tokens = line.split(":");

        String value = HttpRequestUtils.getAcceptHeader(line);
        assertThat(value , is("text/css, */*;q=0.1"));
    }

    @Test public void getMimeType(){

       // int start = value.indexOf("text/css");
        //int end = value.indexOf(",");
        //String mimeType = value.substring(start, end);
    }

        // text/css면, 응답을 text/css로 한다.





/*

    @Test
    public void parseRequestUri(){
        String firstLine = "GET /user/create?userId=javajigi&password=password&name=chan\nHTTP/1.1";
        String url = HttpRequestUtils.getUrl(firstLine);
        assertThat(uriMap.get("path"),is( "/user/create"));
        assertThat(uriMap.get("queryString"),is( "userId=javajigi&password=password"));
    }
/*
    @Test
    public void parseRequestUri_null(){
        Map<String, String> uri = HttpRequestUtils.parseRequestUri(null);
        assertThat(uri.isEmpty(),is(true));

        uri = HttpRequestUtils.parseRequestUri("");
        assertThat(uri.isEmpty(),is(true));

        uri = HttpRequestUtils.parseQueryString(" ");
        assertThat(uri.isEmpty(),is(true));
    }

*/
    @Test
    public void parseRequestedPage() {
        String httpRequestMessage = "GET /index.html HTTP/1.1\nHost: localhost:8080\nConnection: keep-alive\nAccept: */*";
        String index = HttpRequestUtils.parseRequestedPage(httpRequestMessage);
        assertThat(index, is("/index.html"));
    }
    @Test
    public void parseQueryString() {
        String queryString = "userId=javajigi";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is(nullValue()));

        queryString = "userId=javajigi&password=password2";
        parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is("password2"));

        queryString = "userId=javajigi&password=password&name=chan&email=chan9301@naver.com";

        parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("email"), is("chan9301@naver.com"));
        assertThat(parameters.get("name"), is("chan"));

    }

    @Test
    public void parseQueryString_null() {
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(null);
        assertThat(parameters.isEmpty(), is(true));

        parameters = HttpRequestUtils.parseQueryString("");
        assertThat(parameters.isEmpty(), is(true));

        parameters = HttpRequestUtils.parseQueryString(" ");
        assertThat(parameters.isEmpty(), is(true));
    }

    @Test
    public void parseQueryString_invalid() {
        String queryString = "userId=javajigi&password";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is(nullValue()));
    }

    @Test
    public void parseCookies() {
        String cookies = "logined=true; JSessionId=1234";
        Map<String, String> parameters = HttpRequestUtils.parseCookies(cookies);
        assertThat(parameters.get("logined"), is("true"));
        assertThat(parameters.get("JSessionId"), is("1234"));
        assertThat(parameters.get("session"), is(nullValue()));
    }

    @Test
    public void getKeyValue() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId=javajigi", "=");
        assertThat(pair, is(new Pair("userId", "javajigi")));
    }

    @Test
    public void getKeyValue_invalid() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId", "=");
        assertThat(pair, is(nullValue()));
    }

    @Test
    public void parseHeader() throws Exception {
        String header = "Content-Length: 59";
        Pair pair = HttpRequestUtils.parseHeader(header);
        assertThat(pair, is(new Pair("Content-Length", "59")));
    }
}
