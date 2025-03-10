<script setup lang="ts">
import type { Camp } from "@/game/game.ts";
import type { Action } from "@/game/action/Action.ts";
import CampStats from "@/game/camp/CampStats.vue";
import UpgradableBuildingView from "@/game/camp/UpgradableBuildingView.vue";
import { ICON_ACTION } from "@/assets/icon.ts";
import { ref } from "vue";
import { vOnClickOutside } from "@vueuse/components";
import CardWithIcon from "@/game/card/CardWithIcon.vue";
import BuildingView from "@/game/card/BuildingView.vue";
import { Icon } from "@iconify/vue";

defineProps<{ camp: Camp }>()
const emit = defineEmits<{ action: Action[] }>()
const isRemoving = ref(false)

type Category = "Buildings" | "Searches"
const selected = ref<Category>("Buildings")
const actionNames: Record<Category, string> = {
  Buildings: "Click here to destroy a building",
  Searches: "Click here to cancel a search",
}
const actionIcons = {
  Buildings: ICON_ACTION["Construct"],
  Searches: ICON_ACTION["Search"],
} as const

function onClicked(index: number) {
  if (!isRemoving.value) {
    return
  }
  isRemoving.value = false

  switch (selected.value) {
    case "Buildings":
      emit('action', {type: "DestroyBuilding", value: {index}})
      break;
    case "Searches":
      emit('action', {type: "CancelSearch", value: {index}})
      break;
  }
}
</script>

<template>
  <div>
    <CampStats :camp/>
    <div class="bordered rounded padded" style="border-style: dashed">
      <div class="row" style="margin: 0 auto; justify-content: center; gap: 1rem;">
        <div
          v-for="c in (['Buildings', 'Searches'] as const)"
          class="row center-items clickable rounded"
          :focused="selected === c"
          style="width: fit-content; height: fit-content;"
          @click="() => {selected = c; isRemoving = false}"
        >
          <h1 style="width: fit-content" v-text="c" />
          <Icon :icon="actionIcons[c]" height="3rem"/>
        </div>
      </div>
      <div
        class="padded container gap"
        v-on-click-outside="() => isRemoving = false"
      >
        <CardWithIcon
          :name="actionNames[selected]"
          :icon="isRemoving ? 'mdi:trash-can-empty' : 'mdi:trash'"
          :style="isRemoving ? {'--color': 'red'} : undefined"
          :class="isRemoving ? 'tilt-shaking-loop' : undefined"
          @click="isRemoving =! isRemoving"
          class="clickable"
          :focused="isRemoving"
        />
        <template v-if="selected==='Buildings'">
          <UpgradableBuildingView
            v-for="({building, upgrades}, index) in camp.buildings"
            :building
            :upgrades
            :class="{clickable: isRemoving}"
            @click="()=>onClicked(index)"
          />
          <CardWithIcon
            v-for="_ in (camp.maxBuildCount - camp.buildings.length)"
            name="Available construction space"
            :icon="ICON_ACTION['Construct']"
          />
        </template>
        <template v-else-if="selected==='Searches'">
          <BuildingView
            v-for="({value:building}, index) in camp.searches"
            :key="building.name"
            v-bind="building"
            :class="{clickable: isRemoving}"
            @click="()=>onClicked(index)"
          />
          <CardWithIcon
            v-for="_ in (camp.production.PEOPLE??0)"
            name="Available search team"
            :icon="ICON_ACTION['Search']"
          />
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(var(--card-width), 1fr));
  justify-content: space-around;
}
</style>
