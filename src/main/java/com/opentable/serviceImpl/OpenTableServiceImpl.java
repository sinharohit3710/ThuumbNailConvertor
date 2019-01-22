package com.opentable.serviceImpl;


import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.springframework.web.multipart.MultipartFile;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.opentable.Listeners.MySQSListener;
import com.opentable.clients.ClientFactory;

public class OpenTableServiceImpl {
	
	AmazonS3 s3client;
	AmazonSQS sqs;
	final String queueName = "firstsqs";
	final String sourceBucket = "restaurantpics";

	public  void uploadAnImage(MultipartFile file) throws JMSException, InterruptedException, IOException {
		// TODO Auto-generated method stub
		ClientFactory clientFactory = new ClientFactory();
		s3client = clientFactory.getS3Client();
		File convFile = new File(file.getOriginalFilename());
	    
		FileOutputStream fos = new FileOutputStream(convFile); 
	    fos.write(file.getBytes());
	    fos.close();
		String fileName = "mainPictures/" + file.getOriginalFilename();
		s3client.putObject(new PutObjectRequest(sourceBucket, fileName, 
				convFile).withCannedAcl(CannedAccessControlList.PublicRead));
	
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
	         
	        callback.waitForOneHourOfSilence();
	        

	        
	        connection.close();
	         
	}

	public AmazonS3 getS3client() {
		return s3client;
	}

	public void setS3client(AmazonS3 s3client) {
		this.s3client = s3client;
	}

	public AmazonSQS getSqs() {
		return sqs;
	}

	public void setSqs(AmazonSQS sqs) {
		this.sqs = sqs;
	}
	
	

}
