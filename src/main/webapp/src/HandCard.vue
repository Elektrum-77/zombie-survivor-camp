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
  <div class="hand-card" v-element-hover="v=>hovered=v">
    <div>
      <Transition name="fade">
        <ActionSelector
          class="action-selector"
          v-if="focused"
          :camp
          :card
          :index
          @action="$emit('action', $event)"
        />
      </Transition>
    </div>
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
.hand-card {
  display: grid;
  grid-template-rows: 2rem 1fr;
}

.card {
  cursor: pointer;
  transition: all 0.25s;
}

.card[focused=true] {
  transform: translateY(0.5rem);
  box-shadow: 0 0 32px rgba(0, 0, 0, 0.1);
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.25s ease-in-out;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
