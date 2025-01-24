package fr.ecoders.quarkus;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Utils {
  private Utils() {/**/}

  private static JsonSerializer<?> sealedSerializer(List<Class<?>> classes,
    JsonSerializer<?> defaultSerializer) {
    return new JsonSerializer<>() {
      @Override
      public void serialize(Object v, JsonGenerator generator, SerializerProvider provider)
      throws IOException {
        var clazz = v.getClass();
        var typeStr = clazz.getSimpleName();
        generator.writeStartObject();
        generator.writeStringField("type", typeStr);
        generator.writeFieldName("value");
        if (classes.stream()
          .anyMatch(c -> c.isInstance(v))) {
          @SuppressWarnings("unchecked")
          var s = (JsonSerializer<Object>) defaultSerializer;
          s.serialize(v, generator, provider);
        } else {
          provider.defaultSerializeValue(v, generator);
        }
        generator.writeEndObject();
      }
    };
  }

  public static void addSealedSerializer(ObjectMapper mapper, List<Class<?>> classes) {
    List.copyOf(classes)
      .forEach(clazz -> {
        if (!clazz.isSealed() || !clazz.isInterface()) {
          throw new IllegalArgumentException(clazz + " must be a sealed interface");
        }
      });
    mapper.registerModule(new SimpleModule().setSerializerModifier(new BeanSerializerModifier() {

//      @Override
//      public JsonSerializer<?> modifyArraySerializer(SerializationConfig config, ArrayType valueType,
//        BeanDescription beanDesc, JsonSerializer<?> serializer) {
//        var clazz = valueType.getContentType();
//        return classes.stream().anyMatch(clazz::isTypeOrSubTypeOf)
//          ? sealedSerializer(classes, serializer)
//          : serializer;
//      }
//
//      @Override
//      public JsonSerializer<?> modifyMapSerializer(SerializationConfig config, MapType valueType,
//        BeanDescription beanDesc, JsonSerializer<?> serializer) {
//        var clazz = valueType.getContentType();
//        return classes.stream().anyMatch(clazz::isTypeOrSubTypeOf)
//          ? sealedSerializer(classes, serializer)
//          : serializer;
//      }
//
//      @Override
//      public JsonSerializer<?> modifyMapLikeSerializer(SerializationConfig config, MapLikeType valueType,
//        BeanDescription beanDesc, JsonSerializer<?> serializer) {
//        var clazz = valueType.getContentType();
//        return classes.stream().anyMatch(clazz::isTypeOrSubTypeOf)
//          ? sealedSerializer(classes, serializer)
//          : serializer;
//      }
//
//      @Override
//      public JsonSerializer<?> modifyCollectionSerializer(SerializationConfig config, CollectionType valueType,
//        BeanDescription beanDesc, JsonSerializer<?> serializer) {
//        var clazz = valueType.getContentType();
//        return classes.stream().anyMatch(clazz::isTypeOrSubTypeOf)
//          ? sealedSerializer(classes, serializer)
//          : serializer;
//      }
//
//      @Override
//      public JsonSerializer<?> modifyCollectionLikeSerializer(SerializationConfig config, CollectionLikeType valueType,
//        BeanDescription beanDesc, JsonSerializer<?> serializer) {
//        var clazz = valueType.getContentType();
//        return classes.stream().anyMatch(clazz::isTypeOrSubTypeOf)
//          ? sealedSerializer(classes, serializer)
//          : serializer;
//      }

      @Override
      public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc,
        JsonSerializer<?> serializer) {
        var clazz = beanDesc.getBeanClass();
        return classes.stream().anyMatch(c->c.isAssignableFrom(clazz))
          ? sealedSerializer(classes, serializer)
          : serializer;
      }
    }));
  }

  public static <V> void addSealedDeserializer(ObjectMapper mapper, Class<V> sealedInterface) {
    if (!sealedInterface.isSealed() || !sealedInterface.isInterface()) {
      throw new IllegalArgumentException("must be a sealed interface");
    }
    var types = Arrays.stream(sealedInterface.getPermittedSubclasses())
      .collect(Collectors.toUnmodifiableMap(Class::getSimpleName, Function.identity()));
    mapper.registerModule(new SimpleModule().addDeserializer(
      sealedInterface, new JsonDeserializer<V>() {
        @Override
        public V deserialize(JsonParser parser, DeserializationContext ctx)
        throws IOException, JacksonException {
          var codec = parser.getCodec();
          var node = (JsonNode) codec.readTree(parser);
          var strType = node.get("type")
            .asText();
          var type = types.get(strType);
          if (type == null) {
            throw new JsonParseException(parser, "Unknown type: " + strType);
          }
          var value = node.get("value");
          @SuppressWarnings("unchecked")
          var v = (V) codec.readValue(value.traverse(), type);
          return v;
        }
      }));
  }
}
