package controllers;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.InetSocketAddress;


import play.libs.Json;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

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
        System.out.println("\nSending 'POST' request to URL : " + obj);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //응답의 140번째 인덱스부터 hits를 찾음 (그러면 첫번째 hits를 지나서 찾기 시작)
        String hits_str = response.substring(response.indexOf("hits",140), response.length()-2);

        //핵심 hits 안에 raw data들이 있는데, 기사의 구분자인 }, 로 나눔
        String hits_list[] = hits_str.split("},");

        int cnt_tmp = 0;
        ObjectNode result = Json.newObject();
        String text = "";

        String to_list = "";
        /*
           [ [ㅁ,ㅁ], [ㅁ], [ㅁ,ㅁ,ㅁ] ] 가 js에서 ,으로 파싱하기 힘들어서
             [ㅁ,ㅁ]  [ㅁ]  [ㅁ,ㅁ,ㅁ] 은 똑같이 만드는데
             [ㅁ,ㅁ],,[ㅁ],,[ㅁ,ㅁ,ㅁ],, 처럼 'list.toString() + ,,'로 스트링으로 저장하면 파싱하기 쉬울 듯
        */

        for(String str : hits_list) {
            //source
            //각 str에서 source(title, text)추출
            String source = str.substring(str.indexOf("source"), str.length() - 2);

            //title
            //{"ans":"source\": {\"title\": \"Computer terminal\""}
            String title = source.split(",")[0];

            //"title\": \"제목\""에서 제목만 추출
            title = title.substring(title.indexOf("title") + 9, title.length() - 1);

            //add the title to the title list
            title_list.add(title);

            //to_list
            //각 to_list안의 리스트들
            List<String> link_list = new ArrayList<String>();

            //source에서 title과 text가 ,으로 구분되어있는데 limit 인자를 안넣으면 text안의 ,들도 쪼개짐
            //limit는 split으로 만들어지는 리스트의 최대 원소 수
            String[] results = source.split(",",2);
            text = results[1];

            //text안에서 [[를 구분자로 split (link들이 [[링크]] 형태이므로)
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

        result.put("////// 디버깅 //////", cnt_tmp);
        result.put("title_list", title_list.toString());
        result.put("to_list", to_list);

        //print result
        System.out.println(response.toString());

        return ok(result);
    }
}