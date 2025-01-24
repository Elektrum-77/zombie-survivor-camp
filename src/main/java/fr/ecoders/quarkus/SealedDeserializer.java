package fr.ecoders.quarkus;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class SealedDeserializer<I> extends StdDeserializer<I> {
  private final Map<String, Class<? extends I>> types;

  private SealedDeserializer(Class<I> vc) {
    super(vc);
    @SuppressWarnings("unchecked") var subClasses = (Class<? extends I>[]) vc.getPermittedSubclasses();
    types = Arrays.stream(subClasses)
      .collect(Collectors.toUnmodifiableMap(Class::getSimpleName, Function.identity()));
  }

  public static <I> void addSealedDeserializer(Class<I> vc, ObjectMapper mapper) {
    Objects.requireNonNull(vc);
    Objects.requireNonNull(mapper);
    if (!vc.isSealed()) {
      throw new IllegalArgumentException("Not sealed : " + vc.getName());
    }

    mapper.registerModule(new SimpleModule().addDeserializer(vc, new SealedDeserializer<>(vc)));
  }

  private static JsonParser traverse(TreeNode node) throws IOException {
    var p = node.traverse();
    p.nextToken();
    return p;
  }

  @Override
  public I deserialize(JsonParser p, DeserializationContext context) throws IOException {
    var codec = p.getCodec();
    var node = codec.readTree(p);

    var strType = context.readValue(traverse(node.get("type")), String.class);
    var type = types.get(strType);

    if (type == null) {
      throw new JsonParseException(p, "Unknown type : " + strType);
    }

    return context.readValue(traverse(node.get("value")), type);
  }
}
