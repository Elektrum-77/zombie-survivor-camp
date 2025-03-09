import type { Resource } from "@/game/ResourceBank.ts";
import type { ActionType } from "@/game/action/Action.ts";

export const ICON_POWER_GENERATOR = "mdi:electricity-generator"

export const ICON_ACTION: Record<ActionType, string> = {
  Discard: "mdi:trash",
  Construct: "mdi:hammer-screwdriver",
  Search: "mdi:search",
  CancelSearch: "mdi:trash",
  DestroyBuilding: "mdi:trash",
  SendZombie: "mdi:upload",
  UpgradeBuilding: "mdi:office-building-plus",
}
export const ICON_ACTION_CONSTRUCT = ICON_ACTION["Construct"]
export const ICON_ACTION_SEARCH = ICON_ACTION["Search"]


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
