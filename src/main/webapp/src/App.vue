<script setup lang="ts">
import { ref, shallowRef, watch } from 'vue';
import Login from "@/Login.vue";
import Chat, { type Message } from "@/Chat.vue";
import { now, useEventListener } from "@vueuse/core";
import Lobby from "@/Lobby.vue";
import Game from "@/Game.vue";
import type { Event, GameState } from "@/game.ts";
import type { Action } from "@/Action.ts";


const socket = shallowRef<WebSocket>()
const username = ref(localStorage.getItem("username") ?? "")
watch(username, v => localStorage.setItem("username", v))

const players = ref<Record<string, boolean>>({})

const messages = shallowRef<Message[]>([])
const gameState = shallowRef<GameState>()
const connected = ref(false)
const isLoading = ref(true)

function addMessage(msg: Message) {
  messages.value = [...messages.value, msg]
}

function addSystemMessage(text: string) {
  messages.value = [...messages.value, {username: "System", text, timestamp: now().valueOf()}]
}

function onConnect(s: WebSocket) {
  socket.value?.close()
  socket.value = s
  messages.value = []
  s.onopen = () => {
    connected.value = true
  }
  s.onclose = () => {
    console.log("connection closed")
    connected.value = false
    gameState.value = undefined
  }
  s.onmessage = ({data}: MessageEvent<string>) => {
    if (data === "GAME_ALREADY_STARTED") {
      addSystemMessage("User name already taken, please choose another one")
      s.close()
      return
    }
    if (data === "NAME_ALREADY_USED") {
      addSystemMessage("User name already taken, please choose another one")
      s.close()
      return
    }
    const parsed = JSON.parse(data) as Event
    console.log(parsed)
    switch (parsed.type) {
      case "ChatMessage":
        addMessage(parsed.value)
        break
      case "LobbyEvent":
        const {username, state} = parsed.value
        switch (state) {
          case "CONNECT": // same as unready
          case "UNREADY":
          case "READY":
            players.value[username] = state === "READY"
            break
          case "DISCONNECT":
            delete players.value[username]
            break
        }
        addMessage({timestamp: now().valueOf(), username, text: state})
        break
      case "GameState":
        gameState.value = parsed.value
        isLoading.value = false
        break
      case "ConnectedPlayers":
        players.value = parsed.value
        break
    }
  }
}

function sendChatMessage(text: string) {
  socket.value?.send(JSON.stringify({type: "ChatMessage", value: text}))
}

function setReady(ready: boolean) {
  socket.value?.send(JSON.stringify({type: "LobbyCommand", value: ready ? "READY" : "UNREADY"}))
}

function sendAction(action: Action) {
  socket.value?.send(JSON.stringify({type: "Action", value: action}))
  isLoading.value = socket.value !== undefined
}

useEventListener("unload", () => socket.value?.close())

</script>

<template>
  <div>
    <div class="layout">
      <Login v-if="!connected" v-model:username="username" @connected="onConnect"/>
      <Lobby v-else-if="gameState===undefined" :players="players" @ready="setReady($event)"/>
      <Game v-else v-bind="gameState" @action="sendAction($event)" :is-loading/>
      <Chat class="chat" :messages="messages" @send="sendChatMessage" />
    </div>
  </div>
</template>

<style scoped>
.layout {
  display: grid;
  width: 100%;
  height: 100%;
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
