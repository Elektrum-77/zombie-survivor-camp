export type Resource = "PEOPLE" | "WATER" | "FOOD" | "MEDICINES" | "MILITARY" | "MATERIALS" | "FUEL"
export type ResourceBank = Record<Resource, number | undefined>

export type GameState = {
  hand: Card[]
  camps: Record<string, Camp>
}

export type Action = {
  type: "Construct"
  value: {
    index: number
  }
} | {
  type: "Search"
  value: {
    index: number
  }
}

export type ActionType = Action["type"]

export type LobbyPlayer = {
  username: string
  isReady: boolean
}

export type Camp = {
  maxBuildCount: number
  buildings: Card[]
  searches: Card[]
}

export type Card = {
  type: "Building"
  value: {
    name: string
    cost: ResourceBank
    production: ResourceBank
    search: ResourceBank
  }
}
