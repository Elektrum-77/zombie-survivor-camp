<script setup lang="ts">

import type { CardType, CardUnion } from "@/game/card/type.ts";
import BuildingView from "@/game/card/BuildingView.vue";
import DummyView from "@/game/card/DummyView.vue";
import UpgradeView from "@/game/card/UpgradeView.vue";
import ZombieView from "@/game/card/ZombieView.vue";

type Component = typeof DummyView | typeof ZombieView | typeof UpgradeView | typeof BuildingView
const components: {[k in CardType]: Component} = {
  Dummy: DummyView,
  Building: BuildingView,
  Upgrade: UpgradeView,
  Zombie: ZombieView,
}

defineProps<{ card: CardUnion }>()
</script>

<template>
  <Component :is="components[card.type]" v-bind="card.value"/>
</template>

<style scoped>

</style>
