package test.com.resist.websocket; 

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** 
* ConnectionServer Tester. 
* 
* @author <Authors name> 
* @since <pre>dec 4, 2014</pre> 
* @version 1.0 
*/ 
public class ConnectionServerTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: setControlFrameHandler(MessageHandler controlFrameHandler) 
* 
*/ 
@Test
public void testSetControlFrameHandler() throws Exception { 
//TODO: Test goes here...


} 

/** 
* 
* Method: setMessageHandler(MessageHandler messageHandler) 
* 
*/ 
@Test
public void testSetMessageHandler() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: setTimeout(int timeout) 
* 
*/ 
@Test
public void testSetTimeout() throws Exception { 
//TODO: Test goes here...

    int result = 1000*60*60;
    int expected = 3600000;

    assertEquals(result,expected);


} 

/** 
* 
* Method: getAddress() 
* 
*/ 
@Test
public void testGetAddress() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getPort() 
* 
*/ 
@Test
public void testGetPort() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getPath() 
* 
*/ 
@Test
public void testGetPath() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: handleControlFrame(final Message message) 
* 
*/ 
@Test
public void testHandleControlFrame() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: handleMessage(final Message message) 
* 
*/ 
@Test
public void testHandleMessage() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: stop() 
* 
*/ 
@Test
public void testStop() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: manageConnections() 
* 
*/ 
@Test
public void testManageConnections() throws Exception { 
//TODO: Test goes here... 
} 


/** 
* 
* Method: createSocket() 
* 
*/ 
@Test
public void testCreateSocket() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = ConnectionServer.getClass().getMethod("createSocket"); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: createConnections(ServerSocket socket) 
* 
*/ 
@Test
public void testCreateConnections() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = ConnectionServer.getClass().getMethod("createConnections", ServerSocket.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

} 
