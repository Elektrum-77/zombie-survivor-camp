export type Action =
  | DiscardAction
  | ConstructAction
  | SendZombieAction
  | SearchAction
  | DestroyBuildingAction
  | CancelSearchAction
  | UpgradeBuildingAction

export type DiscardAction = { type: "Discard", value: { index: number } }
export type ConstructAction = { type: "Construct", value: { index: number } }
export type SearchAction = { type: "Search", value: { index: number } }
export type DestroyBuildingAction = { type: "DestroyBuilding", value: { index: number } }
export type CancelSearchAction = { type: "CancelSearch", value: { index: number } }
export type SendZombieAction = { type: "SendZombie", value: { index: number, username: string } }
export type UpgradeBuildingAction = {
  type: "UpgradeBuilding",
  value: { upgradeIndex: number, buildingIndex: string }
}

export type ActionType = Action["type"]
