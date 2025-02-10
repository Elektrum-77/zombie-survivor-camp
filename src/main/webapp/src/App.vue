<script setup lang="ts">
import Login from "@/Login.vue";
import Chat from "@/Chat.vue";
import { useLocalStorage, useWebSocket } from "@vueuse/core";
import Lobby from "@/Lobby.vue";
import Game from "@/Game.vue";
import { type Event, useChat, useGame, useLobby } from "@/game.ts";
import type { HandCardAction } from "@/Action.ts";

const username = useLocalStorage("username", "")

const {players: lobbyPlayers, onMessage: onLobbyMessage} = useLobby()
const {state, players: gamePlayers, onMessage: onGameMessage} = useGame()
const {messages, addSystemMessage, addMessage} = useChat()

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
          case "GameState":
          case "TurnStart":
          case "TurnEnd":
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

function sendAction(action: HandCardAction) {
  send(JSON.stringify({type: "Action", value: action}))
}

</script>

<template>
  <div>
    <div class="layout">
      <span v-if="status==='CONNECTING'">Connecting...</span>
      <Login
        v-else-if="status==='CLOSED'"
        v-model:username="username"
        @connect="open"
      />
      <Lobby
        v-else-if="state===undefined"
        :players="lobbyPlayers"
        @ready="setReady($event)"
      />
      <Game
        v-else
        :state="state"
        @action="sendAction($event)"
      />
      <Chat class="chat" :messages="messages" @send="sendChatMessage" />
    </div>
  </div>
</template>

<style scoped>
.layout {
  display: grid;
  width: 100%;
  height: 100%;
  max-height: 100%;
  grid-template-columns: 1fr 20vw;
  background-color: #f8f8f8;
}

.chat {
  border-left: 1px solid transparent;
  padding: 8px;
  height: 100vh;
  transition: all 0.5s;
}

.chat:focus-within {
  border-left: 1px solid #ccc;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
  background-color: white;
}
</style>
