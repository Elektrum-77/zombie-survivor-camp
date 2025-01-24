export type Resource = "PEOPLE" | "WATER" | "FOOD" | "MEDICINES" | "MILITARY" | "MATERIALS" | "FUEL"
export type ResourceBank = Record<Resource, number | undefined>

export type Camp = {
  production: ResourceBank
}

export type GameState = {
  hand: any[]
  camps: Record<string, {}>
}

export type Card = {
  name: string
  production: ResourceBank
}

export type Action = {
  type: "Build"
  value: {
    index: number
  }
}
