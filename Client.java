import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    static final int portNumber = 8003; 
    static final String serverIP = "172.17.3.20"; // ServerIP

    static int clientTrialNumber = 0;
    static String date, actualHashPassword;
    static BufferedReader brIN; 
    static PrintStream psOUT;
    static Socket clientSocket; 
    static MessageDigest md;
    static String mesgSentToServer;
    static int range;
    
    public static void main(String args[]){
        System.out.println("\n**** Distributed Password Breaker ****");
        System.out.println("**************** Client **************");

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        date = getSystemDate();
        
		while (true){
			getConnectedToServer();
			clientTrialNumber = 1;
			while(clientTrialNumber <= 4){
				
				range = getRangeFromServer();
				printIntoSystem(clientTrialNumber);
				mesgSentToServer = doCrackingPassword();
				 
				 if(mesgSentToServer.equals("fail")){
						System.out.print("\nCan't find password.");
						if(clientTrialNumber != 4) System.out.print(" request for another packet...");
						
						psOUT.println(mesgSentToServer);
						clientTrialNumber++;
				 }else{
					 System.out.println("Got password. It is: " + mesgSentToServer);
					 psOUT.println("success");
					 while(true){}
				 } 
			}
			 getDisconnectedFromServer();
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
    * this method crack the password
    * @return "success" if password matches or "fail" if password doesn't match
    */
    public static String doCrackingPassword(){
       
        String value, temp, generatedHash;
        int value_length;
        long lower_limit, upper_limit;
        
        lower_limit = 1000000 * range; //1,000,000
        upper_limit = lower_limit + (1000000-1);
        
        for (long i = lower_limit; i <= upper_limit; i++) {
            value = to52(i);
            StringBuilder sb = new StringBuilder(value);
            
            //if generating password is less than 5 character
            if ((value.length()) < 5) {
                for (int j = 0; j < 5 - (value.length()); j++) {
                    sb.insert(j, "A");
                }
                
                temp = sb.toString();
                generatedHash = generateHash(temp);

            } else {
                temp = value;
                generatedHash = generateHash(temp);
            }
            
            if(compareHash(actualHashPassword, generatedHash)){
                return temp; 
            }
        }
        
        return "fail";
    }
    
  
    
    
    /**
     * this method is for getting system date. 
    */
    public static String getSystemDate(){
        
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = (dateFormat.format(cal.getTime())).toString();
        return date;
    }

    
    
    
    /**
     * this method is for generating password hash. 
    */
    public static String generateHash(String crackPassword) {

        byte[] byteData;
        StringBuffer sb = new StringBuffer();
        md.update((crackPassword + date).getBytes());
        byteData = md.digest();

        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return new String(sb);
    }
    
    
 
    
    
    /**
     * this method is for comparing given hash from server with generating password hash. 
    */
    public static boolean compareHash(String givenHash, String generatedHash){
        if(givenHash.equals(generatedHash)) return true;
        else return false;
    }
    
    
    
    
    /**
     * this method is for getting connected form server. 
    */
    public static void getConnectedToServer() {
        try {
            clientSocket = new Socket(serverIP, portNumber);
            System.out.println("Server connection established\n");

            brIN = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            psOUT = new PrintStream(clientSocket.getOutputStream());   //for writing
        } catch (IOException ex) {

        }
    }
    
    
    
    
    
    /**
     * this method is for getting disconnected form server. 
    */
    public static void getDisconnectedFromServer(){
        try {
            clientSocket.close();
            brIN.close();
            psOUT.close();
            System.out.println("\nConnection lost from server");
        } catch (IOException ex) {
          
        }
    }
   
    
    
    
    
    
    
    /**
     * this method is  for getting range from server. 
     */
    public static int getRangeFromServer(){
        String sendMsg = "", receiveRange = "";
        try {
            receiveRange = brIN.readLine();
            actualHashPassword = brIN.readLine();
       
        } catch (IOException ex) {
        }
        
        return Integer.parseInt(receiveRange);
    }

    
    
    
    
    /**
     * this method print client range and data packet in command prompt.
     * @param dataReceiveNo 
     */
    public static void printIntoSystem(int dataReceiveNo) {
            
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
            
            System.out.println("\n" + "Get data packet_" + dataReceiveNo +" from server");
            System.out.println("Start cracking password with given Range: " + 
                    sLower_limit + " to " + sUpper_limit);
        }
}