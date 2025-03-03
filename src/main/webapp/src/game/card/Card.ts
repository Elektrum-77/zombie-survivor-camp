import type { ResourceBank } from "@/game/ResourceBank.ts";

export type Card = BuildingCard | UpgradeCard | ZombieCard | DummyCard

export type CardType = Card["type"]

export type DummyCard = {
  type: "Dummy"
  value: Dummy
}
export type Dummy = {name: "TEST"}

export type BuildingCard = {
  type: "Building"
  value: Building
}
export type Building = {
  name: string
  cost: ResourceBank
  production: ResourceBank
  search: ResourceBank
  category?: string
  electrified?: ResourceBank
}

export type UpgradeCard = {
  type: "Upgrade"
  value: Upgrade
}
export type Upgrade = {
  name: string
  cost: ResourceBank
  production: ResourceBank
  isPowerGenerator: boolean
}

export type ZombieCard = {
  type: "Zombie"
  value: Zombie
}
export type Zombie = {
  name: string
  count: number
}
