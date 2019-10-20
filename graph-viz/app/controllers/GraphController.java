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

            if(title_list.contains(title)){

            }
            else {
                node.put("id", title);
                node.put("label", title);
                node.put("x", Math.random());
                node.put("y", Math.random());
                node.put("size", 4);
                node.put("color", "#00f");

                title_list.add(title);
                nodes_array.add(node);
            }

            //to_list
            String[] results = source.split(",", 2);
            text = results[1];

            //split using '[[' as a delimter in the text (because link is formed as '[[LINK]]'
            String[] list_tmp = text.split("\\[\\[");

            //find links in a article
            int flag = 0;
            for (String ele : list_tmp) {
                // first element is a garbage value
                if (flag == 0) {
                    flag = 1;
                    continue;
                }
                String link = ele.split("\\]\\]")[0];

                ObjectNode edge = Json.newObject();
                if(title_list.contains(link)){

                }

                // if there is no 'link' node
                else{
                    node = Json.newObject();
                    node.put("id", link);
                    node.put("label", link);
                    node.put("x", Math.random());
                    node.put("y", Math.random());
                    node.put("size", 4);
                    node.put("color", "#f00");

                    //mark adding a node 'link'
                    title_list.add(link);

                    // real putting the node into nodes array
                    nodes_array.add(node);

                }
                edge.put("id", global_edges);
                edge.put("source", title);
                edge.put("target", link);
                edge.put("size", 3);
                edge.put("color", "#000");
                edge.put("type", "arrow");

                edges_array.add(edge);
                global_edges++;
            }

            // make 0 to 1 (after access to 'to_list' index)
            cnt_node++;
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