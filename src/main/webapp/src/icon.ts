import type { Resource } from "@/ResourceBank.ts";
import type { ActionType, HandCardAction } from "@/Action.ts";

export const ICON_ACTION_CONSTRUCT = "mdi:hammer-screwdriver"
export const ICON_ACTION_SEARCH = "mdi:search"

export const ICON_ACTION: Record<ActionType, string> = {
  Construct: ICON_ACTION_CONSTRUCT,
  Search: ICON_ACTION_SEARCH,
  CancelSearch: "mdi:trash",
  DestroyBuilding: "mdi:trash",
}

export const ICON_HAND_CARD_ACTION: Record<HandCardAction["type"], string> = {
  Construct: ICON_ACTION_CONSTRUCT,
  Search: ICON_ACTION_SEARCH,
} as const

export const ICON_RESOURCES: Record<Resource, string> = {
  FOOD: "mdi:pizza",
  WATER: "mdi:water",
  PEOPLE: "mdi:user",
  FUEL: "mdi:fuel",
  MATERIALS: "mdi:screw-lag",
  MEDICINES: "mdi:medication",
  MILITARY: "mdi:gun"
} as const
