<script setup lang="ts">
import type { Card } from "@/card/Card.ts";
import { computed, ref } from "vue";
import ActionSelector from "@/ActionSelector.vue";
import type { Camp } from "@/game.ts";
import CardView from "@/card/CardView.vue";
import type { HandCardAction } from "@/Action.ts";
import { vElementHover, vOnClickOutside } from "@vueuse/components";

defineProps<{ camp: Camp, card: Card, index:number }>()
defineEmits<{action: [HandCardAction]}>()
const hovered = ref(false)
const clicked = ref(false)
const focused = computed(()=>hovered.value||clicked.value)
</script>

<template>
  <div class="col" v-element-hover="v=>hovered=v">
    <ActionSelector
      class="action-selector"
      v-if="focused"
      :camp
      :card
      :index
      @action="$emit('action', $event)"
    />
    <CardView
      v-bind="card"
      @click="clicked=true"
      v-on-click-outside="()=>clicked=false"
      class="card"
      :focused="focused"
    />
  </div>
</template>

<style scoped>


.card {
  cursor: pointer;
  transition: all 0.25s;
}

.card[focused=true] {
  transform: scale(1.05);
  box-shadow: 0 0 32px rgba(0, 0, 0, 0.1);
}
</style>
