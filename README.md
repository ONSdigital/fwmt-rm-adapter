# fwmt-rm-adapter

This service is a gateway between the Response Management System and the FWMT job service.

It takes an Action Instruction (Request or Delete) and transforms it into a FWMT Common Object of the matching type, and then sends it to the Job Service. 
It also accepts a TM response message from the JobSvc, transforms it to XML and then sends it back to RM.


All communication with RM and the JobSvc is done via AMPQ queues using RabbitMQ.

Requires RabbitMQ to start:

	docker run --name rabbit -p 5671-5672:5671:5672 -p 15671-15672:15671-15672 -d rabbitmq:3.6-management

To run:

	./gradlew bootRun
