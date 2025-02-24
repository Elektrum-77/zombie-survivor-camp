import type { ResourceBank } from "@/game/ResourceBank.ts";

export type Card = {
  type: "Building"
  value: Building
} | {
  type: "Dummy"
  value: Dummy
}

export type CardType = Card["type"]

export type Dummy = {name: "TEST"}
export type Building = {
  name: string
  cost: ResourceBank
  production: ResourceBank
  search: ResourceBank
}
