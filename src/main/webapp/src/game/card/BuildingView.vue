<script setup lang="ts">
import type { Building } from "@/game/card/type.ts";
import { ICON_ACTION_CONSTRUCT, ICON_ACTION_SEARCH, ICON_POWER_GENERATOR } from "@/assets/icon.ts";
import CardLayout from "@/game/card/CardLayout.vue";
import CardImage from "@/game/card/CardImage.vue";
import CardStat from "@/game/card/CardStat.vue";
import CardName from "@/game/card/CardName.vue";

defineProps<Building>()
</script>

<template>
  <CardLayout>
    <div class="building">
      <CardName :name />
      <CardStat :icon="ICON_ACTION_SEARCH" :bank="search" />
      <CardImage :name />
      <CardStat :icon="ICON_ACTION_CONSTRUCT" :bank="cost" icon-color="red" opposite />
      <CardStat
        v-if="electrified"
        :icon="ICON_POWER_GENERATOR"
        :bank="electrified"
        icon-color="lightseagreen"
      />
      <CardStat :icon="ICON_ACTION_CONSTRUCT" :bank="production" />
    </div>
  </CardLayout>
</template>

<style scoped>
.building {
  max-width: 100%;
  max-height: 100%;
  display: grid;
  grid-template-rows: 2rem 2rem minmax(0, 1fr) 2rem 2rem 2rem;
}
</style>
