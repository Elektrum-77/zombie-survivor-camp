import type { Message } from "@/Chat.vue";
import type { Card } from "@/card/Card.ts";
import type { ResourceBank } from "@/ResourceBank.ts";
import type { Action } from "@/Action.ts";
import { ref, shallowRef } from "vue";
import { now } from "@vueuse/core";

export type GameState = {
  hand: { card: Card, actions: [Action] }[]
  camps: Record<string, Camp>
  currentPlayer: string
}
export type Hand = GameState["hand"]

export type LobbyPlayer = {
  username: string
  isReady: boolean
}

export type Camp = {
  maxBuildCount: number
  availableSpace: number
  isSpaceAvailable: boolean
  searchCost: ResourceBank
  production: ResourceBank
  buildings: readonly Card[]
  searches: readonly Card[]
}

export type LobbyEvent = { type: "LobbyEvent"; value: LobbyEventValue }
export type LobbyEventValue = {
  state: "CONNECT" | "DISCONNECT" | "READY" | "UNREADY"
  username: string
}
export type GameEvent = TurnUpdateEvent | TurnStartEvent | TurnEndEvent
export type TurnUpdateEvent = { type: "TurnUpdate"; value: GameState }
export type TurnStartEvent = { type: "TurnStart"; value: GameState }
export type TurnEndEvent = { type: "TurnEnd"; value: GameState }
export type ChatEvent = { type: "ChatMessage"; value: Message }
export type ConnectedEvent = { type: "ConnectedPlayers"; value: string[] }

export type Event = ChatEvent | LobbyEvent | GameEvent | ConnectedEvent

export type EventType = Event["type"]

export function useLobby() {
  const players = ref<Record<string, boolean>>({})

  function onMessage(event: LobbyEvent | ConnectedEvent) {
    switch (event.type) {
      case "ConnectedPlayers":
        players.value = Object.fromEntries(event.value.map(p => [p, false]))
        return
      case "LobbyEvent":
        const {state, username} = event.value
        switch (state) {
          case "CONNECT":
          case "UNREADY":
            players.value[username] = false
            return
          case "READY":
            players.value[username] = true
            return
          case "DISCONNECT":
            delete players.value[username]
            return
        }
    }
  }

  return {players, onMessage,}
}

export function useGame() {
  const state = ref<GameState>()
  const players = ref<Record<string, boolean>>({})
  const isTurnDone = ref<boolean>(false)

  function onMessage(event: GameEvent): GameState {
    switch (event.type) {
      case "TurnUpdate": return state.value = event.value
      case "TurnStart":
        isTurnDone.value = false
        return state.value = event.value
      case "TurnEnd":
        isTurnDone.value = true
        return state.value = event.value
    }
  }

  return {state, players, onMessage,}
}

export function useChat() {
  const messages = shallowRef<Message[]>([])

  function addMessage(msg: Message) {
    messages.value = [...messages.value, msg]
  }

  function addSystemMessage(text: string) {
    addMessage({username: "System", text, timestamp: now().valueOf()})
  }

  return {messages, addMessage, addSystemMessage,}
}
