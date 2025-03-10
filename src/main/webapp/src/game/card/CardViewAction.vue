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
</script>

<template>
  <div
    class="card-view-action"
    v-element-hover="(v)=>hovered=v"
    v-on-click-outside="()=>clicked=false"
    @click="()=>clicked=true"
  >
    <div>
      <Transition>
        <ActionDisplay
          v-show="focused"
          class="actions"
          :actions
          @selected="$emit('action',$event)"
        />
      </Transition>
    </div>
    <CardView :card :focused class="card-view clickable"/>
  </div>
</template>

<style scoped>
.card-view-action {
  display: grid;
  grid-template-rows: 2rem 1fr;
  pointer-events: auto;
}

.card-view {
  cursor: pointer;
  transition: all 0.25s;
}

.card-view[focused=true] {
  transform: translateY(0.5rem);
}

.v-enter-active,
.v-leave-active {
  transition: opacity 0.25s ease-in-out;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}
</style>
