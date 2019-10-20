import java.io.File;
import java.util.List;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.FileWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class App{
   public static void main(String[] args) {
 
       Path path = Paths.get("./src/main/java/enwiki.txt");  

       Charset cs = StandardCharsets.UTF_8;

       List<String> in = new ArrayList<String>();

       try{
           // read all content in a file
           in = Files.readAllLines(path,cs); 
       }catch(IOException e){
           e.printStackTrace();
       }

       String content = in.toString();

       // write a file
       File file = new File("./src/main/java/enwiki_out.txt");
       FileWriter fw = null;
       try {
           fw = new FileWriter(file);
           fw.write("{ \"result\" : \"[");

           // parse links from a text
           // find 'hits' from the 140th index in the response (starts finding after 'first' hits)
           String hits_str = content.substring(content.indexOf("hits",140), content.length()-2);

           // raw data in core 'hits', split by delimter '},' in the article.
           String hits_list[] = hits_str.split("}},\\{\"_index");

           int hits_cnt = 0;

           for(String str : hits_list) {

               hits_cnt ++;

               //source
               //extract source(title, text) in each source
               String source = str.substring(str.indexOf("\"_source\""), str.length() - 2);
              
               //title
               //{"ans":"source\": {\"title\": \"Computer terminal\""}
               String title = source.split(", \"tex")[0];  

               //extract only a title in "title\": \"TITLE\""
               title = title.substring(title.indexOf("title") + 9, title.length() - 1);
               // System.out.println("title");
               // System.out.println(title);

               //put title key&value of each element into the output file
               fw.write("{ \"title\" : \""); // { title : "
               fw.write(title); // { title : "windows phone
               fw.write("\", \"text\" : \""); // { title : "windows phone", text : "

               //text
               String text = source.split(", \"text\": \"")[1];
               text = text.split("\", \"link\":")[0];

               fw.write(text);
               fw.write("\", \"link\":\"[");

               //to_link
               //split using '[[' as a delimiter in the text (because link is formed as '[[LINK]]'
               String[] link_tmp = text.split("\\[\\["); 

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

                   fw.write(link);

                   if(link_cnt != link_tmp.length) {
                       // used "[[" as delimiter because I splited the links by "[[ ]]"
                       fw.write("[[");  
                   }

               }
               //the end of an artile
               fw.write("]\"}");

               if(hits_cnt != hits_list.length) {
                   fw.write(",");
               }
           }

           fw.write("]\"}");
           fw.close();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }
}