<script setup lang="ts">
import Login from "@/Login.vue";
import Chat from "@/Chat.vue";
import { useLocalStorage, useWebSocket } from "@vueuse/core";
import Lobby from "@/Lobby.vue";
import Game from "@/game/Game.vue";
import { type Event, useChat, useGame, useLobby } from "@/game/game.ts";
import type { Action } from "@/game/action/Action.ts";
import { Icon } from "@iconify/vue";
import { ref } from "vue";

const username = useLocalStorage("username", "")

const {players: lobbyPlayers, onMessage: onLobbyMessage} = useLobby()
const {state, players: gamePlayers, onMessage: onGameMessage} = useGame()
const {messages, addSystemMessage, addMessage} = useChat()

const isChatOpen = ref(true);

const {status, send, open} = useWebSocket(() =>
  (`ws://localhost:49152/game/${username.value}`), {
  immediate: false,
  autoConnect: false,
  autoReconnect: false,
  onConnected: (socket) => console.log("connection opened"),
  onDisconnected: (socket) => console.log("connection closed"),
  onError: (socket, err) => console.warn("connection error", err),
  onMessage: (socket, {data}: MessageEvent<string>) => {
    switch (data) {
      case "SERVER_IS_STARTING":
        console.log("received :", data)
        addSystemMessage("Server is starting, retry after a few seconds")
        socket.close()
        return
      case "GAME_ALREADY_STARTED":
        console.log("received :", data)
        addSystemMessage("User name already taken, please choose another one")
        socket.close()
        return
      case "NAME_ALREADY_USED":
        console.log("received :", data)
        addSystemMessage("User name already taken, please choose another one")
        socket.close()
        return
      case "ALREADY_PLAYED":
        console.log("received :", data)
        addSystemMessage("You already ended your turn. Wait for the others.")
        socket.close()
        return
      default:
        const parsed = JSON.parse(data) as Event
        console.log("parsed :", parsed)
        switch (parsed.type) {
          case "ChatMessage":
            addMessage(parsed.value)
            return
          case "ConnectedPlayers":
          case "LobbyEvent":
            onLobbyMessage(parsed)
            return
          case "TurnStart":
          case "TurnEnd":
          case "TurnUpdate":
            onGameMessage(parsed)
            return
        }
    }
  },
})


function sendChatMessage(text: string) {
  send(JSON.stringify({type: "ChatMessage", value: text}))
}

function setReady(ready: boolean) {
  send(JSON.stringify({type: "LobbyCommand", value: ready ? "READY" : "UNREADY"}))
}

function sendAction(action: Action) {
  send(JSON.stringify({type: "Action", value: action}))
}

</script>

<template>
  <template v-if="status==='CONNECTING'">
    <span>Connecting...</span>
  </template>
  <template v-else-if="status==='CLOSED'">
    <div class="center-content center-items" style="width: 100%; height: 100%;">
      <Login
        v-model:username="username"
        @connect="open"
      />
    </div>
  </template>
  <template v-else>
    <div
      class="layout" :chat="isChatOpen"
    >
      <Lobby
        v-if="state===undefined"
        :players="lobbyPlayers"
        @ready="setReady($event)"
      />
      <Game
        v-else
        :state="state"
        @action="sendAction($event)"

      />
      <Chat class="chat" :messages="messages" @send="sendChatMessage"/>
      <div class="chat-button-container center-items">
        <div class="chat-button clickable bordered" @click="() => isChatOpen = !isChatOpen">
          <Icon icon="mdi:message-group"/>
        </div>
      </div>
    </div>
  </template>
</template>

<style scoped>
.layout {
  display: grid;
  width: 100%;
  height: 100%;
  max-height: 100%;
  grid-template-columns: 1fr 0;
  background-color: #f8f8f8;
  transition: all 0.5s;

  &[chat=true] {
    grid-template-columns: 1fr 20vw;
  }
}

.chat {
  border-left: 1px solid transparent;
  padding: 8px;
  height: 100%;
  transition: all 0.5s;

  &:focus-within {
    border-left: 1px solid #ccc;
    box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
    background-color: white;
  }
}

.chat-button-container {
  position: fixed;
  right: 0;
  top: 0;
  bottom: 0;

  .chat-button {
    display: flex;
    justify-content: center;
    align-items: center;

    height: 4rem;
    width: 4rem;
    border-radius: 50% 0 0 50%;

    position: relative;
    left: 50%;

    &:hover {
      transform: translateX(-50%);
    }
  }

}
</style>
