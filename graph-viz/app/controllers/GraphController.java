package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.PropertyResolver;
import play.mvc.*;
import play.twirl.api.Html;
import views.html.*;

import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;



/*
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

*/


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */

public class GraphController extends Controller {

    @Qualifier("propertyResolver")
    @Autowired
    private PropertyResolver propertyResolver;

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result index(String query) throws IOException {

        //URL url = new URL("http://localhost:9200/wiki/test/" + query + "?pretty");

        //dataset.json -> test.json (동준오빠가 만든거)

        URL url = new URL("http://localhost:9200/wiki/test/_search?q=text:" + query);
        //URL url = new URL("http://128.195.10.93:8080?keyword=" + query);


        //data.json (혜림언니가 만든거)
        //dataset.json (500메)
        //URL url = new URL("http://localhost:9200/test/page/_search?q=text:" + query);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        // 문자열로 URL 표현
        System.out.println("URL :" + url.toExternalForm());

        // HTTP Connection 구하기
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // 요청 방식 설정 ( GET or POST or .. 별도로 설정하지않으면 GET 방식 )
        conn.setRequestMethod("GET");

        // 연결 타임아웃 설정
        conn.setConnectTimeout(3000); // 3초
        // 읽기 타임아웃 설정
        conn.setReadTimeout(3000); // 3초

        // 요청 방식 구하기
        System.out.println("getRequestMethod():" + conn.getRequestMethod());
        // 응답 콘텐츠 유형 구하기
        System.out.println("getContentType():" + conn.getContentType());
        // 응답 코드 구하기
        System.out.println("getResponseCode():"    + conn.getResponseCode());
        // 응답 메시지 구하기
        System.out.println("getResponseMessage():" + conn.getResponseMessage());

        // 응답 헤더의 정보를 모두 출력
        for (Map.Entry<String, List<String>> header : conn.getHeaderFields().entrySet()) {
            for (String value : header.getValue()) {
                System.out.println(header.getKey() + " : " + value);
            }
        }

        System.out.println("hello");



        // 응답 내용(BODY) 구하기
        try (InputStream in = conn.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            ObjectNode result = Json.newObject();

            byte[] buf = new byte[1024 * 8];
            int length = 0;
            while ((length = in.read(buf)) != -1) { //buf로 읽는 InputStream의 내용이 없을때까지 out에 담는다.
                out.write(buf, 0, length);
            }
            result.put("query", query); //브라우저에 내용 출력하기
            result.put("body", out.toString()); //내용이 담긴 out을 json객체인 result에 담는다.

            // 접속 해제
            conn.disconnect();
            return ok(result);
        }



        /*

        ArrayNode replies = Json.newArray();
        try {
            Class.forName("org.postgresql.Driver"); //환경변수를 포함하는 JAR 파일들 중 이 클래스를 찾는
            Connection conn = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/graphtweet",
                            "graphuser", "graphuser");  //인자인 DB URL을 가지고 DB에 연결
                         //이건 다른 웹서버의 엘라스틱에 연결하는거고, 로컬에 엘라스틱 깔면 더 빠르다.

            Statement statement = conn.createStatement(); //DB에 보낼 SQL statement object를 만
            ResultSet resultSet = statement.executeQuery("select " +
                    "from_longitude, from_latitude, " +
                    "to_longitude, to_latitude " +
                    "from replytweets where " +
                    "to_tsvector('english', from_text) " +
                    "@@ to_tsquery('"+query+"') or " +
                    "to_tsvector('english', to_text) " +
                    "@@ to_tsquery('"+query+"');");
            //주어진 SQL문을 실행하고, ResultSet 객체를 반환

            while (resultSet.next()) { //SQL결과인 ResultSet의 다음줄로 이동
                ObjectNode reply = Json.newObject();
                ArrayNode fromCoordinate = Json.newArray();
                fromCoordinate.add(resultSet.getFloat("from_longitude"));
                fromCoordinate.add(resultSet.getFloat("from_latitude"));
                reply.set("source", fromCoordinate);
                ArrayNode toCoordinate = Json.newArray();
                toCoordinate.add(resultSet.getFloat("to_longitude"));
                toCoordinate.add(resultSet.getFloat("to_latitude"));
                reply.set("target", toCoordinate);
                replies.add(reply);
            }
            resultSet.close();
            statement.close();
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ok(Json.toJson(replies));

        */
    }
}