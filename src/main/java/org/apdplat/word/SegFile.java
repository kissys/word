/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.word;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apdplat.word.dictionary.Dictionary;
import org.apdplat.word.dictionary.DictionaryFactory;

/**
 * 基于词典的正向最大匹配算法
 * @author 杨尚川
 */
public class SegFile {
    private static final Dictionary DIC = DictionaryFactory.getDictionary();
    private static int MAX_LENGTH=6;
    static{
        try {
            String dicPath = System.getProperty("dic.path");
            if(dicPath == null){
                dicPath = "dic.txt";
            }
            System.out.println("开始初始化词典");
            int max=1;
            int count=0;
            List<String> lines = Files.readAllLines(Paths.get(dicPath), Charset.forName("utf-8"));
            for(String line : lines){
                DIC.add(line);
                count++;
                if(line.length()>max){
                    max=line.length();
                }
            }
            MAX_LENGTH = max;
            System.out.println("完成初始化词典，词数目："+count);
            System.out.println("最大分词长度："+MAX_LENGTH);
        } catch (IOException ex) {
            System.err.println("词典装载失败:"+ex.getMessage());
        }        
    }
    public static void main(String[] args) throws Exception{
        String input = "input.txt";
        String output = "output.txt";
        if(args.length == 2){
            input = args[0];
            output = args[1];
        }
        File in = new File(input);
        File out = new File(output);
        long start = System.currentTimeMillis();
        segFile(in,out);
        long cost = System.currentTimeMillis()-start;
        System.out.println("cost time:"+cost+" ms");
    }
    public static void segFile(File input, File output) throws Exception{
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output))); 
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input)))) {
            String line = reader.readLine();
            while(line != null){
                while(line.length()>0){
                    int len=MAX_LENGTH;
                    if(line.length()<len){
                        len=line.length();
                    }
                    //取指定的最大长度的文本去词典里面匹配
                    String tryWord = line.substring(0, 0+len);
                    while(!DIC.contains(tryWord)){
                        //如果长度为一且在词典中未找到匹配，则按长度为一切分
                        if(tryWord.length()==1){
                            break;
                        }
                        //如果匹配不到，则长度减一继续匹配
                        tryWord=tryWord.substring(0, tryWord.length()-1);
                    }
                    writer.write(tryWord+" ");
                    //从待分词文本中去除已经分词的文本
                    line=line.substring(tryWord.length());
                }
                writer.write("\n");
                line = reader.readLine();
            }
        }
    }
}