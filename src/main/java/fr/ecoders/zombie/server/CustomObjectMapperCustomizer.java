package fr.ecoders.zombie.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.ecoders.zombie.Action;
import fr.ecoders.zombie.GameOption;
import fr.ecoders.zombie.Resource;
import fr.ecoders.zombie.card.Building;
import fr.ecoders.zombie.card.Card;
import fr.ecoders.zombie.card.Upgrade;
import fr.ecoders.zombie.card.Zombie;
import fr.ecoders.zombie.state.Camp;
import fr.ecoders.zombie.state.LocalGameState;
import fr.ecoders.zombie.state.ResourceBank;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Singleton
public class CustomObjectMapperCustomizer implements ObjectMapperCustomizer {

  private static final JsonSerializer<Instant> INSTANT_JSON_SERIALIZER =
    new StdSerializer<>(Instant.class) {
      @Override
      public void serialize(Instant instant, JsonGenerator generator, SerializerProvider provider)
      throws IOException {
        generator.writeNumber(instant.toEpochMilli());
      }
    };
  private static final JsonSerializer<ResourceBank> RESOURCE_BANK_JSON_SERIALIZER =
    new StdSerializer<>(ResourceBank.class) {
      @Override
      public void serialize(ResourceBank resourceBank, JsonGenerator jsonGenerator,
        SerializerProvider serializerProvider)
      throws IOException {
        var v = resourceBank.resources();
        serializerProvider.defaultSerializeValue(v, jsonGenerator);
      }
    };
  private static final JsonSerializer<Building> BUILDING_JSON_SERIALIZER =
    new StdSerializer<>(Building.class) {
      @Override
      public void serializeWithType(Building value, JsonGenerator gen, SerializerProvider serializers,
        TypeSerializer typeSer) throws IOException {
        serialize(value, gen, serializers);
      }

      @Override
      public void serialize(Building building, JsonGenerator generator,
        SerializerProvider provider)
      throws IOException {
        generator.writeStartObject();
        generator.writeStringField("type", "Building");
        generator.writeObjectFieldStart("value");
        generator.writeStringField("name", building.name());
        generator.writeFieldName("cost");
        provider.defaultSerializeValue(building.cost(), generator);
        generator.writeFieldName("production");
        provider.defaultSerializeValue(building.production(), generator);
        generator.writeFieldName("search");
        provider.defaultSerializeValue(building.search(), generator);
        generator.writeFieldName("electrified");
        provider.defaultSerializeValue(building.electrified(), generator);
        generator.writeEndObject();
        generator.writeEndObject();
      }
    };
  private static final JsonSerializer<Zombie> ZOMBIE_JSON_SERIALIZER =
    new StdSerializer<>(Zombie.class) {
      @Override
      public void serializeWithType(Zombie value, JsonGenerator gen, SerializerProvider serializers,
        TypeSerializer typeSer) throws IOException {
        serialize(value, gen, serializers);
      }

      @Override
      public void serialize(Zombie event, JsonGenerator generator,
        SerializerProvider provider)
      throws IOException {
        generator.writeStartObject();
        generator.writeStringField("type", "Zombie");
        generator.writeFieldName("value");
        generator.writeStartObject();
        generator.writeStringField("name", event.name());
        generator.writeNumberField("count", event.count());
        generator.writeEndObject();
        generator.writeEndObject();
      }
    };
  private static final JsonSerializer<Upgrade> UPGRADE_JSON_SERIALIZER =
    new StdSerializer<>(Upgrade.class) {
      @Override
      public void serializeWithType(Upgrade value, JsonGenerator gen, SerializerProvider serializers,
        TypeSerializer typeSer) throws IOException {
        serialize(value, gen, serializers);
      }

      @Override
      public void serialize(Upgrade upgrade, JsonGenerator generator,
        SerializerProvider provider)
      throws IOException {
        generator.writeStartObject();
        generator.writeStringField("type", "Upgrade");
        generator.writeObjectFieldStart("value");
        generator.writeStringField("name", upgrade.name());
        generator.writeFieldName("cost");
        provider.defaultSerializeValue(upgrade.cost(), generator);
        generator.writeFieldName("production");
        provider.defaultSerializeValue(upgrade.production(), generator);
        generator.writeBooleanField("isPowerGenerator", upgrade.isPowerGenerator());
        generator.writeBooleanField("isUnique", upgrade.isUnique());
        generator.writePOJOField("categoryFilter", upgrade.categoryFilter());
        generator.writeEndObject();
        generator.writeEndObject();
      }
    };
  private static final JsonSerializer<ServerEvent.ChatMessage> CHAT_MESSAGE_JSON_SERIALIZER =
    new StdSerializer<>(ServerEvent.ChatMessage.class) {
      @Override
      public void serialize(ServerEvent.ChatMessage chatMessage, JsonGenerator generator, SerializerProvider provider)
      throws IOException {
        generator.writeStartObject();
        generator.writeStringField("type", "ChatMessage");
        generator.writeFieldName("value");
        generator.writeStartObject();
        generator.writeStringField("username", chatMessage.username());
        generator.writeStringField("text", chatMessage.text());
        generator.writeFieldName("timestamp");
        provider.defaultSerializeValue(chatMessage.timestamp(), generator);
        generator.writeEndObject();
        generator.writeEndObject();
      }
    };
  private static final JsonSerializer<ServerEvent.LobbyEvent> LOBBY_EVENT_JSON_SERIALIZER =
    new StdSerializer<>(ServerEvent.LobbyEvent.class) {
      @Override
      public void serialize(ServerEvent.LobbyEvent lobbyEvent, JsonGenerator generator, SerializerProvider provider)
      throws IOException {
        generator.writeStartObject();
        generator.writeStringField("type", "LobbyEvent");
        generator.writeFieldName("value");
        generator.writeStartObject();
        generator.writeStringField("username", lobbyEvent.player());
        generator.writeFieldName("state");
        provider.defaultSerializeValue(lobbyEvent.state(), generator);
        generator.writeEndObject();
        generator.writeEndObject();
      }
    };
  private static final JsonSerializer<ServerEvent.ConnectedPlayers> CONNECTED_PLAYERS_JSON_SERIALIZER =
    new StdSerializer<>(ServerEvent.ConnectedPlayers.class) {
      @Override
      public void serialize(ServerEvent.ConnectedPlayers v, JsonGenerator generator, SerializerProvider provider)
      throws IOException {
        generator.writeStartObject();
        generator.writeStringField("type", "ConnectedPlayers");
        generator.writeFieldName("value");
        provider.defaultSerializeValue(v.players(), generator);
        generator.writeEndObject();
      }
    };
  private static final JsonSerializer<LocalGameState> LOCAL_GAME_STATE_JSON_SERIALIZER =
    new StdSerializer<>(LocalGameState.class) {
      @Override
      public void serialize(LocalGameState state, JsonGenerator generator,
        SerializerProvider provider)
      throws IOException {
        generator.writeStartObject();
        generator.writeFieldName("camps");
        provider.defaultSerializeValue(state.camps(), generator);
        generator.writeFieldName("currentPlayer");
        provider.defaultSerializeValue(state.currentPlayer(), generator);
        var hand = state.hand();
        generator.writeArrayFieldStart("hand");
        for (int i = 0; i < hand.size(); i++) {
          var card = hand.get(i);
          generator.writePOJO(Map.of("card", card, "actions", Action.handActionOf(i, state)));
        }
        generator.writeEndArray();
        generator.writeEndObject();
      }
    };
  private static final JsonSerializer<ServerEvent.TurnUpdate> TURN_UPDATE_JSON_SERIALIZER =
    new StdSerializer<>(ServerEvent.TurnUpdate.class) {
      @Override
      public void serialize(ServerEvent.TurnUpdate stateWrapper, JsonGenerator generator,
        SerializerProvider provider)
      throws IOException {
        var state = stateWrapper.state();
        generator.writeStartObject();
        generator.writeStringField("type", "TurnUpdate");
        generator.writeFieldName("value");
        provider.defaultSerializeValue(state, generator);
        generator.writeEndObject();
      }
    };
  private static final JsonSerializer<ServerEvent.TurnStart> TURN_START_JSON_SERIALIZER =
    new StdSerializer<>(ServerEvent.TurnStart.class) {
      @Override
      public void serialize(ServerEvent.TurnStart stateWrapper, JsonGenerator generator,
        SerializerProvider provider)
      throws IOException {
        var state = stateWrapper.state();
        generator.writeStartObject();
        generator.writeStringField("type", "TurnStart");
        generator.writeFieldName("value");
        provider.defaultSerializeValue(state, generator);
        generator.writeEndObject();
      }
    };
  private static final JsonSerializer<ServerEvent.TurnEnd> TURN_END_JSON_SERIALIZER =
    new StdSerializer<>(ServerEvent.TurnEnd.class) {
      @Override
      public void serialize(ServerEvent.TurnEnd stateWrapper, JsonGenerator generator,
        SerializerProvider provider)
      throws IOException {
        var state = stateWrapper.state();
        generator.writeStartObject();
        generator.writeStringField("type", "TurnEnd");
        generator.writeFieldName("value");
        provider.defaultSerializeValue(state, generator);
        generator.writeEndObject();
      }
    };
  private static final JsonSerializer<Camp> CAMP_JSON_SERIALIZER =
    new StdSerializer<>(Camp.class) {
      @Override
      public void serialize(Camp camp, JsonGenerator generator, SerializerProvider provider)
      throws IOException {
        generator.writeStartObject();
        generator.writeNumberField("maxBuildCount", camp.maxBuildCount());
        generator.writeNumberField("availableSpace", camp.availableSpace());
        generator.writeBooleanField("isSpaceAvailable", camp.isSpaceAvailable());
        generator.writeFieldName("searchCost");
        provider.defaultSerializeValue(Camp.SEARCH_COST, generator);
        generator.writeFieldName("production");
        provider.defaultSerializeValue(camp.production(), generator);
        generator.writeFieldName("buildings");
        provider.defaultSerializeValue(camp.buildings(), generator);
        generator.writeFieldName("searches");
        provider.defaultSerializeValue(camp.searches(), generator);
        generator.writeEndObject();
      }
    };
  private static final JsonSerializer<Action> ACTION_JSON_SERIALIZER =
    new StdSerializer<>(Action.class) {
      @Override
      public void serialize(Action action, JsonGenerator generator,
        SerializerProvider provider) throws IOException {
        generator.writeStartObject();
        generator.writeStringField("type", action.getClass().getSimpleName());
        generator.writeFieldName("value");
        generator.writeStartObject();
        switch (action) {
          case Action.CancelSearch(int index) -> generator.writeNumberField("index", index);
          case Action.Construct(int index) -> generator.writeNumberField("index", index);
          case Action.DestroyBuilding(int index) -> generator.writeNumberField("index", index);
          case Action.Search(int index) -> generator.writeNumberField("index", index);
          case Action.Discard(int index) -> generator.writeNumberField("index", index);
          case Action.SendZombie(String username, int index) -> {
            generator.writeNumberField("index", index);
            generator.writeStringField("username", username);
          }
          case Action.UpgradeBuilding u -> {
            generator.writeNumberField("upgradeIndex", u.upgradeIndex());
            generator.writeNumberField("buildingIndex", u.buildingIndex());
          }
          case Action.DrawCard _ -> throw new AssertionError("Not implemented");
        }
        generator.writeEndObject();
        generator.writeEndObject();

      }
    };

  private static final JsonDeserializer<Instant> INSTANT_JSON_DESERIALIZER =
    new StdDeserializer<>(Instant.class) {
      @Override
      public Instant deserialize(JsonParser parser, DeserializationContext deserializationContext)
      throws IOException {
        return Instant.ofEpochMilli(parser.readValueAs(Long.class));
      }
    };
  private static final JsonDeserializer<PlayerCommand> PLAYER_COMMAND_JSON_DESERIALIZER =
    new StdDeserializer<>(PlayerCommand.class) {
      @Override
      public PlayerCommand deserialize(JsonParser jsonParser, DeserializationContext context)
      throws IOException {
        var codec = jsonParser.getCodec();
        var root = (JsonNode) codec.readTree(jsonParser);
        var typeNode = root.get("type");
        var type = typeNode.asText();
        var valueNode = root.get("value");
        return switch (type) {
          case "ChatMessage" -> new PlayerCommand.ChatMessage(valueNode.asText());
          case "LobbyCommand" -> context.readTreeAsValue(valueNode, PlayerCommand.LobbyCommand.class);
          case "Action" -> new PlayerCommand.ActionWrapper(context.readTreeAsValue(valueNode, Action.class));
          default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
      }
    };
  private static final JsonDeserializer<Action> ACTION_JSON_DESERIALIZER =
    new StdDeserializer<>(Action.class) {
      @Override
      public Action deserialize(JsonParser jsonParser, DeserializationContext context)
      throws IOException {
        var codec = jsonParser.getCodec();
        var root = (JsonNode) codec.readTree(jsonParser);
        var type = getAs(root.get("type"), JsonNode::asText).orElseThrow(
          () -> new IllegalArgumentException("Missing type in " + root.toPrettyString()));
        return (switch (type) {
          case "Discard" -> readTreeAs(root.get("value"), context, Action.Discard.class);
          case "DrawCard" -> Optional.of(Action.DRAW_CARD);
          case "UpgradeBuilding" -> readTreeAs(root.get("value"), context, Action.UpgradeBuilding.class);
          case "SendZombie" -> readTreeAs(root.get("value"), context, Action.SendZombie.class);
          case "Construct" -> readTreeAs(root.get("value"), context, Action.Construct.class);
          case "Search" -> readTreeAs(root.get("value"), context, Action.Search.class);
          case "DestroyBuilding" -> readTreeAs(root.get("value"), context, Action.DestroyBuilding.class);
          case "CancelSearch" -> readTreeAs(root.get("value"), context, Action.CancelSearch.class);
          default -> throw new IllegalArgumentException("Unknown Action type " + type);
        }).orElseThrow(() -> new IllegalArgumentException("Missing value in " + root.toPrettyString()));
      }
    };
  private static final JsonDeserializer<ResourceBank> RESOURCE_BANK_JSON_DESERIALIZER =
    new StdDeserializer<>(ResourceBank.class) {
      @Override
      public ResourceBank deserialize(JsonParser jsonParser, DeserializationContext context)
      throws IOException {
        return new ResourceBank(jsonParser.readValueAs(new TypeReference<HashMap<Resource, Integer>>() {}));
      }
    };
  private static final JsonDeserializer<Card> CARD_JSON_DESERIALIZER =
    new StdDeserializer<>(Card.class) {
      @Override
      public Card deserialize(JsonParser jsonParser, DeserializationContext context)
      throws IOException {
        var codec = jsonParser.getCodec();
        var root = (JsonNode) codec.readTree(jsonParser);
        var type = getAs(root.get("type"), JsonNode::asText).orElseThrow(
          () -> new IllegalArgumentException("Missing type in " + root.toPrettyString()));
        return switch (type) {
          case "Building" -> context.readTreeAsValue(root, Building.class);
          case "Zombie" -> context.readTreeAsValue(root, Zombie.class);
          case "Upgrade" -> {
            var name = getAs(root.get("name"), JsonNode::asText).orElseThrow(
              () -> new IllegalArgumentException("Missing name in " + root.toPrettyString()));
            var production = readTreeAs(root.get("production"), context, ResourceBank.class).orElse(ResourceBank.EMPTY);
            var cost = readTreeAs(root.get("cost"), context, ResourceBank.class).orElse(ResourceBank.EMPTY);
            var isPowerGenerator = getAs(root.get("isPowerGenerator"), JsonNode::asBoolean).orElse(false);
            var isUnique = getAs(root.get("isUnique"), JsonNode::asBoolean).orElse(false);
            var categoryFilter = readTreeAs(root.get("categoryFilter"), context, Building.Category.class);
            yield new Upgrade(name, cost, production, isPowerGenerator, isUnique, categoryFilter);
          }
          default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
      }
    };
  private static final JsonDeserializer<GameOption.CardOption> CARD_OPTION_JSON_DESERIALIZER =
    new StdDeserializer<>(GameOption.CardOption.class) {
      @Override
      public GameOption.CardOption deserialize(JsonParser jsonParser, DeserializationContext context)
      throws IOException {
        var codec = jsonParser.getCodec();
        var root = (JsonNode) codec.readTree(jsonParser);
        var replica = getAs(root.get("replica"), JsonNode::asInt).orElse(1);
        var card = readTreeAs(root, context, Card.class).orElseThrow();
        return new GameOption.CardOption(card, replica);
      }
    };


  private static <V> Optional<V> getAs(JsonNode node, Function<? super JsonNode, ? extends V> mapper) {
    return Optional.ofNullable(node)
      .map(mapper);
  }

  private static <V> Optional<V> readTreeAs(JsonNode node, DeserializationContext context, Class<V> type)
  throws IOException {
    return node == null ? Optional.empty() : Optional.of(context.readTreeAsValue(node, type));
  }

  @Override
  public int priority() {
    return MINIMUM_PRIORITY;
  }

  @Override
  public void customize(ObjectMapper mapper) {
    var module = new SimpleModule();

    module.addSerializer(Instant.class, INSTANT_JSON_SERIALIZER);
    module.addSerializer(ResourceBank.class, RESOURCE_BANK_JSON_SERIALIZER);
    module.addSerializer(Building.class, BUILDING_JSON_SERIALIZER);
    module.addSerializer(Zombie.class, ZOMBIE_JSON_SERIALIZER);
    module.addSerializer(Upgrade.class, UPGRADE_JSON_SERIALIZER);
    module.addSerializer(Camp.class, CAMP_JSON_SERIALIZER);
    module.addSerializer(ServerEvent.ChatMessage.class, CHAT_MESSAGE_JSON_SERIALIZER);
    module.addSerializer(ServerEvent.LobbyEvent.class, LOBBY_EVENT_JSON_SERIALIZER);
    module.addSerializer(ServerEvent.ConnectedPlayers.class, CONNECTED_PLAYERS_JSON_SERIALIZER);
    module.addSerializer(LocalGameState.class, LOCAL_GAME_STATE_JSON_SERIALIZER);
    module.addSerializer(ServerEvent.TurnUpdate.class, TURN_UPDATE_JSON_SERIALIZER);
    module.addSerializer(ServerEvent.TurnStart.class, TURN_START_JSON_SERIALIZER);
    module.addSerializer(ServerEvent.TurnEnd.class, TURN_END_JSON_SERIALIZER);
    module.addSerializer(Action.class, ACTION_JSON_SERIALIZER);

    module.addDeserializer(Instant.class, INSTANT_JSON_DESERIALIZER);
    module.addDeserializer(PlayerCommand.class, PLAYER_COMMAND_JSON_DESERIALIZER);
    module.addDeserializer(Action.class, ACTION_JSON_DESERIALIZER);
    module.addDeserializer(Card.class, CARD_JSON_DESERIALIZER);
    module.addDeserializer(ResourceBank.class, RESOURCE_BANK_JSON_DESERIALIZER);
    module.addDeserializer(GameOption.CardOption.class, CARD_OPTION_JSON_DESERIALIZER);

    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.registerModule(module);
    mapper.configOverride(Collection.class)
      .setSetterInfo(JsonSetter.Value.forContentNulls(Nulls.AS_EMPTY));
  }
}
