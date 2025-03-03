<script setup lang="ts">
import type { Card } from "@/game/card/Card.ts";
import { computed, ref } from "vue";
import CardView from "@/game/card/CardView.vue";
import type { Action } from "@/game/action/Action.ts";
import { vElementHover, vOnClickOutside } from "@vueuse/components";
import ActionDisplay from "@/game/action/ActionDisplay.vue";

const {actions} = defineProps<{ card: Card, actions: Action[] }>()
defineEmits<{action: [Action]}>()
const hovered = ref(false)
const clicked = ref(false)
const focused = computed(() => hovered.value || clicked.value)
</script>

<template>
  <div class="card-container" v-element-hover="v=>hovered=v">
    <div>
      <Transition name="fade">
        <ActionDisplay
          v-if="focused"
          :actions
          @selected="$emit('action', $event)"
        />
      </Transition>
    </div>
    <CardView
      v-on-click-outside="()=>clicked=false"
      :card
      :focused
      class="card"
      @click="clicked=true"
    />
  </div>
</template>

<style scoped>
.card-container {
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
