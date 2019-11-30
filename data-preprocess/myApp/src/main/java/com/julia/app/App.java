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

public class App {
    public static void main(String[] args) {

        File dir = new File("./src/main/java/com/julia/app/lastInput");
        File[] fileList = dir.listFiles();
        int cnt = 0;

        for (File f : fileList) {
            if (f.isFile()) {
                cnt++;
                String tempPath = f.getParent();
                String tempFileName = f.getName();
                System.out.println("Path : " + tempPath);
                System.out.println("File name : " + tempFileName);

                String tempFileNameExcludeExtension = tempFileName.replace(".txt", "");
                System.out.println(tempFileNameExcludeExtension);

                try (FileReader reader = new FileReader(tempPath + "/" + tempFileName)) {

                    JSONParser jsonParser = new JSONParser();

                    //Read JSON file
                    Object obj = jsonParser.parse(reader);
                    JSONObject shards = (JSONObject) obj;
                    JSONObject hits = (JSONObject) shards.get("hits");
                    JSONArray hits2 = (JSONArray) hits.get("hits");
                    JSONObject hits2_arrayToJson = (JSONObject) hits2.get(0); //value of key or inner hits
                    JSONObject source = (JSONObject) hits2_arrayToJson.get("_source"); //value of key or inner hits
                    JSONArray result = (JSONArray) source.get("result");

                    int id = 1;

                    for(int i=0; i<result.size(); i++){
                        JSONObject res = (JSONObject) result.get(i); // res = each article
                        res.toJSONString(); // change value form into jsonString

                        Object title = res.get("title");
                        Object text = res.get("text");
                        Object link = res.get("link");

                        File file = new File("./src/main/java/com/julia/app/"+cnt+"/"+tempFileNameExcludeExtension+"_id"+id+".txt");
                        FileWriter fw = new FileWriter(file);

                        System.out.println("file cnt : " + cnt);

                        fw.write("{");
                        fw.write("\"title\":\"");
                        fw.write(title.toString());
                        fw.write("\",\"text\":\"");
                        fw.write(text.toString());
                        fw.write("\",\"link\":\"");
                        fw.write(link.toString());
                        fw.write("\"}");

                        fw.close();
                        id++;
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
