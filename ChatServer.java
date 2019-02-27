import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ChatServer {
    // Save all the usernames
    private static Set<String> users = new HashSet<>();

    private static Set<PrintWriter> writers = new HashSet<>();

    public static void main(String[] args) throws Exception {
        
        System.out.println("The chat server is running...");
        // limit the quantity of users
        ExecutorService pool = Executors.newFixedThreadPool(50);
        try (ServerSocket listener = new ServerSocket(5678)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        }
    }
    
    private static class Handler implements Runnable {
        private String name;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;

       
        public Handler(Socket socket) {
            this.socket = socket;
        }
        public void run() {
            try {
                // receive a message
                in = new Scanner(socket.getInputStream());
                // print a message
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    out.println("WHO");
                    name = in.nextLine();
                  
                    synchronized (users) {
                        
                        if ( name != null  && name.startsWith("IAM")) 
                        {
                              name = name.substring(4);

                            if(users.contains(name))
                               out.println("This username is already taken ... ");
                            else
                            {
                                if(name.contains(" ")) 
                                {
                                    out.println("Please write a username without space");
                                    continue;
                                }                                
                                users.add(name);
                                break;
                            }
                        }
                        else if(name != null && name.equals("list"))
                        {
                            if(users != null && !users.isEmpty())
                            {
                                for (String username : users) 
                                    out.println(username);
                            }   
                            else
                                out.println("There is no user yet");
                        }
                    }
                }
    
                out.println("Welcome " + name);
                for (PrintWriter writer : writers) {
                    writer.println("ARV " + name );
                }
               System.out.println(name + " joined the chat");
                writers.add(out);

                // Accept messages from this client and broadcast them.
                while (true) {
                    String input = in.nextLine();
                     if (input.toLowerCase().equals("bye")) {
                            return;
                        }
                     else if(input.startsWith("MSG"))
                     {
                        for (PrintWriter writer : writers) 
                        {
                            writer.println(name + ": " + input.substring(4));
                        }
                     }
                     else if(input.startsWith("PRV"))
                     {
                        String private_msg = input.substring(4);
                        if(users.contains(private_msg))
                        {
                            out.println(private_msg);
                        }
                        else 
                            out.println("check the username");
                        
                     }
                     else if(input.equals("list"))
                     {
                        if(users != null && !users.isEmpty())
                        {
                            for (String username : users) 
                                out.println(username);
                        }
                        else
                            out.println("There is no user yet");
                     }
                    else    
                    {
                          out.println("Unknown MESSAGE " );       
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (out != null) {
                    writers.remove(out);
                }
                if (name != null) {
                    users.remove(name);
                    for (PrintWriter writer : writers) {
                        writer.println( name + " has left ");
                    }
                   System.out.println(name + " has left the chat");
                }
                try 
                { socket.close(); } 
                catch (IOException e) 
                {
                   System.out.println(e);
                }
            }
        }
    }
}
   