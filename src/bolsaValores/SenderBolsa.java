package bolsaValores;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class SenderBolsa {

    private static final String EXCHANGE_NAME = "BOLSADEVALORES";
 
    public static void main(String[] args) throws Exception {
    	ConnectionFactory factory = new ConnectionFactory();
    	Scanner ler = new Scanner(System.in);
    	
    	// define o host
    	System.out.println("-----------------------BolsaDeValores-----------------------");
    	System.out.println("\n-------------Sender-------------\nDigite o host:");
    	String host = ler.nextLine();
    	factory.setHost(host);
        
    	//define os topicos
    	System.out.println("---utilize ';' para separar topicos ou mensagens---");
    	System.out.println("Topicos:");
		String topics = ler.nextLine();
		System.out.println("Mensagens:");
		String msgs = ler.nextLine();
		
		//envia mensagens
    	try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
    			channel.exchangeDeclare(EXCHANGE_NAME, "topic");
    			String messages[] = topics.split(";");
    	    	String routes[] = msgs.split(";");
    	    	System.out.println(messages[0]);
    	    	for (String route : routes) {
    	        	for (String msg : messages) {
    	        		channel.basicPublish(EXCHANGE_NAME, route, null, msg.getBytes("UTF-8"));
    	                System.out.println(" [x] Enviado '" + route + "':'" + msg + "'");
    	        	}
    	        }
        }
    	
         
    }
}