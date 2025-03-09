<script setup lang="ts">
import CardLayout from "@/game/card/CardLayout.vue";
import type { Upgrade } from "@/game/card/type.ts";
import CardImage from "@/game/card/CardImage.vue";
import { ICON_ACTION, ICON_POWER_GENERATOR } from "@/assets/icon.ts";
import { Icon } from "@iconify/vue"
import CardStat from "@/game/card/CardStat.vue";
import CardName from "@/game/card/CardName.vue";


defineProps<Upgrade>()
</script>

<template>
  <CardLayout>
    <CardName :name/>
    <CardImage :name/>
    <CardStat :icon="ICON_ACTION['UpgradeBuilding']" icon-color="red" :bank="cost" opposite/>

    <div class="row" style="justify-content: space-between">
      <CardStat :icon="ICON_ACTION['UpgradeBuilding']" :bank="production"/>
      <Icon
        v-if="isPowerGenerator"
        :icon="ICON_POWER_GENERATOR"
        style="--color: lightseagreen"
      />
    </div>
  </CardLayout>
</template>

<style scoped>

</style>
