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

       File dir = new File("./src/main/java/com/julia/app/input");
       File []fileList = dir.listFiles();

       for(File f : fileList) {

           if(f.isFile()) {
               String path = f.getParent();
               String fileName = f.getName();
               System.out.println();
               System.out.println("Path : " + path);
               System.out.println("File name : " + fileName);

               String outputPath = path.replace("input", "input_2/");

               String line;
               ArrayList<String> lines = new ArrayList<String>();

               BufferedReader br = null;
               FileWriter fw = null;
               BufferedWriter bw = null;
              
               try (FileReader fr = new FileReader(path + "/" + fileName)) {

                   br = new BufferedReader(fr);

                   File file = new File(outputPath + fileName);
                   fw =  new FileWriter(file);
                   bw = new BufferedWriter(fw);

                   int cnt = 0;
                   while ((line = br.readLine()) != null) {
                       cnt++;
                       System.out.println(cnt);
                       //System.out.println(line);
                      
                       //일단 \를 제외한다. (오류나서)
                      
                       line = line.replaceAll("\t", "");

                       line = line.replaceAll("\u008A", "");
                       line = line.replaceAll("\u009A", "");

                       line = line.replaceAll("\u008B", "");
                       line = line.replaceAll("\u009B", "");

                       line = line.replaceAll("\f", "");
                       line = line.replaceAll("\u008C", "");
                       line = line.replaceAll("\u009C", "");

                       line = line.replaceAll("\u008D", "");
                       line = line.replaceAll("\u009D", "");

                       line = line.replaceAll("\u008E", "");
                       line = line.replaceAll("\u009E", "");

                       line = line.replaceAll("\b", "");

                       line = line.replaceAll("\u0080", "");
                       line = line.replaceAll("\u0081", "");
                       line = line.replaceAll("\u0082", "");
                       line = line.replaceAll("\u0083", "");
                       line = line.replaceAll("\u0084", "");
                       line = line.replaceAll("\u0085", "");
                       line = line.replaceAll("\u0086", "");
                       line = line.replaceAll("\u0087", "");
                       line = line.replaceAll("\u0088", "");
                       line = line.replaceAll("\u0089", "");

                       line = line.replaceAll("\u0090", "");
                       line = line.replaceAll("\u0091", "");
                       line = line.replaceAll("\u0092", "");
                       line = line.replaceAll("\u0093", "");
                       line = line.replaceAll("\u0094", "");
                       line = line.replaceAll("\u0095", "");
                       line = line.replaceAll("\u0096", "");
                       line = line.replaceAll("\u0097", "");
                       line = line.replaceAll("\u0098", "");
                       line = line.replaceAll("\u0099", "");

                       line = line.replaceAll("\u008F", "");
                       line = line.replaceAll("\u009F", "");

                       bw.write(line);
                       bw.flush();
                   }

               } catch (FileNotFoundException e) {
                   e.printStackTrace();
               } catch (IOException e) {
                   e.printStackTrace();
               }

               finally {
                   try {
                       if (br != null)
                           br.close();
                   } catch (IOException e) {
                   }
                   try {
                       if (bw != null)
                           bw.close();
                   } catch (IOException e) {
                   }
               }
           }
       }
   }
}
