package com.oxg.util;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbBLOB;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class DynamicallyCreateEmail extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly assembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = assembly.getMessage();
		
		
		//get Env
		MbMessage outLocalEnv = new MbMessage(assembly.getLocalEnvironment());
		MbMessage outExceptionList = new MbMessage(assembly.getExceptionList());
		
		
		// create new empty message
		MbMessage outMessage = new MbMessage();
		MbMessageAssembly outAssembly = new MbMessageAssembly(assembly, outLocalEnv, outExceptionList, outMessage);
		MbElement localEnv = outAssembly.getLocalEnvironment().getRootElement();
		
		try {
			// optionally copy message headers
			copyMessageHeaders(inMessage, outMessage);
			// ----------------------------------------------------------
			// Add user code below

			// Create the EmailOutputHeader parser. This is where we add recipient, sender and subject information.
			MbElement root = outMessage.getRootElement();
			MbElement SMTPOutput = root.createElementAsLastChild("EmailOutputHeader"); 

			// Add recipient information to EmailOutputHeader
			SMTPOutput.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "To", "oxg@mail.com"); 		
			SMTPOutput.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Cc", "johndoe@mail.com"); 		
			SMTPOutput.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Bcc", "jt@mail.com");  		

			// Add sender information to EmailOutputHeader 		
			SMTPOutput.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "From", "xyz@mail.com"); 		
			//SMTPOutput.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Reply-To", "");  		

			// Add subject information to EmailOutputHeader 		
			SMTPOutput.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Subject", "Response from Salesforce.com");  		

			// Create Destination.Email. This is where we add SMTP server information 		
			MbElement Destination = localEnv.createElementAsLastChild(MbElement.TYPE_NAME, "Destination", null); 		
			MbElement destinationEmail = Destination.createElementAsLastChild(MbElement.TYPE_NAME, "Email", null);
			
			//SMTP Server
			destinationEmail.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "SMTPServer", "192.168.1.144:2525"); 		 		

			// Set last child of root (message body) 		
			MbElement BLOB = root.createElementAsLastChild(MbBLOB.PARSER_NAME);	 		
			
			
			// Create body of the email	
			String id = inMessage.getRootElement().getLastChild().getFirstElementByPath("/JSON/Data/Id").getValueAsString();
			String name = inMessage.getRootElement().getLastChild().getFirstElementByPath("/JSON/Data/Name").getValueAsString();
			String dept = inMessage.getRootElement().getLastChild().getFirstElementByPath("/JSON/Data/Department").getValueAsString();
			String owner = inMessage.getRootElement().getLastChild().getFirstElementByPath("/JSON/Data/OwnerId").getValueAsString();

			
			String answer = "The return Id : " + id;
			answer += "\n Name is: " + name;
			answer += "\n Dept is: " + dept;
			answer += "\n Owner is : " + owner;
			
			
			
			BLOB.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "BLOB", answer.getBytes());  		
			outMessage.finalizeMessage(MbMessage.FINALIZE_VALIDATE); 	 		
			// End of user code
			// ----------------------------------------------------------
			
		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			throw e;
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);
	}

	
	
	
	public void copyMessageHeaders(MbMessage inMessage, MbMessage outMessage)
			throws MbException {
		MbElement outRoot = outMessage.getRootElement();

		// iterate though the headers starting with the first child of the root
		// element
		MbElement header = inMessage.getRootElement().getFirstChild();
		while (header != null && header.getNextSibling() != null) // stop before
																	// the last
																	// child
																	// (body)
		{
			// copy the header and add it to the out message
			outRoot.addAsLastChild(header.copy());
			// move along to next header
			header = header.getNextSibling();
		}
	}

}
