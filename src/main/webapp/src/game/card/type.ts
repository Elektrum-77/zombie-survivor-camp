import type { ResourceBank } from "@/game/ResourceBank.ts";
export type Card = {
  Dummy: { name: string }
  Zombie: {
    name: string
    count: number
  }
  Building: {
    name: string
    cost: ResourceBank
    production: ResourceBank
    search: ResourceBank
    category?: string
    electrified?: ResourceBank
  }
  Upgrade: {
    name: string
    cost: ResourceBank
    production: ResourceBank
    isPowerGenerator: boolean
  }
}

export type CardType = keyof Card
export type CardByType = { [k in keyof Card]: { type: k; value: Card[k] } }
export type CardUnion = CardByType[keyof Card]

export type DummyCard = CardByType["Dummy"]
export type BuildingCard = CardByType["Building"]
export type UpgradeCard = CardByType["Upgrade"]
export type ZombieCard = CardByType["Zombie"]
export type Dummy = Card["Dummy"]
export type Building = Card["Building"]
export type Upgrade = Card["Upgrade"]
export type Zombie = Card["Zombie"]
