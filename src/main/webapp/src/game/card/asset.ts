import camping_tents from "@/assets/cards/camping_tents.webp"
import rain_collectors from "@/assets/cards/rain_collectors.webp"
import vegetable_gardens from "@/assets/cards/vegetable_gardens.webp"
import ammunition_press from "@/assets/cards/ammunition_workbench.webp"
import hunter_tent from "@/assets/cards/hunter_tent.webp"
import medical_tent from "@/assets/cards/medical_tent.webp"
import training_yard from "@/assets/cards/training_yard.webp"
import logging_yard from "@/assets/cards/logging_yard.webp"
import watch_tower from "@/assets/cards/watch_tower.webp"
import well from "@/assets/cards/well.webp"
import infested_gas_station from "@/assets/cards/infested_gas_station.webp"
import infested_police_station from "@/assets/cards/infested_police_station.webp"
import infested_super_market from "@/assets/cards/infested_super_market.webp"
import infested_house from "@/assets/cards/infested_house.webp"

export const CARD_IMG = {
  camping_tents,
  rain_collectors,
  vegetable_gardens,
  ammunition_press,
  hunter_tent,
  training_yard,
  logging_yard,
  well,
  medical_tent,
  watch_tower,
  infested_gas_station,
  infested_police_station,
  infested_super_market,
  infested_house,
} as const

export function isCardImage(name: string): name is keyof typeof CARD_IMG {
  return name in CARD_IMG
}
