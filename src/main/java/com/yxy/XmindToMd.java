package com.yxy;

import com.yxy.to.md.ToMdUtils;
import org.apache.commons.cli.*;
import org.codehaus.plexus.util.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class XmindToMd {

    public static void main(String[] args) {
        String[] arg = {"-s", "/Users/chenyangm/Downloads/日语学习小册总结.pos", "-o"};
        new XmindToMd().parseArgs(arg);
    }

    public void parseArgs(String[] args)  {


        Runtime.getRuntime().addShutdownHook(new Thread(()->{
//            System.out.println("谢谢使用哦");
        }));

        try {
            CommandLineParser parser = new GnuParser( );

            Options options = new Options();
            options.addOption("s","source",true,"来源文件");
            options.addOption("o", "output", false, "输出文件");
            options.addOption("h", "help", false, "帮助信息");

            CommandLine commandLine = null;
            commandLine = parser.parse( options, args );

            if( commandLine.hasOption("h")) {
                System.out.println("-s: " + "来源文件，目前支持 pos、xmind格式");
                System.out.println("-o: " + "输出文件,如果不加这个参数则输出在控制台上，如果加了参数但是没有加路径，则输出在来源文件的路径中");
                System.out.println("-h: " + "查看帮助信息");
                System.exit(0);
            }

            if (!commandLine.hasOption("s") || (commandLine.hasOption("s") && StringUtils.isBlank(commandLine.getOptionValue("s")))) {
                System.out.println("清输入正确的文件路径");
                System.exit(0);
            }

            String inputFile = commandLine.getOptionValue("s");
            Consumer<StringBuilder> consumer = null;
            FileOutputStream outputStream = null;
            if (!commandLine.hasOption("o")) {
                consumer = System.out::println;
            } else {
                String filename = commandLine.getOptionValue("o");
                if (StringUtils.isBlank(filename)) {
                    String[] split = inputFile.split("\\.");
                    split[split.length - 1] = "md";
                    filename = String.join(".", split);
                }
                outputStream = new FileOutputStream(filename, false);
                FileOutputStream finalOutputStream = outputStream;
                consumer = (i) -> {
                    try {
                        finalOutputStream.write(i.toString().getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                };
            }

            ToMdUtils.toMD(inputFile, consumer);
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }

        } catch (ParseException | IOException e) {
            System.out.println("请输入正确的参数");
            System.exit(0);
        }catch (Exception e){
            System.out.println(e);
        }

    }

}
