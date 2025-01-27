<script setup lang="ts">
import { ref, shallowRef, watch } from 'vue';
import Login from "@/Login.vue";
import Chat, { type Message } from "@/Chat.vue";
import { now, useEventListener } from "@vueuse/core";
import Lobby from "@/Lobby.vue";
import Game from "@/Game.vue";
import type { Action, GameState, LobbyPlayer } from "@/game.ts";


const socket = shallowRef<WebSocket>()
const username = ref(localStorage.getItem("username") ?? "")
watch(username, v => localStorage.setItem("username", v))

const players = ref<Record<string, LobbyPlayer>>({})

const connected = shallowRef<boolean>(false)
const messages = shallowRef<Message[]>([])
const gameStarted = ref<boolean>(false)
const gameState = shallowRef<GameState>()

function addMessage(msg: Message) {
  messages.value = [...messages.value, msg]
}

type LobbyEvent = {
  username: string
  state: "CONNECT" | "DISCONNECT" | "READY" | "UNREADY"
}
type GameProgress = "STARTING" | "FINISHED"
type Event = {
  type: "ChatMessage"
  value: Message
} | {
  type: "LobbyEvent"
  value: LobbyEvent
} | {
  type: "GameState"
  value: GameState
} | {
  type: "GameProgress"
  value: GameProgress
} | {
  type: "ConnectedPlayerList",
  value: LobbyPlayer[]
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
    if (data === "NAME_ALREADY_USED") {
      addMessage({
        username: "system",
        text: "User name already taken, please choose another one",
        timestamp: now().valueOf(),
      })
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
          case "CONNECT":
            players.value[username] = {username, isReady: false,}
            break
          case "DISCONNECT":
            delete players.value[username]
            break
          case "READY":
            players.value[username].isReady = true
            break
          case "UNREADY":
            players.value[username].isReady = false
            break
        }
        addMessage({timestamp: now().valueOf(), username, text: state})
        break
      case "GameProgress":
        if (parsed.value === "STARTING") {
          gameStarted.value = true
        }
        break
      case "GameState":
        gameState.value = parsed.value
        break
      case "ConnectedPlayerList":
        const alreadyConnectedPlayers = parsed.value.reduce(
          (acc: Record<string, LobbyPlayer>, player) => {
          acc[player.username] = player
          return acc
        }, {})
        players.value = {...players.value, ...alreadyConnectedPlayers}
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
}

useEventListener("unload", () => socket.value?.close())

</script>

<template>
  <div>
    <div class="layout">
      <Login v-if="!connected" v-model:username="username" @connected="onConnect"/>
      <Lobby v-else-if="gameState===undefined" :players="Object.values(players)" @ready="setReady($event)"/>
      <Game v-else
            :hand="gameState.hand"
            :camps="gameState.camps"
            :username="username"
            @action="sendAction($event)"
      />
      <Chat class="chat" :messages="messages" @send="sendChatMessage" />
    </div>
  </div>
</template>

<style scoped>
.layout {
  display: grid;
  min-width: 100vw;
  min-height: 100vh;
  grid-template-columns: 1fr 20vw;
  background-color: #f8f8f8;
  padding: 16px;
}

.chat {
  padding: 8px;
  transition: all 0.5s;
}

.chat:focus-within {
  border-left: 1px solid #ccc;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
  background-color: white;
}
</style>
