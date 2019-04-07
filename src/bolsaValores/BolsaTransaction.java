package bolsaValores;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class BolsaTransaction {
	private static final String EXCHANGE_NAME = "BOLSADEVALORES";
	private static final String QUEUE_NAME = "BOLSA";
	   
    public static void main(String[] args) throws Exception {
        Scanner ler = new Scanner(System.in);
     // define o host
        System.out.println("-----------------------BolsaDeValores-----------------------");
    	System.out.println("Digite o host:");
    	String host = ler.nextLine();
    	ConnectionFactory factory = new ConnectionFactory();
    	factory.setHost(host);
    	Connection connection = factory.newConnection();
        Channel channel = connection.createChannel(); 
    	
        //define os topicos
    	System.out.println("---utilize ';' para separar topicos---");
    	System.out.println("Topicos:");
		String topics = ler.nextLine();
       
		String queueName = "BROKER";
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        channel.queueDeclare(queueName, false, false, false, null);
        
        String routes[] = topics.split(";");
        if (routes.length < 1) {
            System.err.println("Sem tópicos para binding...");
            System.exit(1);
        }  

        for (String route : routes) {
            channel.queueBind(queueName, EXCHANGE_NAME, route);
        }
        
        //recebe as mensagens e reenvia
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [*] Esperando por Mensagens. Para sair aperte CTRL+C");
            send(channel,delivery.getEnvelope().getRoutingKey(),message);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
    
    private static void send(Channel channel, String route, String message) throws UnsupportedEncodingException, IOException{
    	channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, route);
    	channel.basicPublish(EXCHANGE_NAME, route, null, message.getBytes("UTF-8"));
    	System.out.println(" [x]Recebido e Enviado '" + route + "':'" + message + "'");
    }
}
