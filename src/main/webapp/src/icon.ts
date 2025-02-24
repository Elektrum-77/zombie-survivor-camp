import type { Resource } from "@/game/ResourceBank.ts";
import type { ActionType } from "@/game/Action.ts";

export const ICON_ACTION_CONSTRUCT = "mdi:hammer-screwdriver"
export const ICON_ACTION_SEARCH = "mdi:search"

export const ICON_ACTION: Record<ActionType, string> = {
  Construct: ICON_ACTION_CONSTRUCT,
  Search: ICON_ACTION_SEARCH,
  CancelSearch: "mdi:trash",
  DestroyBuilding: "mdi:trash",
}

export const ICON_RESOURCES: Record<Resource, string> = {
  FOOD: "mdi:pizza",
  WATER: "mdi:water",
  PEOPLE: "mdi:user",
  FUEL: "mdi:fuel",
  MATERIALS: "mdi:screw-lag",
  MEDICINES: "mdi:medication",
  MILITARY: "mdi:gun",
  AMMUNITION: "mdi:bullets"
} as const
