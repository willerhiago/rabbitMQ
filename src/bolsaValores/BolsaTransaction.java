package bolsaValores;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class BolsaTransaction {
	private static final String EXCHANGE_NAME = "BOLSADEVALORES";
	private static final String QUEUE_NAME = "BOLSA";
	private static final String[] TOPICS= {"ABEV3","PETR4","VALE5","ITUB4","BBDC4","BBAS3","CIEL3","PETR3","HYPE3","VALE3",
											"BBSE3","CTIP3","GGBR4","FIBR3","RADL3"};
	private static ArrayList<String> vendas = new ArrayList<String>();
	
    public static void main(String[] args) throws Exception {
    	Scanner ler = new Scanner(System.in);
    	//define o host
        System.out.println("-----------------------BolsaDeValores-----------------------");
    	System.out.println("Digite o host:");
    	String host = ler.nextLine();
    	ler.close();
    	
    	//configuração conexão
    	ConnectionFactory factory = new ConnectionFactory();
    	factory.setHost(host);
    	Connection connection = factory.newConnection();
        Channel channel = connection.createChannel(); 
       
		String queueName = "BROKER";
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        channel.queueDeclare(queueName, false, false, false, null);
 
        
        for (String route : TOPICS) {
            channel.queueBind(queueName, EXCHANGE_NAME, route);
        }
        
        System.out.println(" [*] Esperando por Mensagens. Para sair aperte CTRL+C");
        //recebe as mensagens e reenvia
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            send(channel,delivery.getEnvelope().getRoutingKey(),message);
            
            //Verifica ordens de compra e venda, caso exista envia para os brokers
            if(verifyTransaction(message)){
            	sendTransaction(channel,delivery.getEnvelope().getRoutingKey(),message);
            };
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
    
    //operações de envio de mensagem
    private static void send(Channel channel, String route, String message) throws UnsupportedEncodingException, IOException{
    	channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    	channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, route);
    	String formatedMessage = formatMessage(message);
    	channel.basicPublish(EXCHANGE_NAME, route, null, formatedMessage.getBytes("UTF-8"));
    	System.out.println(" [x]Recebido e Enviado '" + route + "':'" + formatedMessage + "'");
    }
    
    private static void sendTransaction(Channel channel, String route, String message) throws UnsupportedEncodingException, IOException{
    	String formatedMessage = formatMessage(message);
    	String newMessage = " [x]Transação Efetuada: '"+returnTopic(message)+"' : '" + formatedMessage + "'";
    	channel.basicPublish(EXCHANGE_NAME, route, null, newMessage.getBytes("UTF-8"));
    	System.out.println(newMessage);
    }
    
    //operação de verificação de transação
    private static boolean verifyTransaction(String message) throws UnsupportedEncodingException, IOException{
    	String info[] = message.split("="); 
    	String type = info[0]; 
    	boolean result = false;
    	if(type.contains("venda")) vendas.add(type + "="+info[1]);
    	else if(type.contains("compra")){
    		for (int i = 0; i < vendas.size(); i++) {
    			if(returnTopic(message).toLowerCase().equals(returnTopic(vendas.get(i)).toLowerCase()) && 
    				returnValor(info[1]) >= returnValor(vendas.get(i))){
    				vendas.remove(i);
    				result =  true;
    			}
    		      
    		    }
    	}
    	
    	return result;
    }
    
    //funções auxiliares
    private static String returnTopic(String msg){
    	String[] info = msg.split("=");
    	String info2 = info[0];
    	String[] topic = info2 .split("-");
    	String result = topic[1];
    	return result;
    };
    
    private static double returnValor(String msg){
    	String[] info = msg.split(";");
    	double result = 0;
    	result = Double.parseDouble(info[1]);
    	return result;
    };
    
    
    private static String formatMessage(String msg){
    	String result = "";
    	String type[] = msg.split("=");
    	String info[] = type[1].split(";");
    	if(type[0].contains("venda")){
    		result = "Venda = Quant: "+ info[0]+" Valor: "+info[1]+" Corretora:"+info[2];
    	}else if(type[0].contains("compra")){
    		result = "Compra = Quant: "+ info[0]+" Valor: "+info[1]+" Corretora:"+info[2];
    	}else {
    		result = "Info = Data: "+ info[0];
    	}
    	return result;
    }
}
