<script setup lang="ts">
import { ref, shallowRef, watch } from 'vue';
import Login from "@/Login.vue";
import Chat, { type Message } from "@/Chat.vue";
import { now, useEventListener } from "@vueuse/core";
import Lobby from "@/Lobby.vue";
import type { Action, GameState } from "@/game.ts";


const socket = shallowRef<WebSocket>()
const username = ref(localStorage.getItem("username") ?? "")
watch(username, v => localStorage.setItem("username", v))

const players = ref<Record<string, { username: string, isReady: boolean }>>({})

const messages = shallowRef<Message[]>([])
const gameStarted = ref<boolean>(false)
const gameState = shallowRef<GameState>()

function addMessage(msg: Message) {
  messages.value = [...messages.value, msg]
}

type LobbyEvent = {
  player: string
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
  type: "GameStateWrapper"
  value: { state: GameState }
} | {
  type: "GameProgress"
  value: GameProgress
}

function onConnect(s: WebSocket) {
  socket.value?.close()
  socket.value = s
  messages.value = []
  s.onopen = () => {
    s.send(JSON.stringify({type: "LobbyCommand", value: "READY"}))
  }
  s.onclose = () => {
    console.log("connection closed")
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
        const {player, state} = parsed.value
        switch (state) {
          case "CONNECT":
            players.value[player] = {username: player, isReady: false,}
            break
          case "DISCONNECT":
            delete players.value[player]
            break
          case "READY":
            players.value[player].isReady = true
            break
          case "UNREADY":
            players.value[player].isReady = false
            break
        }
        addMessage({timestamp: now().valueOf(), username: player, text: state})
        break
      case "GameProgress":
        if (parsed.value === "STARTING") {
          gameStarted.value = true
        }
        break
      case "GameStateWrapper":
        gameState.value = parsed.value.state
        break
    }
  }
}

function sendChatMessage(text: string) {
  socket.value?.send(JSON.stringify({type: "ChatMessage", value: {text}}))
}

function sendAction(action: Action) {
  socket.value?.send(JSON.stringify({type: "ActionWrapper", value: {action}}))
}

useEventListener("unload", () => socket.value?.close())

</script>

<template>
  <Lobby v-if="gameState===undefined" :players="Object.values(players)"/>
<!--  <Game v-else/>-->
  <div class="chat">
    <Login v-model:username="username" @connected="onConnect"/>
    <Chat :messages="messages" @send="sendChatMessage"/>
  </div>
</template>

<style scoped>
.chat {
  max-width: fit-content;
  margin-left: auto;
}
</style>
