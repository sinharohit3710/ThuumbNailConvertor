This project lets you upload a normal image and create a thubnail for the same .

Technologies Used - AWS S3 Bucket , AWS SNS(Simple Notification Service) , AWS SQS (Simple Queue Service ) , Java , Vaniall JS  , HTML.

When you upload a image file using index.html , it is saved in the AWS S3 Bucket .

Uploading an image creates an event and sends it to AWS SNS Topic.

There is queue which subcribes to the SNS Topic and receives the notification for an object creation S3 Bucket.

There is a listener written to listen to SQS queue and convert the newly added image to thumbnail using Java library "thumbnailator".

This Service can be accessed at http://ec2-54-242-137-169.compute-1.amazonaws.com:8080/. OR You can clone the project from github location 
and run it as executable jar.