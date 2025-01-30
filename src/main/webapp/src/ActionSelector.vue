<script setup lang="ts">
import type { Card } from "@/card/Card.ts";
import type { Camp } from "@/game.ts";
import { type HandCardAction, isBuildable, isSearchable } from "@/Action.ts";
import { Icon } from "@iconify/vue";
import { ICON_ACTION_BUILD, ICON_ACTION_SEARCH } from "@/icon.ts";

defineProps<{ camp: Camp, card: Card, index: number }>()
defineEmits<{ action: [HandCardAction] }>()
</script>

<template>
  <div class="row">
    <Icon
      v-if="isBuildable(camp, card)"
      @click="$emit('action', { type: 'Construct', value: { index } })"
      :icon="ICON_ACTION_BUILD"
      width="2rem"
      height="2rem"
    />
    <Icon
      v-if="isSearchable(camp, card)"
      @click="$emit('action', { type: 'Search', value: { index } })"
      :icon="ICON_ACTION_SEARCH"
      width="2rem"
      height="2rem"
    />
    <!--
    <span
      v-if="isBuildable(camp, card)"
      @click="$emit('action', { type: 'Construct', value: { index } })">
      Construire
    </span>
    <span
      v-if="isSearchable(camp, card)"
      @click="$emit('action', { type: 'Search', value: { index } })">
      Fouiller
    </span>
    -->
  </div>
</template>

<style scoped>
div {
  align-items: stretch;
  justify-content: center;
}

div > * {
  cursor: pointer;
  transition: all 0.25s;
}

div > *:hover {
  transform: translateY(-0.5rem);
}
</style>
