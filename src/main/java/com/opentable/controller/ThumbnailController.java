package com.opentable.controller;


import java.util.List;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.sqs.AmazonSQS;
import com.opentable.Listeners.MySQSListener;
import com.opentable.clients.ClientFactory;
import com.opentable.serviceImpl.OpenTableServiceImpl;
import com.opentable.serviceImpl.ThumsbnailViewerServiceImpl;

@RestController
@SpringBootApplication
public class ThumbnailController {
	
	static AmazonSQS sqs;
	final static String queueName = "firstsqs";

	public static void main(String[] args) throws JMSException, InterruptedException {
		
		ClientFactory clientFactory = new ClientFactory();
		
		sqs = clientFactory.getSQSClient();
		
		SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
                new ProviderConfiguration(),
                sqs
                );
		 SQSConnection connection = connectionFactory.createConnection();
		 Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
	        MessageConsumer consumer = session.createConsumer( session.createQueue(queueName));
	        MySQSListener callback = new MySQSListener();
	        consumer.setMessageListener( callback );

	        
	        connection.start();
	         
	       
		// TODO Auto-generated method stub
		SpringApplication.run(ThumbnailController.class, args);
	}

	
	@PostMapping("/uploadImage")
    public void uploadImage(@RequestParam("image") MultipartFile file) throws Exception {
		OpenTableServiceImpl openTableServiceImpl = new OpenTableServiceImpl();
		try {
			openTableServiceImpl.uploadAnImage(file);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	@GetMapping("/listThumbnails")
	@RequestMapping(value = "/listThumbnails", method = RequestMethod.GET)
	public String listthumbnails() {
		
		return "imageloader.html";
		
		/*
		 * ThumsbnailViewerServiceImpl thumsbnailViewerServiceImpl = new
		 * ThumsbnailViewerServiceImpl(); return
		 * thumsbnailViewerServiceImpl.getImageUrls();
		 */
	}
	
	
	@RequestMapping(value = "/thumbnailsAsJson", method = RequestMethod.GET)
	public List<String> listThumbnailsAsJson() {
		
		
		  ThumsbnailViewerServiceImpl thumsbnailViewerServiceImpl = new
		 ThumsbnailViewerServiceImpl(); 
		  return thumsbnailViewerServiceImpl.getImageUrls();
		 
	}
	
	
	
	
}
