package zy.pointer.crps.sync.local.filesplit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class DoFileSplit {

    public static final Logger LOG = LoggerFactory.getLogger(DoFileSplit.class);

    public static void main(String[] args) throws Exception {
        String source = "G:\\wangyx_tmp_chain.txt";
        String targetFileDir = "G:\\target";
        File sourceFile = new File(source);
        if ( !sourceFile.exists() ){
            throw new RuntimeException("源文件不存在");
        }
        File targetDir = new File( targetFileDir );
        if ( !targetDir.exists() ){
            targetDir.mkdirs();
        }
        String targetFileBaseName = "wangyx_tmp_chain_350";
        final int fileLineSet = 1000000;

        // 当前已分割多少个文件 , 根据此变量来新建新的文件名
        int fileSplitCount = 0;
        // 当前新文件已写入的文件行数 , 当此值统计数大于设定值时,新建文件存储后面的数据.
        int fileLineCount = 0;
        // 源文件总行数计数器
        int lineCount = 0;

        FileInputStream inputStream = new FileInputStream(source);
        Scanner sc = new Scanner(inputStream, "UTF-8");

        File currentOutput = null;
        FileWriter fileWriter = null;
        boolean hasNextLine = true;
        do {
            if ( currentOutput == null ){
                fileSplitCount ++ ;
                currentOutput = new File( targetFileDir + "\\" + targetFileBaseName + "_" + fileSplitCount + ".txt" );
                currentOutput.createNewFile();
                fileWriter = new FileWriter( currentOutput );
                LOG.info("[{}] 创建新文件 -> {}" , lineCount , currentOutput.getName());
            }
            while (sc.hasNextLine() && fileLineCount < fileLineSet ) {
                String line = sc.nextLine();
                fileWriter.write(line + "\n" );
                fileLineCount ++;
                lineCount ++ ;
                LOG.info("[{}] / {} @ {} 数据写入" , lineCount , fileLineCount , currentOutput.getName());
            }
            fileWriter.close();
            currentOutput = null;
            fileLineCount = 0;
            hasNextLine = sc.hasNextLine();
        }while( hasNextLine );

    }

}
