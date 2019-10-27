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
                String path = f.getParent();
                String fileName = f.getName();
                System.out.println();
                System.out.println("Path : " + path);
                System.out.println("File name : " + fileName);
                String outputPath = path.replace("input", "input/");

                String fileNameExcludeExtension = fileName.replace(".txt", "");
                System.out.println(fileNameExcludeExtension);

                try (FileReader reader = new FileReader(path + "/" + fileName)) {

                    //file output
                    File file = new File(outputPath + fileNameExcludeExtension + "_1" + ".txt");
                    File file2 = new File(outputPath + fileNameExcludeExtension + "_2" + ".txt");

                    System.out.println();
                    System.out.println(file);
                    System.out.println(file2);

                    FileWriter fw = null;

                     //JSON parser object to parse read file
                    JSONParser jsonParser = new JSONParser();

                     //Read JSON file
                    Object obj = jsonParser.parse(reader);

                    JSONObject shards = (JSONObject) obj;
                    System.out.println();
                    System.out.println(shards);

                    JSONArray results = (JSONArray) shards.get("result");
                    System.out.println(results);

                    fw = new FileWriter(file);
                    fw.write("{\"result\":");
                    fw.write("[");


                    int article_cnt = 0;
                    int check = 0;

                    double bytes, kilobytes, megabytes;

                    // operation per each article
                    for (int i = 0; i < results.size(); i++) {
                        JSONObject res = (JSONObject) results.get(i); //각 기사
                        System.out.println(res);

                        Object title = res.get("title");
                        Object text = res.get("text");
                        Object link = res.get("link");
 
                        //write title and link into first output file
                        fw.write("{");
                        fw.write("\"title\":\"");
                        fw.write(title.toString());
                        fw.write("\",\"text\":\"");
                        fw.write(text.toString());
                        fw.write("\",\"link\":\"");

                        fw.write(link.toString());
                        fw.write("\"}");

                        //get file size so far
                        bytes = file.length();
                        kilobytes = (bytes / 1024);
                        megabytes = (kilobytes / 1024);

                        //if first file size exceeds a standard, second file is open
                        if (megabytes > 70 && check == 0) { 
                            fw.write("]}");

                            System.out.println("first file's datasize so far is");
                            System.out.println(megabytes);
                            System.out.println("a second file is open");

                            check = 1;
                            fw.close();

                            fw = new FileWriter(file2);
                            fw.write("{\"result\":");
                            fw.write("[");

                            article_cnt++;
                            continue;
                        }

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
