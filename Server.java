import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;


public class Server {

    static final int portNumber = 8003;
    static int range = -1;
    static String hashPassword;

    public static void main(String args[]) throws IOException {
 
        System.out.println("\n**** Distributed Password Cracker ****");
        System.out.println("**************** Server **************");
        hashPassword = generateARandomPasswordWithHash();
        System.out.println("Hash password: " + hashPassword);

        ServerSocket ss = new ServerSocket(portNumber);
        System.out.println("\nWaiting for client response....");
        int id = 0;
        
        while (true) {
            Socket clientSocket = ss.accept();
            ClientServiceThread clientService = new ClientServiceThread(clientSocket, ++id);
            clientService.start();
            
            System.out.println("\nClient_" + id + " connection established\n");
        }
    }

    private static String to52(long data) {
        String str = "ABCDEFGHIJKLNMOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String s = "";
        if(data==0)
        {
            return str.charAt(0)+"";
        }
        while(data > 0){
            if(data < 52){
                s = str.charAt((int)data) + s;
                data = 0;
            }else{
                long r = data%52L;
                s = str.charAt((int)r) + s;
                data  = (data-r)/52;
            }
        }
        return s;
   }


    /**
     * generate random password with md5 hash combining it with date 
     */
    public static String generateARandomPasswordWithHash() {

        StringBuilder tmp = new StringBuilder();
        char[] symbols, buffer = new char[5];
        String date = getSystemDate();
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for (char ch = 'A'; ch <= 'Z'; ++ch) {
            tmp.append(ch);
        }
        for (char ch = 'a'; ch <= 'z'; ++ch) {
            tmp.append(ch);
        }
        symbols = tmp.toString().toCharArray();

        for (int index = 0; index < buffer.length; ++index) {
            buffer[index] = symbols[random.nextInt(symbols.length)];
        }
        
        try {
            String actualPassword = new String(buffer);
            System.out.println("\nRandomly generated password: " + actualPassword);
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(( actualPassword + date).getBytes());
            byte[] byteData = md.digest();

            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException ex) {

        }

        return new String(sb);
    }

   
    
    
    /**
     * this method is used for getting system date for hashing. 
    */
    public static String getSystemDate() {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = (dateFormat.format(cal.getTime())).toString();
        return date;
    }
 
    
    
    
    
    
    public static class ClientServiceThread extends Thread{
        
        int noOfDataSent = 1, clientId = -1;
        String sendMsg = "", receiveMsg = "";
        Socket clientSocket;
        
        ClientServiceThread(Socket socket, int id){
            clientSocket = socket;
            clientId = id;
        }
        
     /**
     * print process range of client.
     */
        void printIntoSystem(int noOfDataSent, int clientId) {
            
            long lower_limit = 1000000 * range; //1,000,000
            long upper_limit = lower_limit + (1000000 - 1);

            String sLower_limit = to52(lower_limit);
            String sUpper_limit = to52(upper_limit);

            if ((sLower_limit.length()) < 5) {

                StringBuilder sb = new StringBuilder(sLower_limit);
                for (int j = 0; j < 5 - (sLower_limit.length()); j++) {
                    sb.insert(j, "A");
                }

                sLower_limit = sb.toString();
            }

            if ((sUpper_limit.length()) < 5) {

                StringBuilder sb = new StringBuilder(sUpper_limit);
                for (int j = 0; j < 5 - (sUpper_limit.length()); j++) {
                    sb.insert(j, "A");
                }

                sUpper_limit = sb.toString();
            }      
            
            System.out.println("\nData packet_" + noOfDataSent +" sent to client_" + clientId);
            System.out.println("Given Range: " + 
                    sLower_limit + " to " + sUpper_limit);
        }
        
        public void run() {
            try {
                BufferedReader brIN = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintStream psOUT = new PrintStream(clientSocket.getOutputStream());

                while (noOfDataSent <= 4) {
                    sendMsg = (++range) + "\n" + hashPassword;
                    psOUT.println(sendMsg);
                    printIntoSystem(noOfDataSent, clientId);
                
                    receiveMsg = brIN.readLine();
                    if (receiveMsg.equals("success")) {
                        System.out.println("Client_" + clientId + " successfully crack the password");
			while (true) {}
                    }
                    noOfDataSent++;
                }

                clientSocket.close();
                brIN.close();
                psOUT.close();
                System.out.println("Connection lost from client_" + clientId);
            } catch (IOException ex) {

            }

        }
    }
}
