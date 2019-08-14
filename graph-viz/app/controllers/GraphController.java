package controllers;


import play.libs.Json;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.PropertyResolver;
import play.mvc.*;

public class GraphController extends Controller {

    @Qualifier("propertyResolver")
    @Autowired
    private PropertyResolver propertyResolver;

    private static final String POST_URL = "http://128.195.10.93:8080/"; //simple-wiki
    //private static final String POST_URL = "http://128.195.10.93:9200/"; //en-wiki

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

        String text = "";
        int cnt_node = 0;
        int global_edges = 0;

        //ArrayNode which contains nodes_array and edges_array
        ArrayNode nodes_array = Json.newArray();
        ArrayNode edges_array = Json.newArray();

        for(String str : hits_list) {
            //source
            //extract source(title, text) in each source
            String source = str.substring(str.indexOf("source"), str.length() - 2);

            //title
            //{"ans":"source\": {\"title\": \"Computer terminal\""}
            String title = source.split(",")[0];

            //extract only a title in "title\": \"TITLE\""
            title = title.substring(title.indexOf("title") + 9, title.length() - 1);

            //node
            ObjectNode node = Json.newObject();
            node.put("id", title);
            node.put("label", title);
            node.put("x", Math.random());
            node.put("y", Math.random());
            node.put("size", 1);
            node.put("color", "#f00");


            title_list.add(title);
            nodes_array.add(node);

            //to_list
            String[] results = source.split(",", 2);
            text = results[1];

            //split using '[[' as a delimter in the text (because link is formed as '[[LINK]]'
            String[] list_tmp = text.split("\\[\\[");

            //find links in a article
            int flag = 0;
            for (String ele : list_tmp) {
                //맨 첫 원소는 쓰레기 값
                if (flag == 0) {
                    flag = 1;
                    continue;
                }
                String link = ele.split("\\]\\]")[0];

                ObjectNode edge = Json.newObject();
                if(title_list.contains(link)){

                }
                else{ //link라는 노드가 없으면
                    node = Json.newObject();
                    node.put("id", link);
                    node.put("label", link);
                    node.put("x", Math.random());
                    node.put("y", Math.random());
                    node.put("size", 1);
                    node.put("color", "#f00");

                    title_list.add(link); //노드생겻다고 추가해주고
                    nodes_array.add(node); //노드 실제로 집어넣음

                }
                edge.put("id", global_edges);
                edge.put("source", nodes_array.get(cnt_node).get("id"));
                edge.put("target", link);
                edge.put("size", 3);
                edge.put("color", "#00f");
                edge.put("type", "arrow");

                edges_array.add(edge);
                global_edges++;
            }

            cnt_node++; //0->1 (to_list 인덱스 접근때문에 후에 증가)
        }

        ObjectNode g =  Json.newObject();
        g.put("nodes", nodes_array);
        g.put("edges", edges_array);

        ObjectNode time = Json.newObject();
        time.put("startTime", startTime);
        time.put("endTime", endTime);

        //ObjectNode which contains graph g and time log
        ObjectNode ret = Json.newObject();
        ret.put("g", g);
        ret.put("time", time);

        return ok(ret);
    }
}