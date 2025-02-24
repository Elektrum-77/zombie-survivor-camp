export type Resource = "PEOPLE" | "WATER" | "FOOD" | "MEDICINES" | "MILITARY" | "AMMUNITION" | "MATERIALS" | "FUEL"
export type ResourceBank = { [r in Resource]?: number }

export function bankContainsAll(bank: ResourceBank, other: ResourceBank) {
  for (const r in other) {
    if ((bank[r as Resource] ?? 0) < (other[r as Resource] ?? 0)) {
      return false
    }
  }
  return true
}
