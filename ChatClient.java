import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;

public class ChatClient implements Runnable 
{

  private static Socket clientSocket ;    
  private static boolean closed = false;  
  private static BufferedReader inputLine = null;  
  private static DataInputStream is = null;
  private static PrintStream os = null;  
  private String host;  
  private int port;  
  
  public ChatClient(String host, int port)
  {
      this.host = host;
      this.port = port;
  }
  
  public static void main(String[] args) {
     String host = "localhost";
     int portNumber = 2222;

    if (args.length < 2) 
    {
      System.out.println("Default host = " + host + " \n Default port port = " 
      + portNumber);
      ChatClient client = new ChatClient(host, portNumber);
    } 
    else 
    {
       host = args[0];
       portNumber = Integer.valueOf(args[1]).intValue();
       ChatClient client = new ChatClient(host, portNumber);
    }
    
    try 
    {
        clientSocket = new Socket(host, portNumber);        
        inputLine = new BufferedReader(new InputStreamReader(System.in));
        os = new PrintStream(clientSocket.getOutputStream());
        is = new DataInputStream(clientSocket.getInputStream());       
    }
    catch (IOException e) 
    {
        System.out.println(e);
    }

    
    if (clientSocket != null && is != null && os != null) {
      try {
        new Thread(new ChatClient(host, portNumber)).start();
        while (!closed) 
        {
           os.println(inputLine.readLine().trim());
        }
        is.close();
        os.close();
        clientSocket.close();
      } 
      catch (IOException e) 
      {
        System.err.println( e );
      }
    }
  }
 
  public void run() {
    char c;
    int i;
    try 
    {
          while((i = is.read()) != -1) 
          {
            c = (char)i;
            System.out.print(c);          
          }
      closed = true;      
    } 
    catch (Exception e) 
    {
      System.err.println( e );
    }
  }
}