package com.julia.app;

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

        File dir = new File("./src/main/input");
        File []fileList = dir.listFiles();

        for(File f : fileList) {

            if(f.isFile()) {
                String tempPath = f.getParent();
                String tempFileName = f.getName();
                System.out.println("Path : " + tempPath);
                System.out.println("File name : " + tempFileName);

                String tempFileNameExcludeExtension = tempFileName.replace(".txt", "");
                System.out.println(tempFileNameExcludeExtension);

                try (FileReader reader = new FileReader(tempPath + "/" + tempFileName)) {

                    //file output
                    File file = new File(tempPath + "/" + tempFileNameExcludeExtension + 1 + ".txt");
                    File file2 = new File(tempPath + "/" + tempFileNameExcludeExtension + 2 + ".txt");

                    System.out.println();
                    System.out.println(file);
                    System.out.println(file2);

                    FileWriter fw = null;

                    //JSON parser object to parse read file
                    JSONParser jsonParser = new JSONParser();

                    //Read JSON file
                    Object obj = jsonParser.parse(reader);

                    JSONObject shards = (JSONObject) obj;
                    JSONObject hits = (JSONObject) shards.get("hits");
                    JSONArray results = (JSONArray) hits.get("hits"); //value of key or inner hits

                    fw = new FileWriter(file);
                    fw.write("{\"result\":");
                    fw.write("[");

                    int article_cnt = 0;
                    int check = 0; // check is 0 until opening a second file

                    double bytes, kilobytes, megabytes;

                    // operation per each article
                    for (int i = 0; i < results.size(); i++) {
                        JSONObject res = (JSONObject) results.get(i); // res = each article
                        JSONObject source = (JSONObject) res.get("_source"); // source = each source
                        source.toJSONString(); // change value form into jsonString

                        Object title = source.get("title");
                        Object text = source.get("text");

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

                        if(link_tmp.length==1 || link_tmp.length==0){
                            System.out.println(link_tmp.length);

                        }

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

                            if (link_cnt != link_tmp.length) {
                                // Because I split links by [[ ]] , use [[ as a delimiter in a json file
                                fw.write("[[");
                            }
                        }

                        fw.write("\"}");

                        //split into two files because of limit of indexing size
                        bytes = file.length();
                        kilobytes = (bytes / 1024);
                        megabytes = (kilobytes / 1024); //////

                        if (megabytes > 4 && check == 0) { /////

                            System.out.println();

                            fw.write("]}");

                            System.out.println("이제까지 데이터의 크기는");
                            System.out.println(megabytes); /////
                            System.out.println("새 파일이 열립니다.");

                            check = 1;
                            fw.close();

                            //start writing a second file
                            fw = new FileWriter(file2);
                            fw.write("{\"result\":");
                            fw.write("[");

                            //',' can not be added if a output file1 ends
                            //just count a current article and continue;
                            article_cnt++;
                            continue;

                        }

                        //There is no split delimiter ',' at the end of the data
                        article_cnt++;
                        if (article_cnt != results.size()) {
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
    }
}