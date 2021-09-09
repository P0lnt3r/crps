package zy.pointer.crps.sync.local.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import zy.pointer.crps.commons.business.cryptotx.DictConstant;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthLocalFileTask;
import zy.pointer.crps.commons.business.cryptotx.service.IEthLocalFileTaskService;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class ReadFileSync implements InitializingBean {

    @Autowired
    IEthLocalFileTaskService ethLocalFileTaskService;

    @Value("${sync.source}")
    String source;

    @Value("${sync.local.dir}")
    String localDir;

    final Integer POOL_SIZE = 11;

    ExecutorService pool;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = Executors.newFixedThreadPool( POOL_SIZE );
        log.info("执行 {} 文件写入操作" , source);
        List<EthLocalFileTask> list = ethLocalFileTaskService.getUnExecuteFileTask(source);
        if ( list.isEmpty() ){
            log.info("暂无需要同步的文件.");
            return;
        }
        list.forEach( ethLocalFileTask -> {
            log.info("预备执行文件同步 => {} @ {}" , ethLocalFileTask.getFileName() , ethLocalFileTask.getPosition());
            pool.execute( ()-> {
                try {
                    String fileName = ethLocalFileTask.getFileName();
                    int position = ethLocalFileTask.getPosition();
                    File readFile = new File(  localDir +  File.separator  + fileName );
                    FileInputStream inputStream = new FileInputStream(readFile);
                    Scanner sc = new Scanner(inputStream, "UTF-8");
                    String line = null;
                    int cursor = 0;
                    List<String> lines = new ArrayList<>();
                    while( sc.hasNext() ){
                        line = sc.nextLine();
                        cursor ++;
                        if ( cursor > position ){
                            lines.add( line );
                            if ( lines.size() == 200 ){
                                for( int i = 0;i<lines.size();i++ ){
                                    ethLocalFileTaskService.handleSyncLocalFile( ethLocalFileTask , cursor - lines.size() + i + 1 , lines.get(i) );
                                }
                                lines.clear();
                            }
                        }
                    }
                    if ( ! lines.isEmpty() ){
                        for( int i = 0;i<lines.size();i++ ){
                            ethLocalFileTaskService.handleSyncLocalFile( ethLocalFileTask , cursor - lines.size() + i + 1 , lines.get(i)  );
                        }
                    }
                    ethLocalFileTask.setState( DictConstant.SYNC_LOCAL_FILE_TASK_STATE_FINISH.value );
                    ethLocalFileTaskService.updateById( ethLocalFileTask );
                    sc.close();
                    inputStream.close();
                } catch (Exception e){
                    log.error( e.getCause().toString() );
                    e.printStackTrace();
                }
            } );
        } );

    }

}
