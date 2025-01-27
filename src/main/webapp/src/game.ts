import type { Message } from "@/Chat.vue";
import type { Card } from "@/card/Card.ts";
import type { ResourceBank } from "@/ResourceBank.ts";

export type GameState = {
  hand: Card[]
  camps: Record<string, Camp>
}

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

type LobbyEvent = {
  username: string
  state: "CONNECT" | "DISCONNECT" | "READY" | "UNREADY"
}

export type Event = {
  type: "ChatMessage"
  value: Message
} | {
  type: "LobbyEvent"
  value: LobbyEvent
} | {
  type: "GameState"
  value: GameState
} | {
  type: "ConnectedPlayerList",
  value: LobbyPlayer[]
}
