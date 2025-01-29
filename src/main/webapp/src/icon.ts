import type { Resource } from "@/ResourceBank.ts";

export const ICON_ACTION_BUILD = "mdi:hammer-screwdriver"
export const ICON_ACTION_SEARCH = "mdi:search"

export const ICON_RESOURCES: Record<Resource, string> = {
  FOOD: "mdi:pizza",
  WATER: "mdi:water",
  PEOPLE: "mdi:user",
  FUEL: "mdi:fuel",
  MATERIALS: "mdi:screw-lag",
  MEDICINES: "mdi:medication",
  MILITARY: "mdi:gun"
} as const
