package broker;

import java.util.Scanner;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class ReceiverBroker {
    private static final String EXCHANGE_NAME = "BROKER";
    private static final String QUEUE_NAME = "BOLSA";

    public static void main(String[] args) throws Exception {
    	 Scanner ler = new Scanner(System.in);
         	//define o host
            System.out.println("-----------------------Broker-----------------------");
        	System.out.println("\n-------------Receiver-------------\nDigite o host:");
        	String host = ler.nextLine();
        	ler.close();
        	
        	//configuração conexão
        	ConnectionFactory factory = new ConnectionFactory();
        	factory.setHost(host);
        	Connection connection = factory.newConnection();
            Channel channel = connection.createChannel(); 

    		//recebe mensagem FILA
    		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] Esperando por Mensagens. Para sair aperte CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Recebido '" + message + "'");
            };
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
        }
    
}