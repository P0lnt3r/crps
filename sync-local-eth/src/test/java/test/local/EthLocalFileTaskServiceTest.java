package test.local;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.SpringTestCase;
import zy.pointer.crps.commons.business.cryptotx.DictConstant;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthLocalFileTask;
import zy.pointer.crps.commons.business.cryptotx.service.IEthLocalFileTaskService;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class EthLocalFileTaskServiceTest extends SpringTestCase {

    @Autowired
    IEthLocalFileTaskService ethLocalFileTaskService;

    @Test
    public void test(){
        String source = "wangyx_tmp_chain.txt";
        String fileName = "wangyx_tmp_chain_350_2.txt";
        EthLocalFileTask ethLocalFileTask = new EthLocalFileTask();
        ethLocalFileTask.setSource( source );
        ethLocalFileTask.setFileName( fileName );
        ethLocalFileTask.setPosition( 0 );
        ethLocalFileTask.setState(DictConstant.SYNC_LOCAL_FILE_TASK_STATE_UNEXECUTE.value);
        ethLocalFileTaskService.save( ethLocalFileTask );
    }



    @Test
    public void test2() throws Exception {
        int pool_size = 2;
        ExecutorService pool = Executors.newFixedThreadPool( pool_size );
        List< EthLocalFileTask > list = ethLocalFileTaskService.getUnExecuteFileTask("wangyx_tmp_chain.txt");
        if ( list.isEmpty() ){
            System.out.println("暂无需要同步的文件.");
            return;
        }
        list.forEach( ethLocalFileTask -> {
            System.out.println("执行文件同步..");
            pool.execute( ()-> {
                try {
                    String fileName = ethLocalFileTask.getFileName();
                    int position = ethLocalFileTask.getPosition();
                    File readFile = new File(  "G:\\target_test\\" + fileName );
                    FileInputStream inputStream = new FileInputStream(readFile);
                    Scanner sc = new Scanner(inputStream, "UTF-8");
                    String line = null;
                    int cursor = 0;
                    while( sc.hasNext() ){
                        line = sc.nextLine();
                        cursor ++;
                        if ( cursor > position ){
                            ethLocalFileTaskService.handleSyncLocalFile( ethLocalFileTask , cursor , line );
                        }
                    }
                    ethLocalFileTask.setState( DictConstant.SYNC_LOCAL_FILE_TASK_STATE_FINISH.value );
                    ethLocalFileTaskService.updateById( ethLocalFileTask );
                    sc.close();
                    inputStream.close();

                } catch (Exception e){
                    System.out.println( e.getMessage() );
                    e.printStackTrace();
                }
            } );
        } );
        Thread.sleep(5000000);
    }

    @Test
    public void test3() throws Exception{
        List< EthLocalFileTask > list = ethLocalFileTaskService.getUnExecuteFileTask("wangyx_tmp_chain.txt");
        LinkedList< EthLocalFileTask > taskQueue = new LinkedList<EthLocalFileTask>();
        taskQueue.addAll( list );
        for( int i =1;i<=2;i++ ){
            new Thread( ()->{
                EthLocalFileTask ethLocalFileTask = null;
                synchronized ( taskQueue ){
                    if ( !taskQueue.isEmpty() ){
                        ethLocalFileTask = taskQueue.removeFirst();
                    }
                }
                try {
                    String fileName = ethLocalFileTask.getFileName();
                    int position = ethLocalFileTask.getPosition();
                    File readFile = new File(  "G:\\target\\" + fileName );
                    FileInputStream inputStream = new FileInputStream(readFile);
                    Scanner sc = new Scanner(inputStream, "UTF-8");
                    String line = null;
                    int cursor = 0;
                    while( sc.hasNext() ){
                        line = sc.nextLine();
                        cursor ++;
                        if ( cursor > position ){
                            ethLocalFileTaskService.handleSyncLocalFile( ethLocalFileTask , cursor , line );
                        }else{

                        }
                    }
                    sc.close();
                    inputStream.close();
                } catch (Exception e){
                    System.out.println( e.getMessage() );
                    e.printStackTrace();
                }
            } ).start();
        }

        Thread.sleep(5000000);
    }


}
