<script setup lang="ts">
import type { Camp, Hand } from "@/game/game.ts";
import type {Action} from "@/game/Action.ts";
import CardViewAction from "@/game/card/CardViewAction.vue";

const {hand} = defineProps<{ camp: Camp, hand: Hand }>()
defineEmits<{ action: [Action] }>()
</script>

<template>
  <div class="hand-layout row">
    <CardViewAction
      v-for="{card, actions} in hand"
      :card
      :actions
      @action="$emit('action', $event)"/>
  </div>
</template>

<style scoped>
</style>
