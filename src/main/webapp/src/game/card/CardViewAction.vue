<script setup lang="ts">
import type { CardUnion } from "@/game/card/type.ts";
import { computed, ref } from "vue";
import CardView from "@/game/card/CardView.vue";
import type { Action } from "@/game/action/Action.ts";
import { vElementHover, vOnClickOutside } from "@vueuse/components";
import ActionDisplay from "@/game/action/ActionDisplay.vue";

const {actions} = defineProps<{ card: CardUnion, actions: Action[] }>()
defineEmits<{action: [Action]}>()
const hovered = ref(false)
const clicked = ref(false)
const focused = computed(() => hovered.value || clicked.value)
const log = console.log;
</script>

<template>
  <div
    class="card-view-action"
    v-element-hover="(v)=>hovered=v"
    v-on-click-outside="()=>clicked=false"
    @click="()=>clicked=true"
  >
    <div>
      <Transition name="fade">
        <ActionDisplay v-show="focused" :actions @selected="$emit('action', $event)"/>
      </Transition>
    </div>
    <CardView :card :focused class="card-view"/>
  </div>
</template>

<style scoped>
.card-view-action {
  display: grid;
  grid-template-rows: 2rem 1fr;
}

.card-view {
  cursor: pointer;
  transition: all 0.25s;
}

.card-view[focused=true] {
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
