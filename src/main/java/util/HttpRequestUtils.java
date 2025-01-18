package util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import model.RequestURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

public class HttpRequestUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpRequestUtils.class);
    private static final String DEFAULT_PATH = "/index.html";
    /**
     * URL 을 파싱한다.
     * @param url - 수신한 url이 없다면 디폴트 /index.html
     * @return Map<String,String> - path, queryString
     */
    public static Map<String,String> parseUrl(String url){
        int index = url.indexOf("?");
        if(index == -1 ){
            return Map.of("path", url, "queryString", "");
        }

        String path = url.substring(0, index);
        String queryString = url.substring(index+1);
        return Map.of("path",path, "queryString",queryString);
    }


    private static String getPath(String url){
        String[] parts = url.split(" ");
        return parts[1];
    }

    public static String getUrl(String firstLine) {
        if(Strings.isNullOrEmpty(firstLine) ){
            return DEFAULT_PATH;
        }

        String path = getPath(firstLine);
        if (path.equals("/")) {
            return DEFAULT_PATH;
        };

        return path;
    }
    /**
     * @param queryString은 URL에서 ? 이후에 전달되는 field1=value1&field2=value2 형식임
     * @return
     */
    public static Map<String, String> parseQueryString(String queryString) {
        return parseValues(queryString, "&");
    }

    /**
     * @param 쿠키
     *            값은 name1=value1; name2=value2 형식임
     * @return
     */
    public static Map<String, String> parseCookies(String cookies) {
        return parseValues(cookies, ";");
    }


    public static String getReturnUrl(boolean isLogin) {
        return isLogin ? "/index.html" : "/user/list.html";
    }

    private static Map<String, String> parseValues(String values, String separator) {
        if (Strings.isNullOrEmpty(values)) {
            return Maps.newHashMap();
        }

        String[] tokens = values.split(separator);
        return Arrays.stream(tokens).map(t -> getKeyValue(t, "=")).filter(p -> p != null)
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }

    public static String parseRequestedPage(CharSequence requestMessage){
        if(requestMessage==null){
            return "";
        }

        return requestMessage.toString().split(" ")[1];
    }


/*
    public static Map<String,String> parseRequestMessage(CharSequence requestMessage){
        if(requestMessage.isEmpty()){
            return Maps.newHashMap();
        }

        String[] message = requestMessage.toString().split(" ");
        Map<String, String> requestUri = parseRequestUri(message[1]);



        return Map.of("method", message[0]
                    , "path", requestUri.get("path")
                    , "queryString", requestUri.getOrDefault("queryString",""));

    }*/
    private static boolean hasParam(String url){
        return url.contains("?");
    }





    static Pair getKeyValue(String keyValue, String regex) {
        if (Strings.isNullOrEmpty(keyValue)) {
            return null;
        }

        String[] tokens = keyValue.split(regex);
        if (tokens.length != 2) {
            return null;
        }

        return new Pair(tokens[0], tokens[1]);
    }

    public static Pair parseHeader(String header) {
        return null;
    }

    public static int getContentLength(String s) {
        return Integer.parseInt(s.split(":")[1].trim());


    }

    public static String getAcceptHeader(String line) {
        String[] split = line.split(":");
        if(split.length > 1){
            return split[1].trim();
        }
        return "*/*";
    }

    public static class Pair {
        String key;
        String value;

        Pair(String key, String value) {
            this.key = key.trim();
            this.value = value.trim();
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Pair other = (Pair) obj;
            if (key == null) {
                if (other.key != null)
                    return false;
            } else if (!key.equals(other.key))
                return false;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "Pair [key=" + key + ", value=" + value + "]";
        }
    }
}
