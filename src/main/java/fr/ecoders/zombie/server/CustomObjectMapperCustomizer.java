package fr.ecoders.zombie.server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import fr.ecoders.quarkus.SealedDeserializer;
import fr.ecoders.zombie.Action;
import fr.ecoders.zombie.Card;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Singleton
public class CustomObjectMapperCustomizer implements ObjectMapperCustomizer {
  @Override
  public void customize(ObjectMapper mapper) {
    var classes = List.of(ServerEvent.class, Card.class);
    mapper.registerModule(new SimpleModule().setSerializerModifier(new BeanSerializerModifier() {
      @Override
      public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc,
        JsonSerializer<?> serializer) {
        var isSub = classes.stream()
          .anyMatch(c -> c.isAssignableFrom(beanDesc.getBeanClass()));
        return isSub ? new JsonSerializer<>() {
          @Override
          public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider serializers,
            TypeSerializer typeSer) throws IOException {
            serialize(value, gen, serializers);
          }

          @Override
          public void serialize(Object value, JsonGenerator generator, SerializerProvider provider)
          throws IOException {
            var clazz = value.getClass();
            var type = clazz.getSimpleName();
            generator.writeStartObject();
            generator.writeStringField("type", type);
            generator.writeFieldName("value");
            @SuppressWarnings("unchecked")
            var s = (JsonSerializer<Object>) serializer;
            s.serialize(value, generator, provider);
            generator.writeEndObject();
          }
        } : serializer;
      }
    }));
    //      SealedSerializer.addSealedSerializer(Card.class, mapper);
    //      SealedSerializer.addSealedSerializer(Event.class, mapper);
    SealedDeserializer.addSealedDeserializer(PlayerCommand.class, mapper);
    SealedDeserializer.addSealedDeserializer(Action.class, mapper);
  }
}
