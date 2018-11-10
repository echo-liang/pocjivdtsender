package pssg.poc.vph.jivtdsender.controller;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import pssg.poc.common.model.justinDispute.DisputeTicketBundle;


/**
 * The Class JivtdsenderController.
 * @author HLiang
 */
@RestController
public class JivtdsenderController {
	/**
     * The logger.
     */
    private static Logger logger = LoggerFactory.getLogger(JivtdsenderController.class);
    
    /** The Constant KAFKA_TOPIC_NAME_STRING. */
    private final static String KAFKA_TOPIC_NAME_STRING = "my_topic";
    
    /** The Constant KAFKA_TOPIC_NAME_OBJ. */
    private final static String KAFKA_TOPIC_NAME_OBJ = "my_topic_obj";
    
    /** The jivtd api uri. */
    @Value(value = "${JI_API_URI}")
    private String jivtd_api_uri;
    
    
    /**
     * Home.
     *
     * @return the string
     */
    @GetMapping("/") 
    public String home(){
		logger.info("Home!");
    	return "{\"message\" : \"Welcome to VPHVTD!\"}";
    }
    
    /**
     * Dispute bundle listen.
     *
     * @param disputeBundle the dispute bundle
     */
    @KafkaListener(topics = KAFKA_TOPIC_NAME_OBJ, containerFactory = "disputeBundleListenerContainerFactory")
    public void disputeBundleListen(DisputeTicketBundle disputeBundle) {
        logger.info("Received disputeBundle with ticketNO[{}]", disputeBundle.getTicket_number());
        
        ResponseEntity<String> pocjivtdResponse = null;
        pocjivtdResponse = sendDispute(disputeBundle);
        if (pocjivtdResponse != null) {
            logger.info("Dispute ticket sent to Justin Inbox: {}", pocjivtdResponse.getBody());
        } else {
            logger.info("Unable to send dispute ticket to Justin");
        }
    }

    /**
     * Listen.
     *
     * @param message the message
     */
    @KafkaListener(topics = KAFKA_TOPIC_NAME_STRING, containerFactory = "stringKafkaListenerContainerFactory")
    public void listen(String message) {
        logger.info("Received GROUP1 message [{}]", message);
    }
    
    /**
     * Send dispute.
     *
     * @param payload the payload
     * @return the response entity
     */
    private ResponseEntity<String> sendDispute(final DisputeTicketBundle payload) {
    	logger.info("Start sending dispute ticket to justing inbox.");
    	boolean isReadyToSend = true;
        
        String vphPayload = null;
      
        try {
        	//Object to JSON in String
            ObjectMapper mapper = new ObjectMapper();
            vphPayload = mapper.writeValueAsString(payload);
        } catch (Exception ex) {
            logger.info("Exception converting request : " + ex.getMessage());
            isReadyToSend = false;
        }
        if (isReadyToSend && vphPayload != null ) {
            try {
                logger.info("Sending request...");
                RestTemplate restTemplate = new RestTemplate();

                String url = jivtd_api_uri + "vtdispute";
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
                URI uri = builder.build().encode().toUri();

                logger.info("URL: {}", uri);

                HttpEntity<Object> request = getRequest(vphPayload);

                return restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            } catch (Exception ex) {
                logger.info("Exception sending request : " + ex.getMessage());

            }
        }
        return null;
    }

    /**
     * Gets the request.
     *
     * @param payload the payload
     * @return the request
     */
    private HttpEntity<Object> getRequest(final Object payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        if (payload != null) {
            return new HttpEntity<Object>(payload, headers);
        } else {
            return new HttpEntity<Object>(headers);
        }

    }
}
