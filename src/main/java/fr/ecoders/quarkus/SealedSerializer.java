package fr.ecoders.quarkus;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Objects;

public final class SealedSerializer<I> extends StdSerializer<I> {

  private final JsonSerializer<I> defaultSerializer;

  public SealedSerializer(Class<I> vc, JsonSerializer<I> defaultSerializer) {
    super(vc);
    this.defaultSerializer = defaultSerializer;
  }

  public static <I> void addSealedSerializer(Class<I> vc, ObjectMapper mapper) {
    Objects.requireNonNull(vc);
    Objects.requireNonNull(mapper);
    if (!vc.isSealed()) {
      throw new IllegalArgumentException();
    }

    var module = new SimpleModule();
    module.setSerializerModifier(new BeanSerializerModifier() {
      @Override
      @SuppressWarnings("unchecked")
      public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc,
        JsonSerializer<?> serializer) {
        var isSubClass = vc.isAssignableFrom(beanDesc.getBeanClass());
        return isSubClass ? new SealedSerializer<>(vc, (JsonSerializer<I>) serializer) : serializer;
      }
    });
    mapper.registerModule(module);
  }

  @Override
  public void serialize(I value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    var clazz = value.getClass();
    var type = clazz.getSimpleName();
    gen.writeStartObject();
    gen.writeStringField("type", type);
    gen.writeFieldName("value");
    provider.defaultSerializeValue((Object) value , gen);
    gen.writeEndObject();
  }
}
