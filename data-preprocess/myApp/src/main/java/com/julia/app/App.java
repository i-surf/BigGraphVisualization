import java.util.*;
import java.io.*;
import java.io.File;
import java.lang.*;
import java.io.IOException;

import java.lang.String;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class App{
    public static void main(String[] args) {
        //file output
        File file = new File("./src/main/java/enwiki_out1.txt");
        File file2 = new File("./src/main/java/enwiki_out2.txt");

        FileWriter fw = null;

        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("./src/main/java/out2.txt"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONObject shards = (JSONObject) obj;
            JSONObject hits = (JSONObject) shards.get("hits");
            JSONArray results = (JSONArray) hits.get("hits"); //value of key or inner hits

            fw = new FileWriter(file);
            fw.write("{\"result\":");
            fw.write("[");

            int article_cnt = 0; 

            // operation per each article
            for(int i=0; i<results.size(); i++){
                JSONObject res = (JSONObject) results.get(i); // res = each article
                JSONObject source = (JSONObject) res.get("_source"); // source = each source
                source.toJSONString(); // change value form into jsonString

                Object title = source.get("title");
                Object text = source.get("text");

                System.out.println("title : " + title);

                //write title and link into the output file
                fw.write("{");
                fw.write("\"title\":\"");
                fw.write(title.toString());
                fw.write("\",\"text\":\"");
                fw.write(text.toString());
                fw.write("\",\"link\":\"");

                //to_link
                //split using '[[' as a delimiter in the text (because link is formed as '[[LINK]]'
                String str = text.toString();
                String[] link_tmp = str.split("\\[\\[");

                //find links in a article
                int flag = 0;
                int link_cnt = 1; // list_tmp.len = cnt(list) + 1
                for (String ele : link_tmp) {
                    // first element is a garbage value
                    if (flag == 0) {
                        flag = 1;
                        continue;
                    }
                    link_cnt++;
                    String link = ele.split("\\]\\]")[0];

                    //write links into the output file
                    fw.write(link);

                    if(link_cnt != link_tmp.length) {
                        // Because I split links by [[ ]] , use [[ as a delimiter in a json file
                        fw.write("[[");
                    }
                }

                fw.write("\"}");

                //There is no split delimiter ',' at the end of the data
                article_cnt ++;
                if(article_cnt != results.size()){
                    fw.write(",");
                }
            }

            fw.write("]}");
            fw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

