<script setup lang="ts">
import { computed, shallowRef } from "vue";
import type { Action, ActionType } from "@/game/action/Action.ts";
import { ICON_ACTION } from "@/assets/icon.ts";
import { Icon } from "@iconify/vue";
import Modal from "@/shared/Modal.vue";

const {actions} = defineProps<{ actions: Action[] }>()
defineEmits<{ selected: [Action] }>()
const actionByType = computed(() => {
  const result = {} as Record<ActionType, Action[]>
  actions.forEach(action => {
    result[action.type] = result[action.type] || []
    result[action.type].push(action)
  })
  return result
})

const multipleChoiceActions = shallowRef<Action[]>([])

function isRed(type: ActionType): boolean {
  return type === "DestroyBuilding"
    || type === "CancelSearch"
    || type === "Discard"
}
</script>

<template>
  <div class="row gap center-content">
    <Icon
      v-for="(list, type) in actionByType"
      :style="isRed(type) ? {'--color': 'red'} : undefined"
      style="width: 2rem; height: 2rem;"
      @click="() => list.length > 1 ? multipleChoiceActions = list : $emit('selected', list[0])"
      class="tilt-shaking-on-hover clickable circle action-type"
      :icon="ICON_ACTION[type]"
    />
    <Modal
      :show="multipleChoiceActions.length > 1"
      @close="multipleChoiceActions = []"
    >
      <ul class="action-list">
        <li
          v-for="(action, i) in multipleChoiceActions" :key="i"
          v-text="JSON.stringify(action)"
          @click="() => {
            $emit('selected', action)
            multipleChoiceActions = []
          }"
        />
      </ul>
    </Modal>
  </div>
</template>

<style scoped>

.action-type {
  position: relative;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;

  &:hover {
    top: -.5rem;
  }
}

.action-list {
  background-color: white;
  padding: 1rem;
  border-radius: 1rem;
  list-style: none;
}

.action-list > li:hover {
  transform: translateX(0.5rem) scale(1.05);
}
</style>
