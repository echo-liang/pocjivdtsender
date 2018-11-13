package pssg.poc.vph.jivtdsender.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import pssg.poc.common.model.justinDispute.DisputeTicketBundle;

/**
 * The Class KafkaConsumerConfig.
 * @author HLiang
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig {
	
	/** The bootstrap address. */
	@Value(value = "${KAFKA_BOOTSTRAP_ADDRESS}")
    private String bootstrapAddress;
 
    /**
     * Consumer factory.
     *
     * @param groupId the group id
     * @return the consumer factory
     */
	public ConsumerFactory<String, String> consumerFactory(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * String kafka listener container factory.
     *
     * @return the concurrent kafka listener container factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> stringKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory("GROUP1"));
        return factory;
    }
    
    
    /**
     * Dispute bundle consumer factory.
     *
     * @return the consumer factory
     */
    public ConsumerFactory<String, DisputeTicketBundle> disputeBundleConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "disputeBundle");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(DisputeTicketBundle.class));
    }

 
    /**
     * Dispute bundle listener container factory.
     *
     * @return the concurrent kafka listener container factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DisputeTicketBundle> disputeBundleListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DisputeTicketBundle> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(disputeBundleConsumerFactory());
        return factory;
    }
}