package controllers;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.PropertyResolver;
import play.mvc.*;

public class GraphController extends Controller {

    @Qualifier("propertyResolver")
    @Autowired
    private PropertyResolver propertyResolver;
    private static final String POST_URL = "http://128.195.10.93:8080/";

    public Result index(String query) throws IOException {

        List<String> title_list = new ArrayList<String>();

        URL obj = new URL(POST_URL);

        // print startTime after post request
        // currentTimeMillis() returns the current time in milliseconds
        long startTime = System.currentTimeMillis();

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");

        String urlParameters = "{\"keyword\" : \"" + query + "\"}";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();

        // print endTime after response
        long endTime = System.currentTimeMillis();

//        System.out.println("\nSending 'POST' request to URL : " + obj);
//        System.out.println("Post parameters : " + urlParameters);
//        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // find 'hits' from the 140th index in the response (starts finding after 'first' hits)
        String hits_str = response.substring(response.indexOf("hits",140), response.length()-2);

        // raw data in core 'hits', split by delimter '},' in the article.
        String hits_list[] = hits_str.split("},");

        int cnt_tmp = 0;
        ObjectNode result = Json.newObject();
        String text = "";

        String to_list = "";
        /*
           is hard to parse in javascript , so
             [ㅁ,ㅁ]  [ㅁ]  [ㅁ,ㅁ,ㅁ] is same, but
             [ㅁ,ㅁ],,[ㅁ],,[ㅁ,ㅁ,ㅁ],, => 'list.toString() + ,,' is a string, easy to parse
         */

        for(String str : hits_list) {
            //source
            //extract source(title, text) in each source
            String source = str.substring(str.indexOf("source"), str.length() - 2);

            //title
            //{"ans":"source\": {\"title\": \"Computer terminal\""}
            String title = source.split(",")[0];

            //extract only a title in "title\": \"TITLE\""
            title = title.substring(title.indexOf("title") + 9, title.length() - 1);

            //add the title to the title list
            title_list.add(title);

            //to_list
            //lists in each 'to_list'
            List<String> link_list = new ArrayList<String>();

            String[] results = source.split(",",2);
            text = results[1];

            //split using '[[' as a delimter in the text (because link is formed as '[[LINK]]'
            String[] list_tmp = text.split("\\[\\[");

            cnt_tmp = list_tmp.length;

            //find links in a article
            int flag = 0;
            for (String ele :list_tmp) {
                //맨 첫 원소는 쓰레기 값
                if (flag == 0){
                    flag =1;
                    continue;
                }
                String ele_tmp = ele;
                String link = ele_tmp.split("\\]\\]")[0];

                link_list.add(link);
            }

            to_list = to_list + link_list.toString();
            to_list = to_list + ",,";
        }

        result.put("title_list", title_list.toString());
        result.put("to_list", to_list);
        result.put("startTime", startTime);
        result.put("endTime", endTime);

        //print result
        System.out.println(response.toString());

        return ok(result);
    }
}