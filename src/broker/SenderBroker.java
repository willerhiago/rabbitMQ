package broker;

import java.util.Scanner;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class SenderBroker {
	private final static String EXCHANGE_NAME = "BOLSADEVALORES";
	private final static String QUEUE_NAME = "BROKER";

    public static void main(String[] args) throws Exception {
    	ConnectionFactory factory = new ConnectionFactory();
    	Scanner ler = new Scanner(System.in);
    	
    	// define o host
    	System.out.println("-----------------------Broker-----------------------");
    	System.out.println("\n-------------Sender-------------\nDigite o host:");
    	String host = ler.nextLine();
    	factory.setHost(host);
        
    	System.out.println("Operação:");
    	System.out.println("1 - Compra\n2 - Venda \n3 - Info");
    	int type = ler.nextInt();
    	ler.nextLine();
    	String message = "";
    	String formatedMessage = "";
		switch(type){
			case 1:
				System.out.println("--Compra--");
				System.out.println("Formato: quantidade ; valor ; corretora");
				message = ler.nextLine();
				formatedMessage = formatMessage(message,type);
				break;
			case 2:
				System.out.println("--Venda--");
				System.out.println("Formato: quantidade ; valor ; corretora");
				message = ler.nextLine();
				formatedMessage = formatMessage(message,type);
				break;
			case 3:
				System.out.println("--Info--");
				System.out.println("Formato: dd/mm/yyyy");
				message = ler.nextLine();
				formatedMessage = formatMessage(message,type);
				break;
			default : System.out.println("Operação inválida."); return;
		}
		
		String route = returnRoute(type);
		System.out.println(route);
		message = route+"="+ message;
		formatedMessage = route+"= "+formatedMessage;
		//envia mensagem FILA
		try (Connection connection = factory.newConnection();
	             Channel channel = connection.createChannel()) {
	            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
	            channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
	            System.out.println(" [x] Enviado '" + formatedMessage + "'");
	        }
    	ler.close();
    }
    
    /*-----------------------------------------------------------------------------------------------------------------------*/
    //funções auxiliares
    private static String formatMessage(String msg, int type){
    	String result = "";
    	String msgs[] = msg.split(";");
    	if(type == 1 || type == 2){
    		result = "Quant: "+ msgs[0]+" Valor: "+msgs[1]+" Corretora:"+msgs[2];
    	}else if(type == 3){
    		result = "Data: " + msgs[0];
    	}
    	return result;
    }
    
    private static String returnRoute(int type){
    	Scanner ler = new Scanner(System.in);
    	System.out.println("Topico:");
    	String result ="";
    	if(type == 1)result += "compra-";
    	if(type == 2)result += "venda-";
    	if(type == 3)result += "info-";
    	result += ler.nextLine();
    	return result;
    }
}
