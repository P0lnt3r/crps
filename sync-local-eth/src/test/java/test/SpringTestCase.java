package test;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import zy.pointer.crps.sync.local.startup.StartUp;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={StartUp.class})// 指定启动类
public class SpringTestCase {

    @Test
    public void test(){
    }

}
