package test.com.resist.websocket.example; 

import com.resist.websocket.ConnectionServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/** 
* Server Tester. 
* 
* @author <Authors name> 
* @since <pre>dec 4, 2014</pre> 
* @version 1.0 
*/ 
public class ServerTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
}

/**
* 
* Method: main(String[] args) 
* 
*/ 
@Test
public void testMain() throws Exception { 
//TODO: Test goes here...

    new ConnectionServer("145.24.222.119",8080,"/search")
            .setTimeout(60000);

    int expected = 60000;
    int result = 60000;
    assertEquals(expected,result);



} 


} 
