<script setup lang="ts">
import type { ResourceBank } from "@/game/ResourceBank.ts";
import ResourceBankView from "@/game/ResourceBankView.vue";
import { Icon } from "@iconify/vue"

defineProps<{
  icon: string
  iconColor?: string
  bank?: ResourceBank
  color?: string
  negativeColor?: string
  opposite?: boolean
  skeleton?: boolean
}>()
</script>

<template>
  <div class="row" style="gap: 0;">
    <Icon :icon :style="iconColor ? {'--color': iconColor} : undefined"/>
    <template v-if="bank && !skeleton">
      <span>:</span>
      <ResourceBankView :bank :opposite :color :negative-color/>
    </template>
    <span v-else>: ...</span>
  </div>
</template>

<style scoped>
span {
  font-weight: bold;
}
</style>
