import type { Camp } from "@/game/game.ts";
import type { Card } from "@/game/card/Card.ts";
import { bankContainsAll } from "@/game/ResourceBank.ts";

export type Action = {
  type: "Construct"
  value: {
    index: number
  }
} | {
  type: "AddZombie"
  value: {
    username: string
    index: number
  }
} | {
  type: "Search"
  value: {
    index: number
  }
} | {
  type: "DestroyBuilding"
  value: {
    index: number
  }
} | {
  type: "CancelSearch"
  value: {
    index: number
  }
}

export type ActionType = Action["type"]

export function isSearchable(camp: Camp, card: Card): boolean {
  switch (card.type) {
    case "Building":
      return bankContainsAll(camp.production, camp.searchCost)
    case "Dummy":
      return false
  }
}

export function isBuildable(camp: Camp, card: Card): boolean {
  if (!camp.isSpaceAvailable) return false
  switch (card.type) {
    case "Building":
      return bankContainsAll(camp.production, card.value.cost)
    case "Dummy":
      return false
  }
}
