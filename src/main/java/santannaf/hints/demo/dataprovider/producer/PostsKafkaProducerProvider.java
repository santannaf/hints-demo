package santannaf.hints.demo.dataprovider.producer;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import santannaf.hints.demo.entity.Post;
import santannaf.hints.demo.provider.SendEventPostsProvider;

@Component
public class PostsKafkaProducerProvider implements SendEventPostsProvider {

    private static final Logger LOG = LoggerFactory.getLogger(PostsKafkaProducerProvider.class);

    private static final Schema POST_SCHEMA = SchemaBuilder.record("Post")
            .namespace("santannaf.hints.demo.entity")
            .fields()
            .requiredLong("id")
            .requiredString("title")
            .requiredString("userId")
            .requiredString("body")
            .endRecord();

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public PostsKafkaProducerProvider(KafkaTemplate<String, Object> kafkaTemplate,
                                     @Value("${kafka.topic.posts}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void sendEvent(Post post) {
        GenericRecord event = toRecord(post);

        kafkaTemplate.send(topic, event).handle((result, error) -> {
            if (error != null) {
                LOG.error("error at publisher event to leader, error is: {}", error.getMessage());
            } else {
                LOG.info("event was sent");
            }
            return null;
        });
    }

    private GenericRecord toRecord(Post post) {
        GenericRecord record = new GenericData.Record(POST_SCHEMA);
        record.put("id", post.id());
        record.put("title", post.title());
        record.put("userId", post.userId());
        record.put("body", post.body());
        return record;
    }
}
