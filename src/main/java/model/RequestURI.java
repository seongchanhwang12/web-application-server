package model;

import util.HttpRequestUtils;

import java.util.Map;

public class RequestURI {

    public RequestURI() {}

    public RequestURI(String path) {
        this.path = path;
    }

    public RequestURI(String path, Map<String, String> params) {
        this.path = path;
        this.params = params;
    }

    public static RequestURI createRequestURI(String url){
        return isStaticResource(url)
                ? new RequestURI(url)
                : new RequestURI();
    }

    private void parseRequestedPage(String url){
        int index = url.indexOf("?");
        this.setPath(url.substring(0, index));
        Map<String, String> stringStringMap = HttpRequestUtils.parseQueryString(url.substring(index + 1));
        this.setParams(stringStringMap);
    }

    private static boolean isStaticResource(String url){
        return url.endsWith(".html");
    }

    private String path;
    private Map<String,String> params;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }


}
